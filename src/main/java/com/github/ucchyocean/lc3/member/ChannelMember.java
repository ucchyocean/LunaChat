/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.member;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatMode;

/**
 * チャンネルメンバーの抽象クラス
 * @author ucchy
 */
public abstract class ChannelMember implements Comparable<ChannelMember> {

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
     * 発言者が今いるワールドのワールド名を取得する
     * @return ワールド名
     */
    public abstract String getWorldName();

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     */
    public abstract boolean hasPermission(String node);

    /**
     * 文字列表現を返す
     * @return 名前管理なら名前、UUID管理なら "$" + UUID を返す
     */
    @Override
    public abstract String toString();

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     */
    public abstract boolean isPermissionSet(String node);

    /**
     * 同一のオブジェクトかどうかを返す
     * @param other 他方のオブジェクト
     * @return 同一かどうか
     */
    @Override
    public boolean equals(Object other) {
        if ( !(other instanceof ChannelMember) ) {
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
    public int compareTo(ChannelMember other) {
        return this.toString().compareTo(other.toString());
    }

    /**
     * 名前またはUUIDから、ChannelMemberを作成して返す
     * @param nameOrUuid 名前、または、"$" + UUID
     * @return ChannelMember
     */
    public static ChannelMember getChannelMember(String nameOrUuid) {
        if ( LunaChat.getMode() == LunaChatMode.BUKKIT ) {
            return ChannelMemberPlayer.getChannelMember(nameOrUuid);
        } else if ( LunaChat.getMode() == LunaChatMode.BUNGEE ) {
            return ChannelMemberProxiedPlayer.getChannelMember(nameOrUuid);
        }
        return null; // TODO standalone用のChannelMemberを返す
    }

    /**
     * オブジェクトから、ChannelMemberを作成して返す
     * @param obj
     * @return ChannelMember
     */
    public static ChannelMember getChannelMember(Object obj) {

        if ( obj != null && obj instanceof ChannelMember ) return (ChannelMember)obj;

        if ( LunaChat.getMode() == LunaChatMode.BUKKIT ) {
            return ChannelMemberBukkit.getChannelMemberBukkit(obj);
        } else if ( LunaChat.getMode() == LunaChatMode.BUNGEE ) {
            return ChannelMemberBungee.getChannelMemberBungee(obj);
        }
        return null; // TODO standalone用のChannelMemberを返す
    }
}
