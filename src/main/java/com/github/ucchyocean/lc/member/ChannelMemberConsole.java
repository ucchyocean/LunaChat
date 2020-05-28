/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.member;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * ChannelMemberのBukkit-ConsoleCommandSender実装
 * @author ucchy
 */
public class ChannelMemberConsole extends ChannelMemberBukkit {

    ConsoleCommandSender sender;

    /**
     * コンストラクタ
     * @param sender コンソール
     */
    public ChannelMemberConsole(ConsoleCommandSender sender) {
        this.sender = sender;
    }

    /**
     * オンラインかどうか
     * @return 常にtrue
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#isOnline()
     */
    @Override
    public boolean isOnline() {
        return true;
    }

    /**
     * プレイヤー名を返す
     * @return プレイヤー名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getName()
     */
    @Override
    public String getName() {
        return sender.getName();
    }

    /**
     * プレイヤー表示名を返す
     * @return プレイヤー表示名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return sender.getName();
    }

    /**
     * プレフィックスを返す
     * @return 常に空文字列
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getPrefix()
     */
    @Override
    public String getPrefix() {
        return "";
    }

    /**
     * サフィックスを返す
     * @return 常に空文字列
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getSuffix()
     */
    @Override
    public String getSuffix() {
        return "";
    }

    /**
     * メッセージを送る
     * @param message 送信するメッセージ
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    /**
     * BukkitのPlayerを取得する
     * @return 常にnullが返される
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getPlayer()
     */
    @Override
    public Player getPlayer() {
        return null;
    }

    /**
     * 発言者が今いるワールドのワールド名を取得する
     * @return 常に "-" が返される。
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getWorldName()
     */
    @Override
    public String getWorldName() {
        return "-";
    }

    /**
     * 発言者が今いる位置を取得する
     * @return 常にnullが返される
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getLocation()
     */
    @Override
    public Location getLocation() {
        return null;
    }

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#hasPermission(java.lang.String)
     */
    @Override
    public boolean hasPermission(String node) {
        return sender.hasPermission(node);
    }

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#isPermissionSet(java.lang.String)
     */
    @Override
    public boolean isPermissionSet(String node) {
        return sender.isPermissionSet(node);
    }

    /**
     * IDを返す
     * @return 名前をそのまま返す
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getID()
     */
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public World getWorld() {
        return null;
    }
}
