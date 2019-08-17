/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.channel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.LunaChatLogger;
import com.github.ucchyocean.lc.NGWordAction;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.bridge.DynmapBridge;
import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelMessageEvent;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * チャンネルの実装クラス
 * @author ucchy
 */
public class ChannelImpl extends Channel {

    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";

    private static final String INFO_FIRSTLINE = Resources.get("channelInfoFirstLine");
    private static final String INFO_PREFIX = Resources.get("channelInfoPrefix");
    private static final String INFO_ALIAS = Resources.get("channelInfoAlias");
    private static final String INFO_GLOBAL = Resources.get("channelInfoGlobal");
    private static final String INFO_BROADCAST = Resources.get("channelInfoBroadcast");
    private static final String INFO_SECRET = Resources.get("channelInfoSecret");
    private static final String INFO_PASSWORD = Resources.get("channelInfoPassword");
    private static final String INFO_WORLDCHAT = Resources.get("channelInfoWorldChat");
    private static final String INFO_RANGECHAT = Resources.get("channelInfoRangeChat");
    private static final String INFO_FORMAT = Resources.get("channelInfoFormat");
    private static final String INFO_BANNED = Resources.get("channelInfoBanned");
    private static final String INFO_MUTED = Resources.get("channelInfoMuted");

    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String MSG_BAN_NGWORD = Resources.get("banNGWordMessage");
    private static final String MSG_KICK_NGWORD = Resources.get("kickNGWordMessage");
    private static final String MSG_MUTE_NGWORD = Resources.get("muteNGWordMessage");

    private static final String MSG_BAN_EXPIRED = Resources.get("expiredBanMessage");
    private static final String MSG_MUTE_EXPIRED = Resources.get("expiredMuteMessage");
    private static final String MSG_BAN_EXPIRED_PLAYER = Resources.get("cmdmsgPardoned");
    private static final String MSG_MUTE_EXPIRED_PLAYER = Resources.get("cmdmsgUnmuted");

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

    private static final String MSG_NO_RECIPIENT = Resources.get("noRecipientMessage");

    private static final String ERRMSG_MUTED = Resources.get("errmsgMuted");

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /** ロガー */
    private LunaChatLogger logger;

