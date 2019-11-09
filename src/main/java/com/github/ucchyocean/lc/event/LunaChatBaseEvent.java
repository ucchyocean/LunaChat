/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.channel.Channel;

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
        super(!Bukkit.isPrimaryThread());
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
     * チャンネル名を取得する
     * @return チャンネル名
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * チャンネルを取得する
     * @return チャンネル
     */
    public Channel getChannel() {
        return LunaChat.getInstance().getLunaChatAPI().getChannel(channelName);
    }
}
