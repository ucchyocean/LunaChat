/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.util.Collection;

import org.bukkit.Bukkit;
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

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    public static boolean existsOfflinePlayer(String name) {
        return (getOfflinePlayer(name) != null);
    }
}
