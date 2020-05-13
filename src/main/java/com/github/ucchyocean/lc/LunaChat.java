/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;

/**
 * LunaChat
 * @author ucchy
 */
public class LunaChat {

    private static LunaChatPlugin instance;

    public static void setInstance(LunaChatPlugin plugin) {
        instance = plugin;
    }

    public static LunaChatPlugin getInstance() {
        return instance;
    }

    public static boolean isBukkitMode() {
        return instance instanceof LunaChatBukkit;
    }

    public static EventSenderInterface getEventSender() {
        // TODO 未実装
        return null;
    }
}
