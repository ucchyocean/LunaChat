/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee.event;

import net.md_5.bungee.api.plugin.Cancellable;

/**
 * 基底のキャンセル可能イベントクラス
 * @author ucchy
 */
public class LunaChatBungeeBaseCancellableEvent extends LunaChatBungeeBaseEvent implements Cancellable {

    private boolean isCancelled;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     */
    public LunaChatBungeeBaseCancellableEvent(String channelName) {
        super(channelName);
    }

    /**
     * イベントがキャンセルされたかどうかをかえす
     * @see org.bukkit.event.Cancellable#isCancelled()
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * イベントをキャンセルするかどうかを設定する
     * @see org.bukkit.event.Cancellable#setCancelled(boolean)
     */
    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
