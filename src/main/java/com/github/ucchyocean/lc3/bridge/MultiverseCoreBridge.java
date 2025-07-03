/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bridge;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.external.vavr.control.Option;

/**
 * MultiverseCore連携クラス
 * @author ucchy
 */
public class MultiverseCoreBridge {

    /** MultiverseCore API クラス */
    private MultiverseCoreApi mvc;

    /** コンストラクタは使用不可 */
    private MultiverseCoreBridge() {
    }

    /**
     * MultiverseCore-apiをロードする
     * @param plugin MultiverseCoreのプラグインインスタンス
     */
    public static MultiverseCoreBridge load(Plugin plugin) {
        if ( plugin instanceof MultiverseCore ) {
            MultiverseCoreBridge bridge = new MultiverseCoreBridge();
            bridge.mvc = (MultiverseCoreApi) plugin;
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
        Option<MultiverseWorld> option = mvc.getWorldManager().getWorld(worldName);
        if (option.isDefined()) {
            MultiverseWorld mvworld = option.get();
            if (!mvworld.getAlias().isEmpty()) {
                return mvworld.getAlias();
            } else {
                return mvworld.getName();
            }
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
        Option<MultiverseWorld> option = mvc.getWorldManager().getWorld(world);
        if (option.isDefined()) {
            MultiverseWorld mvworld = option.get();
            if (!mvworld.getAlias().isEmpty()) {
                return mvworld.getAlias();
            } else {
                return mvworld.getName();
            }
        } else {
            return null;
        }
    }
}