    /**
     * コンストラクタ
     * @param name チャンネル名
     */
    protected ChannelImpl(String name) {

        super(name);

        logger = new LunaChatLogger(name.replace(">", "-"));
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    @Override
    public void chat(ChannelPlayer player, String message) {

        // 発言権限を確認する
        String node = PERMISSION_SPEAK_PREFIX + "." + getName();
        if ( player.isPermissionSet(node) && !player.hasPermission(node) ) {
            sendResourceMessage(player, PREERR, "errmsgPermission",
                    PERMISSION_SPEAK_PREFIX + "." + getName());
            return;
        }

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        // Muteされているかどうかを確認する
        if ( getMuted().contains(player) ) {
            player.sendMessage( PREERR + ERRMSG_MUTED );
            return;
        }

        String preReplaceMessage = new String(message);
        String maskedMessage = new String(message);

        // 一時的にJapanizeスキップ設定かどうかを確認する
        boolean skipJapanize = false;
        String marker = config.getNoneJapanizeMarker();
        if ( !marker.equals("") && maskedMessage.startsWith(marker) ) {
            skipJapanize = true;
            maskedMessage = maskedMessage.substring(marker.length());
        }

        // NGワード発言をしたかどうかのチェックとマスク
        boolean isNG = false;
        for ( Pattern pattern : config.getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(maskedMessage);
            if ( matcher.find() ) {
                maskedMessage = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
                isNG = true;
            }
        }

        // キーワード置き換え
        String msgFormat = replaceKeywords(getFormat(), player);

        // カラーコード置き換え
        // チャンネルで許可されていて、発言者がパーミッションを持っている場合に置き換える
        if ( isAllowCC() && player.hasPermission("lunachat.allowcc") ) {
            maskedMessage = Utility.replaceColorCode(maskedMessage);
        }

        // イベントコール
        LunaChatChannelChatEvent event =
                new LunaChatChannelChatEvent(getName(), player,
                        preReplaceMessage, maskedMessage, msgFormat);
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return;
        }
        msgFormat = event.getMessageFormat();
        maskedMessage = event.getNgMaskedMessage();

        // 2byteコードを含むか、半角カタカナのみなら、Japanize変換は行わない
        String kanaTemp = Utility.stripColor(maskedMessage);
        if ( !skipJapanize &&
                ( kanaTemp.getBytes().length > kanaTemp.length() ||
                        kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
            skipJapanize = true;
        }

        // Japanize変換タスクを作成する
        boolean isIncludeSyncChat = true;
        DelayedJapanizeConvertTask delayedTask = null;
        JapanizeType japanizeType = (getJapanizeType() == null)
                ? config.getJapanizeType() : getJapanizeType();

        if ( !skipJapanize &&
                api.isPlayerJapanize(player.getName()) &&
                japanizeType != JapanizeType.NONE ) {

            int lineType = config.getJapanizeDisplayLine();
            String jpFormat;
            String messageFormat = null;
            if ( lineType == 1 ) {
                jpFormat = Utility.replaceColorCode(config.getJapanizeLine1Format());
                messageFormat = msgFormat;
                isIncludeSyncChat = false;
            } else {
                jpFormat = Utility.replaceColorCode(config.getJapanizeLine2Format());
            }

            // タスクを作成しておく
            delayedTask = new DelayedJapanizeChannelChatTask(maskedMessage,
                    japanizeType, this, player, jpFormat, messageFormat);
        }

        if ( isIncludeSyncChat ) {
            // メッセージの送信
            sendMessage(player, maskedMessage, msgFormat, true, player.getDisplayName());
        }

        // 非同期実行タスクがある場合、追加で実行する
        if ( delayedTask != null ) {
            delayedTask.runTaskAsynchronously(LunaChat.getInstance());
        }

        // NGワード発言者に、NGワードアクションを実行する
        if ( isNG ) {
            if ( config.getNgwordAction() == NGWordAction.BAN ) {
                // BANする

                if ( !isGlobalChannel() ) {
                    getBanned().add(player);
                    removeMember(player);
                    if ( !MSG_BAN_NGWORD.equals("") ) {
                        String m = replaceKeywordsForSystemMessages(
                                MSG_BAN_NGWORD, player.getName());
                        player.sendMessage(m);
                        sendMessage(null, m, null, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.KICK ) {
                // キックする

                if ( !isGlobalChannel() ) {
                    removeMember(player);
                    if ( !MSG_KICK_NGWORD.equals("") ) {
                        String m = replaceKeywordsForSystemMessages(
                                MSG_KICK_NGWORD, player.getName());
                        player.sendMessage(m);
                        sendMessage(null, m, null, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.MUTE ) {
                // Muteする

                getMuted().add(player);
                save();
                if ( !MSG_MUTE_NGWORD.equals("") ) {
                    String m = replaceKeywordsForSystemMessages(
                            MSG_MUTE_NGWORD, player.getName());
                    player.sendMessage(m);
                    sendMessage(null, m, null, true, "system");
                }
            }
        }
    }

    /**
     * ほかの連携先などから、このチャットに発言する
     * @param player プレイヤー名
     * @param source 連携元を判別する文字列
     * @param message メッセージ
     */
    @Override
    public void chatFromOtherSource(String player, String source, String message) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();

        // 表示名
        String name = player + "@" + source;

        // NGワード発言のマスク
        String maskedMessage = new String(message);
        for ( Pattern pattern : config.getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(maskedMessage);
            if ( matcher.find() ) {
                maskedMessage = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
            }
        }

        // キーワード置き換え
        String msgFormat = replaceKeywordsForSystemMessages(getFormat(), name);
        msgFormat = msgFormat.replace("%prefix", "");
        msgFormat = msgFormat.replace("%suffix", "");

        // カラーコード置き換え チャンネルで許可されている場合に置き換える。
        if ( isAllowCC() ) {
            maskedMessage = Utility.replaceColorCode(maskedMessage);
        }

        // メッセージの送信
        boolean sendDynmap = source == null || !source.equals("web");
        sendMessage(null, maskedMessage, msgFormat, sendDynmap, name);
    }

    /**
     * プレイヤーに関連する、システムメッセージをチャンネルに流す
     * @param key リソースキー
     * @param player プレイヤー
     */
    @Override
    protected void sendSystemMessage(String key, ChannelPlayer player) {

        // プライベートチャットならシステムメッセージを流さない
        if ( isPersonalChat() ) {
            return;
        }

        String msg = Resources.get(key);
        if ( msg == null || msg.equals("") ) {
            return;
        }
        msg = replaceKeywordsForSystemMessages(msg, player.getName());
        sendMessage(null, msg, null, false, "system");
    }

    /**
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param player プレイヤー（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     * @param name 発言者名
     */
    @Override
    public void sendMessage(ChannelPlayer player, String message,
            String format, boolean sendDynmap, String name) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();

        String originalMessage = new String(message);

        // 受信者を設定する
        ArrayList<ChannelPlayer> recipients = new ArrayList<ChannelPlayer>();
        boolean sendNoRecipientMessage = false;

        if ( isBroadcastChannel() ) {
            // ブロードキャストチャンネル

            if ( isWorldRange() && player != null &&
                    player.isOnline() && player.getPlayer() != null ) {

                World w = player.getPlayer().getWorld();

                if ( getChatRange() > 0 ) {
                    // 範囲チャット

                    Location origin = player.getPlayer().getLocation();
                    for ( Player p : Utility.getOnlinePlayers() ) {
                        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(p);
                        if ( p.getWorld().equals(w) &&
                                origin.distance(p.getLocation()) <= getChatRange() &&
                                !getHided().contains(cp) ) {
                            recipients.add(ChannelPlayer.getChannelPlayer(p));
                        }
                    }

                } else {
                    // ワールドチャット

                    for ( Player p : Utility.getOnlinePlayers() ) {
                        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(p);
                        if ( p.getWorld().equals(w) && !getHided().contains(cp) ) {
                            recipients.add(ChannelPlayer.getChannelPlayer(p));
                        }
                    }
                }

                // 受信者が自分以外いない場合は、メッセージを表示する
                if ( !MSG_NO_RECIPIENT.equals("") && (
                        recipients.size() == 0 ||
                        (recipients.size() == 1 &&
                         recipients.get(0).getName().equals(player.getName()) ) ) ) {
                    sendNoRecipientMessage = true;
                }

            } else {
                // 通常ブロードキャスト（全員へ送信）

                for ( Player p : Utility.getOnlinePlayers() ) {
                    ChannelPlayer cp = ChannelPlayer.getChannelPlayer(p);
                    if ( !getHided().contains(cp) ) {
                        recipients.add(cp);
                    }
                }
            }

        } else {
            // 通常チャンネル

            for ( ChannelPlayer mem : getMembers() ) {
                if ( mem != null && mem.isOnline() && !getHided().contains(mem) ) {
                    recipients.add(mem);
                }
            }
        }

        // opListenAllChannel 設定がある場合は、
        // パーミッション lunachat-admin.listen-all-channels を持つプレイヤーを
        // 受信者に加える。
        if ( config.isOpListenAllChannel() ) {
            for ( Player p : Utility.getOnlinePlayers() ) {
                ChannelPlayer cp = ChannelPlayer.getChannelPlayer(p);
                if ( cp.hasPermission("lunachat-admin.listen-all-channels")
                        && !recipients.contains(cp) ) {
                    recipients.add(cp);
                }
            }
        }

        // hideされている場合は、受信対象者から抜く。
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        for ( ChannelPlayer cp : api.getHidelist(player) )  {
            if ( recipients.contains(cp) ) {
                recipients.remove(cp);
            }
        }

        // フォーマットがある場合は置き換える
        if ( format != null ) {
            message = format.replace("%msg", message);
        }

        // イベントコール
        LunaChatChannelMessageEvent event =
                new LunaChatChannelMessageEvent(
                        getName(), player, message, recipients, name, originalMessage);
        Utility.callEventSync(event);
        message = event.getMessage();
        recipients = event.getRecipients();

        // 通常ブロードキャストなら、設定に応じてdynmapへ送信する
        DynmapBridge dynmap = LunaChat.getInstance().getDynmap();
        if ( config.isSendBroadcastChannelChatToDynmap() &&
                sendDynmap &&
                dynmap != null &&
                isBroadcastChannel() &&
                !isWorldRange() ) {
            if ( config.isSendFormattedMessageToDynmap() ) {
                if ( player != null && player.getPlayer() != null ) {
                    dynmap.chat(player.getPlayer(), message);
                } else {
                    dynmap.broadcast(message);
                }
            } else {
                if ( player != null && player.getPlayer() != null ) {
                    dynmap.chat(player.getPlayer(), originalMessage);
                } else {
                    dynmap.broadcast(originalMessage);
                }
            }
        }

        // 送信する
        for ( ChannelPlayer p : recipients ) {
            p.sendMessage(message);
        }

        // 設定に応じて、コンソールに出力する
        if ( config.isDisplayChatOnConsole() ) {
            Bukkit.getLogger().info(ChatColor.stripColor(message));
        }

        // 受信者が自分以外いない場合は、メッセージを表示する
        if ( sendNoRecipientMessage ) {
            String msg = replaceKeywordsForSystemMessages(MSG_NO_RECIPIENT, "");
            player.sendMessage(msg);
        }

        // ロギング
        log(originalMessage, name, player);
    }

    /**
     * チャンネル情報を返す
     * @param forModerator モデレータ向けの情報を含めるかどうか
     * @return チャンネル情報
     */
    @Override
    public ArrayList<String> getInfo(boolean forModerator) {

        ArrayList<String> info = new ArrayList<String>();
        info.add(INFO_FIRSTLINE);

        // チャンネル名、参加人数、総人数、チャンネル説明文
        info.add( String.format(
                LIST_FORMAT, getName(), getOnlineNum(), getTotalNum(), getDescription()) );

        // チャンネル別名
        String alias = getAlias();
        if ( alias != null && alias.length() > 0 ) {
            info.add(INFO_ALIAS + alias);
        }

        // 参加メンバー一覧
        if ( isGlobalChannel() ) {
            info.add(INFO_GLOBAL);
        } else if ( isBroadcastChannel() ) {
            info.add(INFO_BROADCAST);
        } else {
            // メンバーを、5人ごとに表示する
            StringBuffer buf = new StringBuffer();
            buf.append(INFO_PREFIX);

            for ( int i=0; i<getMembers().size(); i++ ) {

                if ( i%5 == 0 && i != 0 ) {
                    info.add(buf.toString());
                    buf = new StringBuffer();
                    buf.append(INFO_PREFIX);
                }

                ChannelPlayer cp = getMembers().get(i);
                String name = cp.getName();
                String disp;
                if ( getModerator().contains(cp) ) {
                    name = "@" + name;
                }
                if ( cp.isOnline() ) {
                    if ( getHided().contains(cp) )
                        disp = ChatColor.DARK_AQUA + name;
                    else
                        disp = ChatColor.WHITE + name;
                } else {
                    disp = ChatColor.GRAY + name;
                }
                buf.append(disp + ",");
            }

            info.add(buf.toString());
        }

        // シークレットチャンネルかどうか
        if ( !isVisible() ) {
            info.add(INFO_SECRET);
        }

        // パスワード設定があるかどうか
        if ( getPassword().length() > 0 ) {
            if ( !forModerator ) {
                info.add(INFO_PASSWORD);
            } else {
                info.add(INFO_PASSWORD + " " + getPassword());
            }
        }

        // 範囲チャット、ワールドチャット
        if ( isWorldRange() && getChatRange() > 0 ) {
            info.add(String.format(INFO_RANGECHAT, getChatRange()));
        } else if ( isWorldRange() ) {
            info.add(INFO_WORLDCHAT);
        }

        if ( forModerator ) {

            // フォーマット情報
            info.add(INFO_FORMAT);
            info.add(INFO_PREFIX + " " + ChatColor.WHITE + getFormat());

            // Muteリスト情報、5人ごとに表示する
            if ( getMuted().size() > 0 ) {
                info.add(INFO_MUTED);

                StringBuffer buf = new StringBuffer();
                buf.append(INFO_PREFIX + ChatColor.WHITE);
                for ( int i=0; i<getMuted().size(); i++ ) {
                    if ( i%5 == 0 && i != 0 ) {
                        info.add(buf.toString());
                        buf = new StringBuffer();
                        buf.append(INFO_PREFIX + ChatColor.WHITE);
                    }
                    buf.append(getMuted().get(i).getName() + ",");
                }

                info.add(buf.toString());
            }

            // BANリスト情報、5人ごとに表示する
            if ( getBanned().size() > 0 ) {
                info.add(INFO_BANNED);

                StringBuffer buf = new StringBuffer();
                buf.append(INFO_PREFIX + ChatColor.WHITE);
                for ( int i=0; i<getBanned().size(); i++ ) {
                    if ( i%5 == 0 && i != 0 ) {
                        info.add(buf.toString());
                        buf = new StringBuffer();
                        buf.append(INFO_PREFIX + ChatColor.WHITE);
                    }
                    buf.append(getBanned().get(i).getName() + ",");
                }

                info.add(buf.toString());
            }
        }

        info.add(LIST_ENDLINE);

        return info;
    }

    /**
     * ログファイルを読み込んで、ログデータを取得する
     * @param player プレイヤー名、フィルタしないならnullを指定すること
     * @param filter フィルタ、フィルタしないならnullを指定すること
     * @param date 日付、今日のデータを取得するならnullを指定すること
     * @param reverse 逆順取得
     * @return ログデータ
     */
    public ArrayList<String> getLog(
            String player, String filter, String date, boolean reverse) {

        return logger.getLog(player, filter, date, reverse);
    }

    /**
     * 期限付きBanや期限付きMuteをチェックし、期限が切れていたら解除を行う
     */
    @Override
    public void checkExpires() {

        long now = System.currentTimeMillis();

        // 期限付きBANのチェック
        for ( ChannelPlayer cp : getBanExpires().keySet() ) {
            if ( getBanExpires().get(cp) <= now ) {

                // 期限マップから削除し、BANを解除
                getBanExpires().remove(cp);
                if ( getBanned().contains(cp) ) {
                    getBanned().remove(cp);
                    save();

                    // メッセージ通知を流す
                    if ( !MSG_BAN_EXPIRED.equals("") ) {
                        String msg = replaceKeywords(MSG_BAN_EXPIRED, cp);
                        sendMessage(null, msg, null, false, "system");
                    }

                    if ( cp.isOnline() && !MSG_BAN_EXPIRED_PLAYER.equals("") ) {
                        String msg = PREINFO + String.format(MSG_BAN_EXPIRED_PLAYER, getName());
                        cp.sendMessage(msg);
                    }
                }
            }
        }

        // 期限付きMuteのチェック
        for ( ChannelPlayer cp : getMuteExpires().keySet() ) {
            if ( getMuteExpires().get(cp) <= now ) {

                // 期限マップから削除し、Muteを解除
                getMuteExpires().remove(cp);
                if ( getMuted().contains(cp) ) {
                    getMuted().remove(cp);
                    save();

                    // メッセージ通知を流す
                    if ( !MSG_MUTE_EXPIRED.equals("") ) {
                        String msg = replaceKeywords(MSG_MUTE_EXPIRED, cp);
                        sendMessage(null, msg, null, false, "system");
                    }

                    if ( cp.isOnline() && !MSG_MUTE_EXPIRED_PLAYER.equals("") ) {
                        String msg = PREINFO + String.format(MSG_MUTE_EXPIRED_PLAYER, getName());
                        cp.sendMessage(msg);
                    }
                }
            }
        }
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param player プレイヤー
     * @return 置き換え結果
     */
    private String replaceKeywords(String format, ChannelPlayer player) {

        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        String msg = format;

        // テンプレートのキーワードを、まず最初に置き換える
        for ( int i=0; i<=9; i++ ) {
            String key = "%" + i;
            if ( msg.contains(key) ) {
                if ( api.getTemplate("" + i) != null ) {
                    msg = msg.replace(key, api.getTemplate("" + i));
                    break;
                }
            }
        }

        msg = msg.replace("%ch", getName());
        //msg = msg.replace("%msg", message);
        msg = msg.replace("%color", getColorCode());
        msg = msg.replace("%to", getPrivateMessageTo());

        if ( msg.contains("%date") ) {
            msg = msg.replace("%date", dateFormat.format(new Date()));
        }
        if ( msg.contains("%time") ) {
            msg = msg.replace("%time", timeFormat.format(new Date()));
        }

        if ( player != null ) {
            msg = msg.replace("%username", player.getDisplayName());
            msg = msg.replace("%player", player.getName());

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                msg = msg.replace("%prefix", player.getPrefix());
                msg = msg.replace("%suffix", player.getSuffix());
            }

            if ( msg.contains("%world") ) {

                String worldname = null;
                if ( LunaChat.getInstance().getMultiverseCore() != null ) {
                    worldname = LunaChat.getInstance().getMultiverseCore().getWorldAlias(player.getWorldName());
                }
                if ( worldname == null || worldname.equals("") ) {
                    worldname = player.getWorldName();
                }
                msg = msg.replace("%world", worldname);
            }

        } else {
            msg = msg.replace("%username", "");
            msg = msg.replace("%player", "");
            msg = msg.replace("%prefix", "");
            msg = msg.replace("%suffix", "");
            msg = msg.replace("%world", "");
        }

        return Utility.replaceColorCode(msg);
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param playerName プレイヤー名
     * @return 置き換え結果
     */
    private String replaceKeywordsForSystemMessages(String format, String playerName) {

        String msg = format;
        msg = msg.replace("%ch", getName());
        msg = msg.replace("%color", getColorCode());
        msg = msg.replace("%username", playerName);
        msg = msg.replace("%player", playerName);

        return Utility.replaceColorCode(msg);
    }

    /**
     * ログを記録する
     * @param name 発言者
     * @param message 記録するメッセージ
     * @param player プレイヤー
     */
    private void log(String message, String name, ChannelPlayer player) {

        // LunaChatのチャットログへ記録
        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        if ( config.isLoggingChat() && logger != null ) {
            logger.log(message, name);
        }

        // Hawkeye Reloaded のチャットログへ記録
        if ( config.isLoggingChatToHawkEye() && LunaChat.getInstance().getHawkEye() != null
                && player != null && player.getLocation() != null ) {
            LunaChat.getInstance().getHawkEye().writeLog(name, player.getLocation(),
                    "channel(" + getName() + ")-" + Utility.stripColor(message));
        }

        // Prism のチャットログへ記録
        if ( config.isLoggingChatToPrism() && LunaChat.getInstance().getPrism() != null
                && player != null && player.getPlayer() != null ) {
            LunaChat.getInstance().getPrism().writeLog(player.getPlayer(),
                    "channel(" + getName() + ")-" + Utility.stripColor(message));
        }
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * @param player メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    private void sendResourceMessage(
            ChannelPlayer player, String pre, String key, Object... args) {

        String org = Resources.get(key);
        if ( org == null || org.equals("") ) {
            return;
        }
        String msg = String.format(pre + org, args);
        player.sendMessage(msg);
    }
}
