/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.HashMap;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author ucchy
 * LunaChat プラグイン
 */
public class LunaChat extends JavaPlugin {

    protected static LunaChat instance;
    protected static LunaChatConfig config;
    protected static ChannelManager manager;
    protected static HashMap<String, String> inviteMap;
    protected static HashMap<String, String> inviterMap;
    protected static HashMap<String, String> privateMessageMap;

    protected static Chat chatPlugin;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // 変数などの初期化
        instance = this;
        manager = new ChannelManager();
        config = new LunaChatConfig();
        inviteMap = new HashMap<String, String>();
        inviterMap = new HashMap<String, String>();
        privateMessageMap = new HashMap<String, String>();

        // Chat Plugin のロード
        Plugin temp = getServer().getPluginManager().getPlugin("Vault");
        if ( temp != null && temp instanceof Vault ) {
            RegisteredServiceProvider<Chat> chatProvider =
                    getServer().getServicesManager().getRegistration(Chat.class);
            if ( chatProvider != null ) {
                chatPlugin = chatProvider.getProvider();
            }
        }

        // リスナーの登録
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // コマンドの登録
        getCommand("lunachat").setExecutor(new LunaChatCommand());
        getCommand("message").setExecutor(new LunaChatMessageCommand());
        getCommand("reply").setExecutor(new LunaChatReplyCommand());

        // シリアル化可能オブジェクトの登録
        ConfigurationSerialization.registerClass(Channel.class, "Channel");
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return
     */
    protected static File getPluginJarFile() {
        return instance.getFile();
    }

    /**
     * ログにメッセージを出力する
     * @param message メッセージ
     */
    protected static void log(String message) {
        instance.getLogger().info(ChatColor.stripColor(message));
    }

    /**
     * Playerを取得する
     * @param name プレイヤー名
     * @return Player
     */
    protected static Player getPlayerExact(String name) {
        return instance.getServer().getPlayerExact(name);
    }

    /**
     * LunaChatAPIを取得する
     * @return LunaChatAPI
     */
    public LunaChatAPI getLunaChatAPI() {
        return manager;
    }

    /**
     * LunaChatConfigを取得する
     * @return LunaChatConfig
     */
    public LunaChatConfig getLunaChatConfig() {
        return config;
    }
}
