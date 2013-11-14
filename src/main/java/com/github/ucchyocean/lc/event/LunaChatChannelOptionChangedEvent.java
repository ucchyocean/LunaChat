/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.HashMap;

/**
 * オプション変更イベント
 * @author ucchy
 */
public class LunaChatChannelOptionChangedEvent extends LunaChatBaseCancellableEvent {

    private HashMap<String, String> options;

    public LunaChatChannelOptionChangedEvent(String channelName,
            HashMap<String, String> options) {
        super(channelName);
        this.options = options;
    }

    /**
     * 変更後のオプションリストをかえす
     * @return オプションリスト
     */
    public HashMap<String, String> getOptions() {
        return options;
    }

    /**
     * オプションリストを上書き設定する
     * @param options オプションリスト
     */
    public void setOptions(HashMap<String, String> options) {
        this.options = options;
    }
}
