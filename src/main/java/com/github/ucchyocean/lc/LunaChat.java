/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

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

    protected static PermissionsEx pex;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        // 変数などの初期化
        instance = this;
        config = new LunaChatConfig();
        manager = new ChannelManager();
        inviteMap = new HashMap<String, String>();
        inviterMap = new HashMap<String, String>();
        privateMessageMap = new HashMap<String, String>();

        // PermissionsExのロード
        Plugin temp = getServer().getPluginManager().getPlugin("PermissionsEx");
        if ( temp != null && temp instanceof PermissionsEx ) {
            pex = (PermissionsEx)temp;
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
}
