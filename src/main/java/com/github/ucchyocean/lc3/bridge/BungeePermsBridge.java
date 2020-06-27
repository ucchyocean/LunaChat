/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bridge;

import net.alpenblock.bungeeperms.BungeePermsAPI;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * BungeePerms連携クラス
 * @author ucchy
 */
public class BungeePermsBridge {

    // コンストラクタは外から利用不可
    private BungeePermsBridge() {
    }

    /**
     * BungeePermsをロードする
     * @param plugin BungeePermsのインスタンス
     * @return ロードされたBungeePermsBridge
     */
    public static BungeePermsBridge load(Plugin plugin) {

        // BungeePermsが指定されたかどうかを確認
        boolean loaded = (plugin != null);

        // ロードされているならBungeePermsBridgeのインスタンスを、ロードされていないならnullを返す
        return loaded ? new BungeePermsBridge() : null;
    }

    /**
     * Gets the full prefix of the player
     * @param nameoruuid the username or uuid of the player
     * @param server the server; may be null; if server == "" then the current server is used or null if BungeeCord
     * @param world the world; may be null
     * @return the full prefix; may be null
     */
    public String userPrefix(String nameoruuid, String server, String world) {
        return BungeePermsAPI.userPrefix(nameoruuid, server, world);
    }

    /**
     * Gets the full suffix of the player
     * @param nameoruuid the username or uuid of the player
     * @param server the server; may be null; if server == "" then the current server is used or null if BungeeCord
     * @param world the world; may be null
     * @return the full suffix; may be null
     */
    public String userSuffix(String nameoruuid, String server, String world) {
        return BungeePermsAPI.userSuffix(nameoruuid, server, world);
    }
}
