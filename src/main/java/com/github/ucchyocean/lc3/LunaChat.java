/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;

import com.github.ucchyocean.lc3.event.EventSenderInterface;

/**
 * LunaChat
 * @author ucchy
 */
public class LunaChat {

    /** Bukkit->BungeeCord チャット発言内容の転送に使用するプラグインメッセージチャンネル名 */
    public static final String PMC_MESSAGE = "lunachat:message";

    /** Bukkit->BungeeCord API実行処理の転送に使用するプラグインメッセージチャンネル名 */
    public static final String PMC_API = "lunachat:api";

    /** BungeeCord->Bukkit イベント通知の転送に使用するプラグインメッセージチャンネル名 */
    public static final String PMC_EVENT = "lunachat:event";

    private static PluginInterface instance;
    private static LunaChatMode mode;
    private static EventSenderInterface esender;

    static void setPlugin(PluginInterface plugin) {
        instance = plugin;
    }

    public static PluginInterface getPlugin() {
        return instance;
    }

    static void setMode(LunaChatMode _mode) {
        mode = _mode;
    }

    public static LunaChatMode getMode() {
        return mode;
    }

    static void setEventSender(EventSenderInterface eventSender) {
        esender = eventSender;
    }

    public static EventSenderInterface getEventSender() {
        return esender;
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

    public static LunaChatAPI getAPI() {
        return instance.getLunaChatAPI();
    }

    public static LunaChatLogger getNormalChatLogger() {
        return instance.getNormalChatLogger();
    }

    public static UUIDCacheData getUUIDCacheData() {
        return instance.getUUIDCacheData();
    }

    public static void runAsyncTask(Runnable task) {
        instance.runAsyncTask(task);
    }
}