/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.channel;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
     * 指定されたPlayerと同一かどうかを返す
     * @param player プレイヤー
     * @return 同一かどうか
     */
    public abstract boolean equals(Player player);

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
        if ( nameOrUuid.startsWith("$") ) {
            String id = nameOrUuid.substring(1);
            return new ChannelPlayerUUID(id);
        }
        if ( isCB175orLater() ) {
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
        }
        if ( isCB175orLater() ) {
            return ChannelPlayerUUID.getChannelPlayer(sender);
        }
        return new ChannelPlayerName(sender.getName());
    }

    /**
     * 現在動作中のCraftBukkitが、v1.7.5 以上かどうかを確認する
     * @return v1.7.5以上ならtrue、そうでないならfalse
     */
    private static boolean isCB175orLater() {

        int[] borderNumbers = {1, 7, 5};

        String version = Bukkit.getBukkitVersion();
        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") )
                return false;
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }
}
