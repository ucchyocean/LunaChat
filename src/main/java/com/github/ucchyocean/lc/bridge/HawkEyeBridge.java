/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Utility;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

/**
 * HawkEye連携クラス
 * @author ucchy
 */
public class HawkEyeBridge {

    private boolean isV162orLater;

    /** コンストラクタは使用不可 */
    private HawkEyeBridge() {
    }

    /**
     * HawkEye Reloaded をロードする
     * @param plugin HawkEye Reloaded のプラグインインスタンス
     * @return ロードしたブリッジのインスタンス
     */
    public static HawkEyeBridge load(Plugin plugin) {

        String version = plugin.getDescription().getVersion();
        HawkEyeBridge instance = new HawkEyeBridge();
        instance.isV162orLater = Utility.isUpperVersion(version, "1.6.2");
        return instance;
    }

    /**
     * HawkEyeにCHATログを書き出す
     * @param player プレイヤー名
     * @param location 場所
     * @param data データ
     */
    @SuppressWarnings("deprecation")
    public void writeLog(String player, Location location, String data) {
        final DataEntry entry = new DataEntry(player, DataType.CHAT, location, data);
        new BukkitRunnable() {
            public void run() {
                if ( isV162orLater ) {
                    //TODO reason:なんか怒られてビルドできなかったので変更しています
                    // APIの仕様変更が原因でしょうけどどこから変更されたのか不明(1.6以前を参照しても怒られる)ので諦めました
                    //HawkEyeAPI.addEntry(entry);
                    HawkEyeAPI.addEntry(LunaChat.getInstance(), entry);
                } else {
                    HawkEyeAPI.addEntry(LunaChat.getInstance(), entry);
                }
            }
        }.runTaskAsynchronously(LunaChat.getInstance());
    }
}
