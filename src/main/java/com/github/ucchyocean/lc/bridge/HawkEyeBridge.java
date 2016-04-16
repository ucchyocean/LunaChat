/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

/**
 * HawkEye連携クラス
 * @author ucchy
 */
public class HawkEyeBridge {

    /**
     * HawkEyeにCHATログを書き出す
     * @param action アクション
     * @param player プレイヤー
     * @param location 場所
     * @param data データ
     */
    public void writeLog(String action, Player player, Location location, String data) {
        HawkEyeAPI.addEntry(new DataEntry(player, DataType.CHAT, location, data));
    }

    /**
     * HawkEyeにCHATログを書き出す
     * @param action アクション
     * @param player プレイヤー名
     * @param location 場所
     * @param data データ
     */
    public void writeLog(String action, String player, Location location, String data) {
        HawkEyeAPI.addEntry(new DataEntry(player, DataType.CHAT, location, data));
    }
}
