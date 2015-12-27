/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2015
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.Core;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

/**
 * MultiverseCore連携クラス
 * @author ucchy
 */
public class MultiverseCoreBridge {

    /** MultiverseCore API クラス */
    private Core mvc;

    /** コンストラクタは使用不可 */
    private MultiverseCoreBridge() {
    }

    /**
     * MultiverseCore-apiをロードする
     * @param plugin MultiverseCoreのプラグインインスタンス
     * @param ロードしたかどうか
     */
    public static MultiverseCoreBridge load(Plugin plugin) {

        if ( plugin instanceof MultiverseCore ) {
            MultiverseCoreBridge bridge = new MultiverseCoreBridge();
            bridge.mvc = (Core)plugin;
            return bridge;
        } else {
            return null;
        }
    }

    /**
     * 指定されたワールドのエイリアス名を取得する
     * @param worldName ワールド名
     * @return エイリアス名、取得できない場合はnullが返される
     */
    public String getWorldAlias(String worldName) {

        MultiverseWorld mvworld = mvc.getMVWorldManager().getMVWorld(worldName);
        if ( mvworld != null ) {
            return mvworld.getAlias();
        } else {
            return null;
        }
    }

    /**
     * 指定されたワールドのエイリアス名を取得する
     * @param world ワールド
     * @return エイリアス名、取得できない場合はnullが返される
     */
    public String getWorldAlias(World world) {

        MultiverseWorld mvworld = mvc.getMVWorldManager().getMVWorld(world);
        if ( mvworld != null ) {
            return mvworld.getAlias();
        } else {
            return null;
        }
    }
}
