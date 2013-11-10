/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 基底イベントクラス
 * @author ucchy
 */
public abstract class LunaChatBaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    protected String channelName;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     */
    public LunaChatBaseEvent(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return チャンネル名
     */
    public String getChannelName() {
        return channelName;
    }
}
