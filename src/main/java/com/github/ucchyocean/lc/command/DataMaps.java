/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import java.util.HashMap;

/**
 * データマップ
 * @author ucchy
 */
public class DataMaps {

    /** 招待された人→招待されたチャンネル名 のマップ */
    protected static HashMap<String, String> inviteMap;

    /** 招待された人→招待した人 のマップ */
    protected static HashMap<String, String> inviterMap;

    /** tell/rコマンドの送信者→受信者 のマップ */
    protected static HashMap<String, String> privateMessageMap;

    static {
        inviteMap = new HashMap<String, String>();
        inviterMap = new HashMap<String, String>();
        privateMessageMap = new HashMap<String, String>();
    }
}
