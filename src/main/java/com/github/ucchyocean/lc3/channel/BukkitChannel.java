/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.NGWordAction;
import com.github.ucchyocean.lc3.Utility;
import com.github.ucchyocean.lc3.UtilityBukkit;
import com.github.ucchyocean.lc3.bridge.DynmapBridge;
import com.github.ucchyocean.lc3.bukkit.LunaChatBukkit;
import com.github.ucchyocean.lc3.event.LunaChatChannelChatEvent;
import com.github.ucchyocean.lc3.event.LunaChatChannelMessageEvent;
import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberBukkit;

/**
 * チャンネルの実装クラス
 * @author ucchy
 */
public class BukkitChannel extends Channel {

    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /**
     * コンストラクタ
     * @param name チャンネル名
     */
    protected BukkitChannel(String name) {
        super(name);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    @Override
    public void chat(ChannelMember player, String message) {

        // 発言権限を確認する
        String node = PERMISSION_SPEAK_PREFIX + "." + getName();
        if ( player.isPermissionSet(node) && !player.hasPermission(node) ) {
            player.sendMessage(Messages.errmsgPermission(node));
            return;
        }

        LunaChatConfig config = LunaChat.getConfig();
        LunaChatAPI api = LunaChat.getAPI();

        // Muteされているかどうかを確認する
        if ( getMuted().contains(player) ) {
            player.sendMessage(Messages.errmsgMuted());
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
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }
        msgFormat = event.getMessageFormat();
        maskedMessage = event.getNgMaskedMessage();

        // 2byteコードを含むか、半角カタカナのみなら、Japanize変換は行わない
        String kanaTemp = Utility.stripColorCode(maskedMessage);
        if ( !skipJapanize &&
                ( kanaTemp.getBytes().length > kanaTemp.length() ||
                        kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
            skipJapanize = true;
        }

        // Japanize変換タスクを作成する
        boolean isIncludeSyncChat = true;
        ChannelChatJapanizeTask delayedTask = null;
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
            delayedTask = new ChannelChatJapanizeTask(maskedMessage,
                    japanizeType, this, player, jpFormat, messageFormat);
        }

        if ( isIncludeSyncChat ) {
            // メッセージの送信
            sendMessage(player, maskedMessage, msgFormat, true, player.getDisplayName());
        }

        // 非同期実行タスクがある場合、追加で実行する
        if ( delayedTask != null ) {
            Bukkit.getScheduler().runTaskAsynchronously(
                    LunaChatBukkit.getInstance(), delayedTask);
        }

        // NGワード発言者に、NGワードアクションを実行する
        if ( isNG ) {
            if ( config.getNgwordAction() == NGWordAction.BAN ) {
                // BANする

                if ( !isGlobalChannel() ) {
                    getBanned().add(player);
                    removeMember(player);
                    if ( !Messages.banNGWordMessage("", "", "").isEmpty() ) {
                        String m = Messages.banNGWordMessage(getColorCode(), getName(), player.getName());
                        player.sendMessage(m);
                        sendMessage(null, m, null, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.KICK ) {
                // キックする

                if ( !isGlobalChannel() ) {
                    removeMember(player);
                    if ( !Messages.kickNGWordMessage("", "", "").isEmpty() ) {
                        String m = Messages.kickNGWordMessage(getColorCode(), getName(), player.getName());
                        player.sendMessage(m);
                        sendMessage(null, m, null, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.MUTE ) {
                // Muteする

                getMuted().add(player);
                save();
                if ( !Messages.muteNGWordMessage("", "", "").isEmpty() ) {
                    String m = Messages.muteNGWordMessage(getColorCode(), getName(), player.getName());
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

        LunaChatConfig config = LunaChat.getConfig();

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
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param player プレイヤー（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     * @param name 発言者名
     */
    @Override
    public void sendMessage(ChannelMember player, String message,
            String format, boolean sendDynmap, String name) {

        LunaChatConfig config = LunaChat.getConfig();

        String originalMessage = new String(message);

        // 受信者を設定する
        ArrayList<ChannelMember> recipients = new ArrayList<ChannelMember>();
        boolean sendNoRecipientMessage = false;

        if ( isBroadcastChannel() ) {
            // ブロードキャストチャンネル

            if ( isWorldRange() && player.isOnline() && player.getWorldName() != null ) {

                World w = Bukkit.getWorld(player.getWorldName());

                if ( getChatRange() > 0 ) {
                    // 範囲チャット

                    if ( player instanceof ChannelMemberBukkit ) {
                        Location origin = ((ChannelMemberBukkit)player).getLocation();
                        for ( Player p : Bukkit.getOnlinePlayers() ) {
                            ChannelMember cp = ChannelMember.getChannelMember(p);
                            if ( p.getWorld().equals(w) &&
                                    origin.distance(p.getLocation()) <= getChatRange() &&
                                    !getHided().contains(cp) ) {
                                recipients.add(ChannelMember.getChannelMember(p));
                            }
                        }
                    } else {
                        // TODO 何かするか？検討する
                    }

                } else {
                    // ワールドチャット

                    for ( Player p : Bukkit.getOnlinePlayers() ) {
                        ChannelMember cp = ChannelMember.getChannelMember(p);
                        if ( p.getWorld().equals(w) && !getHided().contains(cp) ) {
                            recipients.add(ChannelMember.getChannelMember(p));
                        }
                    }
                }

                // 受信者が自分以外いない場合は、メッセージを表示する
                if ( !Messages.noRecipientMessage("", "").isEmpty() && (
                        recipients.size() == 0 ||
                        (recipients.size() == 1 &&
                         recipients.get(0).getName().equals(player.getName()) ) ) ) {
                    sendNoRecipientMessage = true;
                }

            } else {
                // 通常ブロードキャスト（全員へ送信）

                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    ChannelMember cp = ChannelMember.getChannelMember(p);
                    if ( !getHided().contains(cp) ) {
                        recipients.add(cp);
                    }
                }
            }

        } else {
            // 通常チャンネル

            for ( ChannelMember mem : getMembers() ) {
                if ( mem != null && mem.isOnline() && !getHided().contains(mem) ) {
                    recipients.add(mem);
                }
            }
        }

        // opListenAllChannel 設定がある場合は、
        // パーミッション lunachat-admin.listen-all-channels を持つプレイヤーを
        // 受信者に加える。
        if ( config.isOpListenAllChannel() ) {
            for ( Player p : Bukkit.getOnlinePlayers() ) {
                ChannelMember cp = ChannelMember.getChannelMember(p);
                if ( cp.hasPermission("lunachat-admin.listen-all-channels")
                        && !recipients.contains(cp) ) {
                    recipients.add(cp);
                }
            }
        }

        // hideされている場合は、受信対象者から抜く。
        LunaChatAPI api = LunaChat.getAPI();
        for ( ChannelMember cp : api.getHidelist(player) )  {
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
        Bukkit.getPluginManager().callEvent(event);
        message = event.getMessage();
        recipients = event.getRecipients();

        // 通常ブロードキャストなら、設定に応じてdynmapへ送信する
        DynmapBridge dynmap = LunaChatBukkit.getInstance().getDynmap();
        if ( config.isSendBroadcastChannelChatToDynmap() &&
                sendDynmap &&
                dynmap != null &&
                isBroadcastChannel() &&
                !isWorldRange() ) {

            String msg = config.isSendFormattedMessageToDynmap() ? message : originalMessage;
            if ( player != null && player instanceof ChannelMemberBukkit
                    && ((ChannelMemberBukkit)player).getPlayer() != null ) {
                dynmap.chat(((ChannelMemberBukkit)player).getPlayer(), msg);
            } else {
                dynmap.broadcast(msg);
            }
        }

        // 送信する
        for ( ChannelMember p : recipients ) {
            p.sendMessage(message);
        }

        // 設定に応じて、コンソールに出力する
        if ( config.isDisplayChatOnConsole() ) {
            Bukkit.getLogger().info(ChatColor.stripColor(message));
        }

        // 受信者が自分以外いない場合は、メッセージを表示する
        if ( sendNoRecipientMessage ) {
            player.sendMessage(Messages.noRecipientMessage(getColorCode(), getName()));
        }

        // ロギング
        log(originalMessage, name, player);
    }

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     * @see com.github.ucchyocean.lc3.channel.Channel#getOnlineNum()
     */
    @Override
    public int getOnlineNum() {

        // ブロードキャストチャンネルならサーバー接続人数を返す
        if ( isBroadcastChannel() ) {
            return UtilityBukkit.getOnlinePlayersCount();
        }

        return super.getOnlineNum();
    }

    /**
     * チャンネルの総参加人数を返す
     * @return 総参加人数
     * @see com.github.ucchyocean.lc3.channel.Channel#getTotalNum()
     */
    @Override
    public int getTotalNum() {

        // ブロードキャストチャンネルならサーバー接続人数を返す
        if ( isBroadcastChannel() ) {
            return UtilityBukkit.getOnlinePlayersCount();
        }

        return super.getTotalNum();
    }

    /**
     * チャンネルのメンバーを返す
     * @return チャンネルのメンバー
     * @see com.github.ucchyocean.lc3.channel.Channel#getMembers()
     */
    @Override
    public List<ChannelMember> getMembers() {

        // ブロードキャストチャンネルなら、
        // 現在サーバーに接続している全プレイヤーをメンバーとして返す
        if ( isBroadcastChannel() ) {
            List<ChannelMember> mem = new ArrayList<ChannelMember>();
            for ( Player p : Bukkit.getOnlinePlayers() ) {
                mem.add(ChannelMember.getChannelMember(p));
            }
            return mem;
        }

        return super.getMembers();
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param player プレイヤー
     * @return 置き換え結果
     */
    @Override
    protected String replaceKeywords(String format, ChannelMember player) {

        LunaChatAPI api = LunaChat.getAPI();

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
        if ( getPrivateMessageTo() != null ) {
            msg = msg.replace("%to", getPrivateMessageTo().getDisplayName());
        } else {
            msg = msg.replace("%to", "");
        }
        msg = msg.replace("%recieverserver", "");

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
                if ( LunaChatBukkit.getInstance().getMultiverseCore() != null ) {
                    worldname = LunaChatBukkit.getInstance().getMultiverseCore().getWorldAlias(player.getWorldName());
                }
                if ( worldname == null || worldname.equals("") ) {
                    worldname = player.getWorldName();
                }
                msg = msg.replace("%world", worldname);
            }

            msg = msg.replace("%server", "");

        } else {
            msg = msg.replace("%username", "");
            msg = msg.replace("%player", "");
            msg = msg.replace("%prefix", "");
            msg = msg.replace("%suffix", "");
            msg = msg.replace("%world", "");
            msg = msg.replace("%server", "");
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
    private void log(String message, String name, ChannelMember player) {

        // LunaChatのチャットログへ記録
        LunaChatConfig config = LunaChat.getConfig();
        if ( config.isLoggingChat() && logger != null ) {
            logger.log(message, name);
        }

        // TODO ログ記録プラグイン連携を検討する
//        // Hawkeye Reloaded のチャットログへ記録
//        if ( config.isLoggingChatToHawkEye() && LunaChat.getInstance().getHawkEye() != null
//                && player != null && player.getLocation() != null ) {
//            LunaChat.getInstance().getHawkEye().writeLog(name, player.getLocation(),
//                    "channel(" + getName() + ")-" + Utility.stripColor(message));
//        }
//
//        // Prism のチャットログへ記録
//        if ( config.isLoggingChatToPrism() && LunaChat.getInstance().getPrism() != null
//                && player != null && player.getPlayer() != null ) {
//            LunaChat.getInstance().getPrism().writeLog(player.getPlayer(),
//                    "channel(" + getName() + ")-" + Utility.stripColor(message));
//        }
    }
}
