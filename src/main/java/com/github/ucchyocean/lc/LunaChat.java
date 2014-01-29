/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.ucchyocean.lc.bridge.DynmapBridge;
import com.github.ucchyocean.lc.bridge.VaultChatBridge;
import com.github.ucchyocean.lc.command.LunaChatCommand;
import com.github.ucchyocean.lc.command.LunaChatJapanizeCommand;
import com.github.ucchyocean.lc.command.LunaChatMessageCommand;
import com.github.ucchyocean.lc.command.LunaChatReplyCommand;

/**
 * LunaChat プラグイン
 * @author ucchy
 */
public class LunaChat extends JavaPlugin {

    public static LunaChat instance;
    protected static LunaChatConfig config;
    protected static ChannelManager manager;

    protected static VaultChatBridge vaultchat;
    protected static DynmapBridge dynmap;
    
    private static BukkitTask expireCheckerTask;
    
    private LunaChatCommand mainCommand;

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

        // Chat Plugin のロード
        Plugin temp = getServer().getPluginManager().getPlugin("Vault");
        if ( temp != null ) {
            vaultchat = VaultChatBridge.load(temp);
        }

        // Dynmap のロード
        temp = getServer().getPluginManager().getPlugin("dynmap");
        if ( temp != null ) {
            dynmap = DynmapBridge.load(temp);
            if ( dynmap != null ) {
                getServer().getPluginManager().registerEvents(dynmap, this);
            }
        }

        // リスナーの登録
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // コマンドの登録
        mainCommand = new LunaChatCommand();
        getCommand("lunachat").setExecutor(mainCommand);
        getCommand("message").setExecutor(new LunaChatMessageCommand());
        getCommand("reply").setExecutor(new LunaChatReplyCommand());
        getCommand("lcjapanize").setExecutor(new LunaChatJapanizeCommand());

        // シリアル化可能オブジェクトの登録
        ConfigurationSerialization.registerClass(Channel.class, "Channel");
        
        // 期限チェッカータスクの起動
        expireCheckerTask =
                getServer().getScheduler().runTaskTimer(
                        this, new ExpireCheckTask(), 100, 1200);
    }

    /**
     * プラグインが無効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {
        
        // 期限チェッカータスクの停止
        if ( expireCheckerTask != null ) {
            getServer().getScheduler().cancelTask(expireCheckerTask.getTaskId());
        }
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     * @see org.bukkit.plugin.java.JavaPlugin#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        List<String> completeList = null;
        if ( command.getName().equals("lunachat") ) {
            completeList = mainCommand.onTabComplete(sender, command, label, args);
        }
        if ( completeList != null ) {
            return completeList;
        }
        return super.onTabComplete(sender, command, label, args);
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return
     */
    protected static File getPluginJarFile() {
        return instance.getFile();
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
