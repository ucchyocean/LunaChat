/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bukkit;

import org.bukkit.Bukkit;

/**
 *
 * @author ucchy
 */
public class BukkitUtility {

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    @SuppressWarnings("deprecation")
    public static boolean existsOfflinePlayer(String name) {
        return (Bukkit.getOfflinePlayer(name) != null);
    }
}
