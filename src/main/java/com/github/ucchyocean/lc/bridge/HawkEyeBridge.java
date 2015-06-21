/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

/**
 * HawkEye連携クラス
 * @author ucchy
 */
public class HawkEyeBridge {

    private JavaPlugin lunaChatInstance;

    public HawkEyeBridge(JavaPlugin lunaChatInstance) {
        this.lunaChatInstance = lunaChatInstance;
    }

    /**
     * HawkEyeにログを書き出す
     * @param action アクション
     * @param player プレイヤー
     * @param location 場所
     * @param data データ
     */
    public void writeLog(String action, Player player, Location location, String data) {
        HawkEyeAPI.addCustomEntry(lunaChatInstance, action, player, location, data);
    }

    /**
     * HawkEyeにログを書き出す
     * @param action アクション
     * @param player プレイヤー名
     * @param location 場所
     * @param data データ
     */
    public void writeLog(String action, String player, Location location, String data) {
        HawkEyeAPI.addCustomEntry(lunaChatInstance, action, player, location, data);
    }
}
