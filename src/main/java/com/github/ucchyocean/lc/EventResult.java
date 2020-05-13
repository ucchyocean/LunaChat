/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.util.HashMap;
import java.util.Map;

/**
 * イベントの実行結果を格納するクラス
 * @author ucchy
 */
public class EventResult {

    private Map<String, Object> map = new HashMap<>();
    private boolean isCancelled = false;

    public void setValue(String key, Object value) {
        map.put(key, value);
    }

    public Object getValue(String key) {
        return map.get(key);
    }

    public String getValueAsString(String key) {
        return map.get(key).toString();
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getValueAsStringMap(String key) {
        Object obj = map.get(key);
        if ( obj == null || !(obj instanceof HashMap<?, ?>) ) return null;
        return (HashMap<String, String>)map.get(key);
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
