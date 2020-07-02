/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatBukkit;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.bridge.DynmapBridge;
import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberBukkit;
import com.github.ucchyocean.lc3.util.ClickableFormat;
import com.github.ucchyocean.lc3.util.UtilityBukkit;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * チャンネルの実装クラス
 * @author ucchy
 */
public class BukkitChannel extends Channel {

    /**
     * コンストラクタ
     * @param name チャンネル名
     */
    protected BukkitChannel(String name) {
        super(name);
    }

    /**
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param player プレイヤー（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     */
    @Override
    protected void sendMessage(
            ChannelMember player, String message,
            @Nullable ClickableFormat format, boolean sendDynmap) {

        LunaChatConfig config = LunaChat.getConfig();

        String originalMessage = new String(message);

        // 受信者を設定する
        List<ChannelMember> recipients = new ArrayList<ChannelMember>();
        boolean sendNoRecipientMessage = false;

        if ( isBroadcastChannel() ) {
            // ブロードキャストチャンネル

            if ( isWorldRange() && player.isOnline() && player.getWorldName() != null ) {

                World w = Bukkit.getWorld(player.getWorldName());

                if ( getChatRange() > 0 ) {
                    // 範囲チャット

                    if ( player instanceof ChannelMemberBukkit ) {
                        // ↑常にtrueだと思うが、念のため。

                        Location origin = ((ChannelMemberBukkit)player).getLocation();
                        for ( Player p : Bukkit.getOnlinePlayers() ) {
                            ChannelMember cp = ChannelMember.getChannelMember(p);
                            if ( p.getWorld().equals(w) &&
                                    origin.distance(p.getLocation()) <= getChatRange() &&
                                    !getHided().contains(cp) ) {
                                recipients.add(ChannelMember.getChannelMember(p));
                            }
                        }
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
                if ( Messages.noRecipientMessage("", "").length > 0 && (
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

        // LunaChatChannelMessageEvent イベントコール
        String name = (player != null) ? player.getDisplayName() : "<null>";
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelMessageEvent(
                getName(), player, message, recipients, name, originalMessage);
        message = result.getMessage();
        recipients = result.getRecipients();

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
        if ( format != null ) {
            format.replace("%msg", message);
            BaseComponent[] comps = format.makeTextComponent();
            for ( ChannelMember p : recipients ) {
                p.sendMessage(comps);
            }
        } else {
            for ( ChannelMember p : recipients ) {
                p.sendMessage(message);
            }
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
        log(originalMessage, name);
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
     * ログを記録する
     * @param name 発言者
     * @param message 記録するメッセージ
     */
    @Override
    protected void log(String message, String name) {

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
