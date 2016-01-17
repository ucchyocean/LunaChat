/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ucchyocean.lc.bridge.DynmapBridge;
import com.github.ucchyocean.lc.bridge.HawkEyeBridge;
import com.github.ucchyocean.lc.bridge.McMMOBridge;
import com.github.ucchyocean.lc.bridge.MultiverseCoreBridge;
import com.github.ucchyocean.lc.bridge.VaultChatBridge;
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

    private static LunaChat instance;

    private LunaChatConfig config;
    private ChannelManager manager;

    private VaultChatBridge vaultchat;
    private DynmapBridge dynmap;
    private HawkEyeBridge hawkeye;
    private MultiverseCoreBridge multiverse;

    private ExpireCheckTask expireCheckerTask;
    private LunaChatLogger normalChatLogger;

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
        config = new LunaChatConfig();
        manager = new ChannelManager();
        normalChatLogger = new LunaChatLogger("==normalchat");

        // チャンネルチャット無効なら、デフォルト発言先をクリアする(see issue #59)
        if ( !config.isEnableChannelChat() ) {
            manager.removeAllDefaultChannels();
        }

        // Vault のロード
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

        // HawkEye のロード
        if ( getServer().getPluginManager().isPluginEnabled("HawkEye") ) {
            hawkeye = new HawkEyeBridge(this);
        }

        // MultiverseCore のロード
        temp = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if ( temp != null ) {
            multiverse = MultiverseCoreBridge.load(temp);
        }

        // mcMMOのロード
        if ( getServer().getPluginManager().isPluginEnabled("mcMMO") ) {
            getServer().getPluginManager().registerEvents(new McMMOBridge(), this);
        }

        // リスナーの登録
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // コマンドの登録
        lunachatCommand = new LunaChatCommand();
        messageCommand = new LunaChatMessageCommand();
        replyCommand = new LunaChatReplyCommand();
        lcjapanizeCommand = new LunaChatJapanizeCommand();

        // 期限チェッカータスクの起動
        expireCheckerTask = new ExpireCheckTask();
        expireCheckerTask.runTaskTimerAsynchronously(this, 100, 600);
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
        if ( instance == null ) {
            instance = (LunaChat)Bukkit.getPluginManager().getPlugin("LunaChat");
        }
        return instance;
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return Jarファイル
     */
    protected static File getPluginJarFile() {
        return getInstance().getFile();
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
     * HawkEye連携クラスを返す
     * @return HawkEyeBridge
     */
    public HawkEyeBridge getHawkEye() {
        return hawkeye;
    }

    /**
     * MultiverseCore連携クラスを返す
     * @return MultiverseCoreBridge
     */
    public MultiverseCoreBridge getMultiverseCore() {
        return multiverse;
    }

    /**
     * 通常チャット用のロガーを返す
     * @return normalChatLogger
     */
    public LunaChatLogger getNormalChatLogger() {
        return normalChatLogger;
    }

    /**
     * 通常チャット用のロガーを設定する
     * @param normalChatLogger normalChatLogger
     */
    protected void setNormalChatLogger(LunaChatLogger normalChatLogger) {
        this.normalChatLogger = normalChatLogger;
    }
}
