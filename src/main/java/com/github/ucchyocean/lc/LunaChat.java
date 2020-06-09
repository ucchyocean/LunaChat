/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

/**
 * LunaChat プラグイン
 * @author ucchy
 */
public class LunaChat {

    private static LunaChat instance = new LunaChat();
    private LunaChatAPI api = new LunaChatAPI();

    /**
     * LunaChatのインスタンスを返す
     * @return LunaChat
     * @deprecated Legacy Version
     */
    public static LunaChat getInstance() {
        return instance;
    }

    /**
     * LunaChatAPIを取得する
     * @return LunaChatAPI
     * @deprecated Legacy Version
     */
    public LunaChatAPI getLunaChatAPI() {
        return api;
    }
}
