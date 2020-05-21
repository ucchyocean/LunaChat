/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import net.md_5.bungee.api.ProxyServer;

/**
 *
 * @author ucchy
 */
public class BungeeUtility {

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    @SuppressWarnings("deprecation")
    public static boolean existsOfflinePlayer(String name) {
        // TODO たぶんこの実装であっていると思う・・・
        return (ProxyServer.getInstance().getPlayer(name) != null);
    }

}
