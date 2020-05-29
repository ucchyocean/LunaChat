/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;

/**
 * LunaChat
 * @author ucchy
 */
public class LunaChat {

    private static PluginInterface instance;
    private static LunaChatMode mode;

    public static void setPlugin(PluginInterface plugin) {
        instance = plugin;
    }

    public static PluginInterface getPlugin() {
        return instance;
    }

    public static void setMode(LunaChatMode _mode) {
        mode = _mode;
    }

    public static LunaChatMode getMode() {
        return mode;
    }


//    public static EventSenderInterface getEventSender() {
//        // TODO 未実装
//        return null;
//    }

    public static File getDataFolder() {
        return instance.getDataFolder();
    }

    public static File getPluginJarFile() {
        return instance.getPluginJarFile();
    }

    public static LunaChatConfig getConfig() {
        return instance.getLunaChatConfig();
    }

    public static LunaChatAPI getAPI() {
        return instance.getLunaChatAPI();
    }

    public static LunaChatLogger getNormalChatLogger() {
        return instance.getNormalChatLogger();
    }
}