/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.member;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc.CommandSenderInterface;
import com.github.ucchyocean.lc.bungee.CommandSenderBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * ChannelMemberのBungeeCord-ProxiedPlayer実装
 * @author ucchy
 */
public class ChannelMemberProxiedPlayer extends ChannelMember {

    private UUID id;

    /**
     * コンストラクタ
     * @param id プレイヤーID
     */
    public ChannelMemberProxiedPlayer(String id) {
        this.id = UUID.fromString(id);
    }

    /**
     * コンストラクタ
     * @param id UUID
     */
    public ChannelMemberProxiedPlayer(UUID id) {
        this.id = id;
    }

    /**
     * プレイヤー名からUUIDを取得してChannelMemberProxiedPlayerを作成して返す
     * @param name プレイヤー名
     * @return ChannelMemberProxiedPlayer
     */
    public static ChannelMemberProxiedPlayer getChannelMemberProxiedPlayerFromName(String name) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
        if ( player != null ) {
            return new ChannelMemberProxiedPlayer(player.getUniqueId());
        }

        // TODO 接続していないプレイヤーのUUIDを取得する方法を検討する

        return null;
    }

    /**
     * CommandSenderから、ChannelMemberProxiedPlayerを作成して返す
     * @param sender
     * @return ChannelMemberProxiedPlayer
     */
    public static ChannelMemberProxiedPlayer getChannelPlayer(CommandSender sender) {
        if ( sender instanceof ProxiedPlayer ) {
            return new ChannelMemberProxiedPlayer(((ProxiedPlayer)sender).getUniqueId());
        }
        // TODO BungeeCordのCommandSenderを実装するクラスは、ProxiedPlayer以外にないはず。コンソールは？
        return null;
    }

    /**
     * オンラインかどうか
     * @return オンラインかどうか
     */
    @Override
    public boolean isOnline() {
        return (ProxyServer.getInstance().getPlayer(id) != null);
    }

    /**
     * プレイヤー名を返す
     * @return プレイヤー名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getName()
     */
    @Override
    public String getName() {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            return player.getName();
        }

        // TODO 接続していないプレイヤーのUUIDを取得する方法を検討する

        return id.toString();
    }

    /**
     * プレイヤー表示名を返す
     * @return プレイヤー表示名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            return player.getDisplayName();
        }
        return getName();
    }

    /**
     * プレフィックスを返す
     * @return プレフィックス
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getPrefix()
     */
    @Override
    public String getPrefix() {

        // TODO 未実装

        return "";
    }

    /**
     * サフィックスを返す
     * @return サフィックス
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getSuffix()
     */
    @Override
    public String getSuffix() {

        // TODO 未実装

        return "";
    }

    /**
     * メッセージを送る
     * @param message 送るメッセージ
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#sendMessage(java.lang.String)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void sendMessage(String message) {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            player.sendMessage(message);
        }
    }

    /**
     * 発言者が今いるワールドのワールド名を取得する
     * @return ワールド名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getWorldName()
     */
    @Override
    public String getWorldName() {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            // TODO 未実装。方法を検討する
        }
        return "-";
    }

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#hasPermission(java.lang.String)
     */
    @Override
    public boolean hasPermission(String node) {
        ProxiedPlayer player = getPlayer();
        if ( player == null ) {
            return false;
        } else {
            return player.hasPermission(node);
        }
    }

    /**
     * 指定されたConnectionと同一かどうかを返す
     * @param sender
     * @return 同一かどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#equals(org.bukkit.entity.Player)
     */
    @Override
    public boolean equals(CommandSenderInterface sender) {
        if ( sender == null || !(sender instanceof CommandSenderBungee) ) {
            return false;
        }
        CommandSender s = ((CommandSenderBungee)sender).getSender();
        if ( !(s instanceof ProxiedPlayer) ) {
            return false;
        }
        return id.equals(((ProxiedPlayer)s).getUniqueId());
    }

    /**
     * IDを返す
     * @return "$" + UUID を返す
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getID()
     */
    @Override
    public String toString() {
        return "$" + id.toString();
    }

    /**
     * ProxiedPlayerを取得して返す
     * @return ProxiedPlayer
     */
    private @Nullable ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(id);
    }

    @Override
    public boolean isPermissionSet(String node) {
        // TODO 要テスト
        ProxiedPlayer player = getPlayer();
        if ( player == null ) {
            return false;
        } else {
            return player.getPermissions().contains(node);
        }
    }

    public static ChannelMemberProxiedPlayer getChannelMemberFromSender(CommandSender sender) {
        if ( sender == null ) return null;
        if ( !(sender instanceof ProxiedPlayer) ) return null;
        return new ChannelMemberProxiedPlayer(((ProxiedPlayer)sender).getUniqueId());
    }

    public static ChannelMemberProxiedPlayer getChannelMemberFromSender(Object obj) {
        if ( obj == null ) return null;
        if ( !(obj instanceof ProxiedPlayer) ) return null;
        return new ChannelMemberProxiedPlayer(((ProxiedPlayer)obj).getUniqueId());
    }
}
