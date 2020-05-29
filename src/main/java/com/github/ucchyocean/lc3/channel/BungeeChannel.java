/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.LunaChatLogger;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.NGWordAction;
import com.github.ucchyocean.lc3.Utility;
import com.github.ucchyocean.lc3.bungee.LunaChatBungee;
import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberProxiedPlayer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * チャンネルのBungee実装クラス
 * @author ucchy
 */
public class BungeeChannel extends Channel {

    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";

    private static final String MSG_BAN_NGWORD = Messages.get("banNGWordMessage");
    private static final String MSG_KICK_NGWORD = Messages.get("kickNGWordMessage");
    private static final String MSG_MUTE_NGWORD = Messages.get("muteNGWordMessage");

    private static final String PREERR = Messages.get("errorPrefix");

    private static final String ERRMSG_MUTED = Messages.get("errmsgMuted");

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /** ロガー */
    private LunaChatLogger logger;

    /**
     * コンストラクタ
     * @param name チャンネル名
     */
    protected BungeeChannel(String name) {

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
    public void chat(ChannelMember player, String message) {

        // 発言権限を確認する
        String node = PERMISSION_SPEAK_PREFIX + "." + getName();
        if ( player.isPermissionSet(node) && !player.hasPermission(node) ) {
            sendResourceMessage(player, PREERR, "errmsgPermission",
                    PERMISSION_SPEAK_PREFIX + "." + getName());
            return;
        }

        LunaChatConfig config = LunaChat.getConfig();
        LunaChatAPI api = LunaChat.getAPI();

        // Muteされているかどうかを確認する
        if ( getMuted().contains(player) ) {
            player.sendMessage( PREERR + ERRMSG_MUTED );
            return;
        }

//        String preReplaceMessage = new String(message);
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
//        LunaChatChannelChatEvent event =
//                new LunaChatChannelChatEvent(getName(), player,
//                        preReplaceMessage, maskedMessage, msgFormat);
//        Bukkit.getPluginManager().callEvent(event);
//        if ( event.isCancelled() ) {
//            return;
//        }
//        msgFormat = event.getMessageFormat();
//        maskedMessage = event.getNgMaskedMessage();

        // 2byteコードを含むか、半角カタカナのみなら、Japanize変換は行わない
        String kanaTemp = Utility.stripColorCode(maskedMessage);
        if ( !skipJapanize &&
                ( kanaTemp.getBytes().length > kanaTemp.length() ||
                        kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
            skipJapanize = true;
        }

        // Japanize変換タスクを作成する
        boolean isIncludeSyncChat = true;
        JapanizeChannelChatTask delayedTask = null;
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
            delayedTask = new JapanizeChannelChatTask(maskedMessage,
                    japanizeType, this, player, jpFormat, messageFormat);
        }

        if ( isIncludeSyncChat ) {
            // メッセージの送信
            sendMessage(player, maskedMessage, msgFormat, true, player.getDisplayName());
        }

        // 非同期実行タスクがある場合、追加で実行する
        if ( delayedTask != null ) {
            ProxyServer.getInstance().getScheduler().runAsync(
                    LunaChatBungee.getInstance(), delayedTask);
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
     * プレイヤーに関連する、システムメッセージをチャンネルに流す
     * @param key リソースキー
     * @param player プレイヤー
     */
    @Override
    protected void sendSystemMessage(String key, ChannelMember player) {

        // プライベートチャットならシステムメッセージを流さない
        if ( isPersonalChat() ) {
            return;
        }

        String msg = Messages.get(key);
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
    public void sendMessage(ChannelMember player, String message,
            String format, boolean sendDynmap, String name) {

        LunaChatConfig config = LunaChat.getConfig();

        String originalMessage = new String(message);

        // 受信者を設定する
        Set<ChannelMember> recipients = new HashSet<ChannelMember>();

        if ( isBroadcastChannel() ) {
            // ブロードキャストチャンネル

            // NOTE: BungeeChannelは範囲チャットやワールドチャットをサポートしない

            // 通常ブロードキャスト（全員へ送信）
            for ( ProxiedPlayer p : ProxyServer.getInstance().getPlayers() ) {
                ChannelMember cp = ChannelMember.getChannelMember(p);
                if ( !getHided().contains(cp) ) {
                    recipients.add(cp);
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
            for ( ProxiedPlayer p : ProxyServer.getInstance().getPlayers() ) {
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
//        LunaChatChannelMessageEvent event =
//                new LunaChatChannelMessageEvent(
//                        getName(), player, message, recipients, name, originalMessage);
//        Bukkit.getPluginManager().callEvent(event);
//        message = event.getMessage();
//        recipients = event.getRecipients();

        // 送信する
        for ( ChannelMember p : recipients ) {
            p.sendMessage(message);
        }

        // 設定に応じて、コンソールに出力する
        if ( config.isDisplayChatOnConsole() ) {
            LunaChatBungee.getInstance().getLogger().info(message);
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
            return ProxyServer.getInstance().getOnlineCount();
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
            return ProxyServer.getInstance().getOnlineCount();
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
            for ( ProxiedPlayer p : ProxyServer.getInstance().getPlayers() ) {
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

            msg = msg.replace("%world", "");

            if ( msg.contains("%server") ) {
                if ( player instanceof ChannelMemberProxiedPlayer ) {
                    String serverName = ((ChannelMemberProxiedPlayer)player).getServer().getInfo().getName();
                    msg = msg.replace("%server", serverName);
                }
                msg = msg.replace("%server", "");
            }

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
    }
}