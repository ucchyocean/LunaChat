/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.HashMap;

/**
 * @author ucchy
 * オプション変更イベント
 */
public class LunaChatChannelOptionChangedEvent extends LunaChatBaseEvent {

    private HashMap<String, String> options;

    public LunaChatChannelOptionChangedEvent(String channelName,
            HashMap<String, String> options) {
        super(channelName);
        this.options = options;
    }

    public HashMap<String, String> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }
}
