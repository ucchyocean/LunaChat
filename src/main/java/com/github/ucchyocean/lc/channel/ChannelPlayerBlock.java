/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.channel;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * コマンドブロック
 * @author ucchy
 */
public class ChannelPlayerBlock extends ChannelPlayer {

    BlockCommandSender sender;

    /**
     * コンストラクタ
     * @param sender コマンドブロック
     */
    public ChannelPlayerBlock(BlockCommandSender sender) {
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
     * 指定されたCommandSenderと同一かどうかを返す
     * @param sender
     * @return 同一かどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#equals(org.bukkit.entity.Player)
     */
    @Override
    public boolean equals(CommandSender sender) {
        return this.sender.equals(sender);
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
}
