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

import com.github.ucchyocean.lc.bridge.DynmapBridge;
import com.github.ucchyocean.lc.bridge.VaultChatBridge;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelManager;
import com.github.ucchyocean.lc.command.LunaChatCommand;
import com.github.ucchyocean.lc.command.LunaChatJapanizeCommand;
import com.github.ucchyocean.lc.command.LunaChatMessageCommand;
import com.github.ucchyocean.lc.command.LunaChatReplyCommand;

/**
 * LunaChat プラグイン
 * @author ucchy
 */
public class LunaChat extends JavaPlugin {

    private static final String FILE_NAME_DCHANNELS = "defaults.yml";

    private static LunaChat instance;

    private LunaChatConfig config;
    private ChannelManager manager;

    private VaultChatBridge vaultchat;
    private DynmapBridge dynmap;

    private ExpireCheckTask expireCheckerTask;

    private LunaChatCommand lunachatCommand;
    private LunaChatMessageCommand messageCommand;
    private LunaChatReplyCommand replyCommand;
    private LunaChatJapanizeCommand lcjapanizeCommand;

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
        lunachatCommand = new LunaChatCommand();
        messageCommand = new LunaChatMessageCommand();
        replyCommand = new LunaChatReplyCommand();
        lcjapanizeCommand = new LunaChatJapanizeCommand();

        // シリアル化可能オブジェクトの登録
        ConfigurationSerialization.registerClass(Channel.class, "Channel");

        // 期限チェッカータスクの起動
        expireCheckerTask = new ExpireCheckTask();
        expireCheckerTask.runTaskTimer(this, 100, 1200);
    }

    /**
     * プラグインが無効化されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {

        // 期限チェッカータスクの停止
        if ( expireCheckerTask != null ) {
            expireCheckerTask.cancel();
        }
    }

    /**
     * コマンド実行時に呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( command.getName().equals("lunachat") ) {
            return lunachatCommand.onCommand(sender, command, label, args);
        } else if ( command.getName().equals("tell") ) {
            return messageCommand.onCommand(sender, command, label, args);
        } else if ( command.getName().equals("reply") ) {
            return replyCommand.onCommand(sender, command, label, args);
        } else if ( command.getName().equals("lcjapanize") ) {
            return lcjapanizeCommand.onCommand(sender, command, label, args);
        }

        return false;
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @see org.bukkit.plugin.java.JavaPlugin#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

        List<String> completeList = null;
        if ( command.getName().equals("lunachat") ) {
            completeList = lunachatCommand.onTabComplete(sender, command, label, args);
        }
        if ( completeList != null ) {
            return completeList;
        }
        return super.onTabComplete(sender, command, label, args);
    }

    /**
     * LunaChatのインスタンスを返す
     * @return LunaChat
     */
    public static LunaChat getInstance() {
        return instance;
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return Jarファイル
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

    /**
     * VaultChat連携クラスを返す
     * @return VaultChatBridge
     */
    public VaultChatBridge getVaultChat() {
        return vaultchat;
    }

    /**
     * Dynmap連携クラスを返す
     * @return DynmapBridge
     */
    public DynmapBridge getDynmap() {
        return dynmap;
    }

    /**
     * 全てのデフォルトチャンネル設定を削除する
     */
    protected void removeAllDefaultChannels() {
        if ( manager != null ) {
            manager.removeAllDefaultChannels();
        } else {
            File fileDefaults = new File(
                    LunaChat.getInstance().getDataFolder(), FILE_NAME_DCHANNELS);
            if ( fileDefaults.exists() ) {
                fileDefaults.delete();
            }
        }
    }
}
