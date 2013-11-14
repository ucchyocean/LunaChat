/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

/**
 * チャンネル削除イベント
 * @author ucchy
 */
public class LunaChatChannelRemoveEvent extends LunaChatBaseCancellableEvent {

    public LunaChatChannelRemoveEvent(String channelName) {
        super(channelName);
    }
}
