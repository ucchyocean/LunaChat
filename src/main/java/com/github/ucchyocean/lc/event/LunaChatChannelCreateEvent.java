/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

/**
 * @author ucchy
 * チャンネル作成イベント
 */
public class LunaChatChannelCreateEvent extends LunaChatBaseEvent {

    public LunaChatChannelCreateEvent(String channelName) {
        super(channelName);
    }

    /**
     * @param channelName 設定するチャンネル名
     */
    protected void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
