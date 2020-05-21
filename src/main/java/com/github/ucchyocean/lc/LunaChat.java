/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.io.File;

import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;

/**
 * LunaChat
 * @author ucchy
 */
public class LunaChat {

    private static PluginInterface instance;

    public static void setPlugin(PluginInterface plugin) {
        instance = plugin;
    }

    public static PluginInterface getPlugin() {
        return instance;
    }

    public static boolean isBukkitMode() {
        return instance instanceof LunaChatBukkit;
    }

    public static EventSenderInterface getEventSender() {
        // TODO 未実装
        return null;
    }

    public static File getDataFolder() {
        return instance.getDataFolder();
    }

    public static File getPluginJarFile() {
        return instance.getPluginJarFile();
    }

    public static LunaChatConfig getConfig() {
        return instance.getLunaChatConfig();
    }
}
