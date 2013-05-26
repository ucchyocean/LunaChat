/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

/**
 * @author ucchy
 * チャンネル削除イベント
 */
public class LunaChatChannelRemoveEvent extends LunaChatBaseEvent {

    public LunaChatChannelRemoveEvent(String channelName) {
        super(channelName);
    }
}
