/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.member;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * ChannelMemberのBukkit-BlockCommandSender実装
 * @author ucchy
 */
public class ChannelMemberBlock extends ChannelMemberBukkit {

    BlockCommandSender sender;

    /**
     * コンストラクタ
     * @param sender コマンドブロック
     */
    public ChannelMemberBlock(BlockCommandSender sender) {
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
     * メッセージを送る、実際は何もせずにメッセージを捨てる
     * @param message
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String message) {
        // do nothing.
    }

    /**
     * メッセージを送る、実際は何もせずにメッセージを捨てる
     * @param message 送るメッセージ
     * @see com.github.ucchyocean.lc3.member.ChannelMember#sendMessage(net.md_5.bungee.api.chat.BaseComponent[])
     */
    public void sendMessage(BaseComponent[] message) {
        // do nothing.
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
     * @return 常に空文字列
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getWorldName()
     */
    @Override
    public String getWorldName() {
        return "";
    }

    /**
     * 発言者が今いる位置を取得する
     * @return 発言者の位置
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getLocation()
     */
    @Override
    public Location getLocation() {
        if ( sender != null ) {
            return sender.getBlock().getLocation();
        }
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
     * 指定されたメッセージの内容を発言する
     * @param message メッセージ
     * @see com.github.ucchyocean.lc3.member.ChannelMember#chat(java.lang.String)
     */
    public void chat(String message) {
        Bukkit.broadcastMessage("<" + getName() + "> " + message);
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
        return sender.getBlock().getWorld();
    }

    public BlockCommandSender getBlockCommandSender() {
        return sender;
    }
}
