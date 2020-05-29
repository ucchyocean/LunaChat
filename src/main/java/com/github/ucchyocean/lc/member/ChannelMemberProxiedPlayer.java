/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.member;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * ChannelMemberのBungeeCord-ProxiedPlayer実装
 * @author ucchy
 */
public class ChannelMemberProxiedPlayer extends ChannelMemberBungee {

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
     * @param nameOrUuid 名前、または、"$" + UUID
     * @return ChannelMemberProxiedPlayer
     */
    public static ChannelMemberProxiedPlayer getChannelMember(String nameOrUuid) {
        ProxiedPlayer player = null;
        if ( nameOrUuid.startsWith("$") ) {
            player = ProxyServer.getInstance().getPlayer(
                    UUID.fromString(nameOrUuid.substring(1)));
        } else {
            player = ProxyServer.getInstance().getPlayer(nameOrUuid);
        }
        if ( player != null ) {
            return new ChannelMemberProxiedPlayer(player.getUniqueId());
        }

        // TODO オフラインプレイヤーのUUIDを取得する方法を検討する

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
    @Override
    public void sendMessage(String message) {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            player.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    /**
     * 発言者が今いるワールドのワールド名を取得する
     * @return ワールド名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getWorldName()
     */
    @Override
    public String getWorldName() {
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
    @Override
    public @Nullable ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(id);
    }

    /**
     * 発言者が今いるサーバーを取得する
     * @return サーバー
     * @see com.github.ucchyocean.lc.member.ChannelMemberBungee#getServer()
     */
    @Override
    public @Nullable Server getServer() {
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            return player.getServer();
        }
        return null;
    }

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     * @see com.github.ucchyocean.lc.member.ChannelMember#isPermissionSet(java.lang.String)
     */
    @Override
    public boolean isPermissionSet(String node) {
        // TODO 要テスト
        ProxiedPlayer player = getPlayer();
        if ( player != null ) {
            return player.getPermissions().contains(node);
        }
        return false;
    }
}
