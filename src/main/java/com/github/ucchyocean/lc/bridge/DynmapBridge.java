/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;

/**
 * dynmap連携クラス
 * @author ucchy
 */
public class DynmapBridge {

    /** dynmap-apiクラス */
    private DynmapAPI dynmap;

    /** コンストラクタは使用不可 */
    private DynmapBridge() {
    }

    /**
     * dynmap-apiをロードする
     * @param plugin dynmap-apiのプラグインインスタンス
     * @param ロードしたかどうか
     */
    public static DynmapBridge load(Plugin plugin) {

        if ( plugin instanceof DynmapAPI ) {
            DynmapBridge bridge = new DynmapBridge();
            bridge.dynmap = (DynmapAPI)plugin;
            return bridge;
        } else {
            return null;
        }
    }

    /**
     * dynmapにプレイヤーのチャットを流す
     * @param player プレイヤー
     * @param message 発言内容
     */
    public void chat(Player player, String message) {

        dynmap.postPlayerMessageToWeb(player, message);
    }
    
    /**
     * dynmapにブロードキャストメッセージを流す
     * @param message メッセージ
     */
    public void broadcast(String message) {
        
        dynmap.sendBroadcastToWeb(null, message);
    }
}
