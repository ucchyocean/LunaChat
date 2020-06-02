/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.member;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * チャンネルメンバーのBungee抽象クラス
 * @author ucchy
 */
public abstract class ChannelMemberBungee extends ChannelMember {

    /**
     * BungeeのProxiedPlayerを取得する
     * @return ProxiedPlayer
     */
    public abstract ProxiedPlayer getPlayer();

    /**
     * 発言者が今いるサーバーを取得する
     * @return サーバー
     */
    public abstract Server getServer();

    /**
     * CommandSenderから、ChannelMemberを作成して返す
     * @param sender
     * @return ChannelMember
     */
    public static ChannelMemberBungee getChannelMemberBungee(Object sender) {
        if ( sender == null || !(sender instanceof CommandSender) ) return null;
        if ( sender instanceof ProxiedPlayer ) {
            return new ChannelMemberProxiedPlayer(((ProxiedPlayer)sender).getUniqueId());
        } else {
            // ProxiedPlayer以外のCommandSenderは、ConsoleSenderしかないはず
            return new ChannelMemberBungeeConsole((CommandSender)sender);
        }
    }

}
