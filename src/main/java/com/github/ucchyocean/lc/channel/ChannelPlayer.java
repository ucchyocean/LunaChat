/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.channel;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Utility;

/**
 * プレイヤーの抽象クラス
 * @author ucchy
 */
public abstract class ChannelPlayer implements Comparable<ChannelPlayer> {

    /**
     * オンラインかどうか
     * @return オンラインかどうか
     */
    public abstract boolean isOnline();

    /**
     * プレイヤー名を返す
     * @return プレイヤー名
     */
    public abstract String getName();

    /**
     * プレイヤー表示名を返す
     * @return プレイヤー表示名
     */
    public abstract String getDisplayName();

    /**
     * プレフィックスを返す
     * @return プレフィックス
     */
    public abstract String getPrefix();

    /**
     * サフィックスを返す
     * @return サフィックス
     */
    public abstract String getSuffix();

    /**
     * メッセージを送る
     * @param message 送るメッセージ
     */
    public abstract void sendMessage(String message);

    /**
     * BukkitのPlayerを取得する
     * @return Player
     */
    public abstract Player getPlayer();

    /**
     * 発言者が今いるワールドのワールド名を取得する
     * @return ワールド名
     */
    public abstract String getWorldName();

    /**
     * 発言者が今いる位置を取得する
     * @return 発言者の位置
     */
    public abstract Location getLocation();

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     */
    public abstract boolean hasPermission(String node);

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     */
    public abstract boolean isPermissionSet(String node);

    /**
     * 指定されたCommandSenderと同一かどうかを返す
     * @param sender
     * @return 同一かどうか
     */
    public abstract boolean equals(CommandSender sender);

    /**
     * 文字列表現を返す
     * @return 名前管理なら名前、UUID管理なら "$" + UUID を返す
     */
    @Override
    public abstract String toString();

    /**
     * 同一のオブジェクトかどうかを返す
     * @param other 他方のオブジェクト
     * @return 同一かどうか
     */
    @Override
    public boolean equals(Object other) {
        if ( !(other instanceof ChannelPlayer) ) {
            return false;
        }
        return this.toString().equals(other.toString());
    }

    /**
     * インスタンス同士の比較を行う。このメソッドを実装しておくことで、
     * Java8でのHashMapのキー挿入における高速化が期待できる（らしい）。
     * @param other
     * @return
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ChannelPlayer other) {
        return this.toString().compareTo(other.toString());
    }

    /**
     * 名前またはUUIDから、ChannelPlayerを作成して返す
     * @param nameOrUuid 名前、または、"$" + UUID
     * @return ChannelPlayer
     */
    public static ChannelPlayer getChannelPlayer(String nameOrUuid) {
        if( nameOrUuid == null )
            return null;
        if ( nameOrUuid.startsWith("$") ) {
            String id = nameOrUuid.substring(1);
            return new ChannelPlayerUUID(id);
        }
        if ( Utility.isCB178orLater() ) {
            ChannelPlayer player =
                    ChannelPlayerUUID.getChannelPlayerUUIDFromName(nameOrUuid);
            if ( player != null ) {
                return player;
            }
        }
        return new ChannelPlayerName(nameOrUuid);
    }

    /**
     * CommandSenderから、ChannelPlayerを作成して返す
     * @param sender
     * @return ChannelPlayer
     */
    public static ChannelPlayer getChannelPlayer(CommandSender sender) {
        if ( sender == null ) {
            return null;
        } else if ( sender instanceof BlockCommandSender ) {
            return new ChannelPlayerBlock((BlockCommandSender)sender);
        } else if ( sender instanceof ConsoleCommandSender ) {
            return new ChannelPlayerConsole((ConsoleCommandSender)sender);
        } else if ( Utility.isCB178orLater() ) {
            return ChannelPlayerUUID.getChannelPlayer(sender);
        }
        return new ChannelPlayerName(sender.getName());
    }
}
