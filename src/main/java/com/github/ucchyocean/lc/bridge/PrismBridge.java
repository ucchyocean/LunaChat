/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2016
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.LunaChat;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.botsko.prism.actionlibs.RecordingQueue;

/**
 * Prism連携クラス
 * @author ucchy
 */
public class PrismBridge {

    /** コンストラクタは使用不可 */
    private PrismBridge() {
    }

    /**
     * Prism をロードする
     * @return ロードしたブリッジのインスタンス
     */
    public static PrismBridge load() {
        return new PrismBridge();
    }

    /**
     * PrismにCHATログを書き出す
     * @param player プレイヤー
     * @param data データ
     */
    public void writeLog(final Player player, final String data) {
        if ( !Prism.getIgnore().event( "player-chat", player ) ) {
            return;
        }
        new BukkitRunnable() {
            public void run() {
                RecordingQueue.addToQueue( ActionFactory.createPlayer(
                        "player-chat", player, data ) );
            }
        }.runTaskAsynchronously(LunaChat.getInstance());
    }
}
