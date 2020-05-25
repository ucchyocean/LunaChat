/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Bukkit関連のユーティリティクラス
 * @author ucchy
 */
public class UtilityBukkit {

    /**
     * 現在接続中のプレイヤーを全て取得する
     * @return 接続中の全てのプレイヤー
     */
    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    /**
     * 現在のサーバー接続人数を返します。
     * @return サーバー接続人数
     */
    public static int getOnlinePlayersCount() {
        return getOnlinePlayers().size();
    }

    /**
     * ChatColorで指定可能な色かどうかを判断する
     * @param color カラー表記の文字列
     * @return 指定可能かどうか
     */
    public static boolean isValidColor(String color) {
        if ( color == null ) return false;
        for (ChatColor c : ChatColor.values()) {
            if (c.name().equals(color.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * カラー表記の文字列を、カラーコードに変換する
     * @param color カラー表記の文字列
     * @return カラーコード
     */
    public static String changeToColorCode(String color) {

        return "&" + changeToChatColor(color).getChar();
    }

    /**
     * カラー表記の文字列を、ChatColorクラスに変換する
     * @param color カラー表記の文字列
     * @return ChatColorクラス
     */
    public static ChatColor changeToChatColor(String color) {

        if (isValidColor(color)) {
            return ChatColor.valueOf(color.toUpperCase());
        }
        return ChatColor.WHITE;
    }

    /**
     * 指定された名前のオフラインプレイヤーを取得する
     * @param name プレイヤー名
     * @return オフラインプレイヤー
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(String name) {
        if (name == null) return null;
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null || (!player.hasPlayedBefore() && !player.isOnline()))
            return null;
        return player;
    }

    /**
     * 指定された名前のプレイヤーを取得する
     * @param name プレイヤー名
     * @return プレイヤー
     */
    public static Player getPlayerExact(String name) {
        return Bukkit.getPlayer(Utility.stripColorCode(name));
    }
}
