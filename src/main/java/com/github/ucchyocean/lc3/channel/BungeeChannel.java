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

import com.github.ucchyocean.lc3.KeywordReplacer;
import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatBungee;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.Utility;
import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberProxiedPlayer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * チャンネルのBungee実装クラス
 * @author ucchy
 */
public class BungeeChannel extends Channel {

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /**
     * コンストラクタ
     * @param name チャンネル名
     */
    protected BungeeChannel(String name) {

        super(name);

        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
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
    protected void sendMessage(ChannelMember player, String message,
            String format, boolean sendDynmap, String name) {

        LunaChatConfig config = LunaChat.getConfig();

        String originalMessage = new String(message);

        // 受信者を設定する
        List<ChannelMember> recipients = new ArrayList<ChannelMember>();

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

        // LunaChatChannelMessageEvent イベントコール
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelMessageEvent(
                getName(), player, message, recipients, name, originalMessage);
        message = result.getMessage();
        recipients = result.getRecipients();

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

        KeywordReplacer msg = new KeywordReplacer(format);

        // テンプレートのキーワードを、まず最初に置き換える
        for ( int i=0; i<=9; i++ ) {
            String key = "%" + i;
            if ( msg.contains(key) ) {
                if ( api.getTemplate("" + i) != null ) {
                    msg.replace(key, api.getTemplate("" + i));
                    break;
                }
            }
        }

        msg.replace("%ch", getName());
        //msg.replace("%msg", message);
        msg.replace("%color", getColorCode());
        if ( getPrivateMessageTo() != null ) {
            msg.replace("%to", getPrivateMessageTo().getDisplayName());
            if ( getPrivateMessageTo() instanceof ChannelMemberProxiedPlayer ) {
                ChannelMemberProxiedPlayer cm = (ChannelMemberProxiedPlayer)getPrivateMessageTo();
                msg.replace("%recieverserver", cm.getServer().getInfo().getName());
            } else {
                msg.replace("%recieverserver", "");
            }
        } else {
            msg.replace("%to", "");
            msg.replace("%recieverserver", "");
        }

        if ( msg.contains("%date") ) {
            msg.replace("%date", dateFormat.format(new Date()));
        }
        if ( msg.contains("%time") ) {
            msg.replace("%time", timeFormat.format(new Date()));
        }

        if ( player != null ) {
            msg.replace("%username", player.getDisplayName());
            msg.replace("%player", player.getName());

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                msg.replace("%prefix", player.getPrefix());
                msg.replace("%suffix", player.getSuffix());
            }

            msg.replace("%world", "");

            if ( msg.contains("%server") ) {
                if ( player instanceof ChannelMemberProxiedPlayer ) {
                    String serverName = ((ChannelMemberProxiedPlayer)player).getServer().getInfo().getName();
                    msg.replace("%server", serverName);
                }
                msg.replace("%server", "");
            }

        } else {
            msg.replace("%username", "");
            msg.replace("%player", "");
            msg.replace("%prefix", "");
            msg.replace("%suffix", "");
            msg.replace("%world", "");
            msg.replace("%server", "");
        }

        return Utility.replaceColorCode(msg.toString());
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