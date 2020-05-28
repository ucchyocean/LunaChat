/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.io.File;
import java.util.HashMap;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.LunaChatLogger;
import com.github.ucchyocean.lc.LunaChatMode;
import com.github.ucchyocean.lc.PluginInterface;
import com.github.ucchyocean.lc.channel.ChannelManager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * LunaChatのBungeeCord実装
 * @author ucchy
 */
public class LunaChatBungee extends Plugin implements PluginInterface {

    private static LunaChatBungee instance;

    private HashMap<String, String> history;
    private LunaChatConfig config;
    private ChannelManager manager;
    private LunaChatLogger normalChatLogger;

    /**
     * プラグインが有効化されたときに呼び出されるメソッド
     * @see net.md_5.bungee.api.plugin.Plugin#onEnable()
     */
    @Override
    public void onEnable() {

        LunaChat.setPlugin(this);
        LunaChat.setMode(LunaChatMode.BUNGEE);

        // 初期化
        history = new HashMap<String, String>();

        manager = new ChannelManager();
        normalChatLogger = new LunaChatLogger("==normalchat");

        // チャンネルチャット無効なら、デフォルト発言先をクリアする
        if ( !config.isEnableChannelChat() ) {
            manager.removeAllDefaultChannels();
        }

        // コマンド登録
        for ( String command : new String[]{
                "tell", "msg", "message", "m", "w", "t"}) {
            getProxy().getPluginManager().registerCommand(
                    this, new TellCommand(this, command));
        }
        for ( String command : new String[]{"reply", "r"}) {
            getProxy().getPluginManager().registerCommand(
                    this, new ReplyCommand(this, command));
        }
        for ( String command : new String[]{"dictionary", "dic"}) {
            getProxy().getPluginManager().registerCommand(
                    this, new DictionaryCommand(this, command));
        }

        // コンフィグ取得
        config = new LunaChatConfig(getDataFolder(), getFile());

        // リスナー登録
        getProxy().getPluginManager().registerListener(this, new BungeeEventListener(this));
    }

    /**
     * LunaChatのインスタンスを返す
     * @return LunaChat
     */
    public static LunaChatBungee getInstance() {
        if ( instance == null ) {
            instance = (LunaChatBungee)ProxyServer.getInstance().getPluginManager().getPlugin("LunaChat");
        }
        return instance;
    }

    /**
     * コンフィグを返す
     * @return コンフィグ
     */
    public LunaChatConfig getConfig() {
        return config;
    }

    /**
     * プライベートメッセージの受信履歴を記録する
     * @param reciever 受信者
     * @param sender 送信者
     */
    protected void putHistory(String reciever, String sender) {
        history.put(reciever, sender);
    }

    /**
     * プライベートメッセージの受信履歴を取得する
     * @param reciever 受信者
     * @return 送信者
     */
    protected String getHistory(String reciever) {
        return history.get(reciever);
    }

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return Jarファイル
     * @see com.github.ucchyocean.lc.PluginInterface#getPluginJarFile()
     */
    @Override
    public File getPluginJarFile() {
        return getFile();
    }

    /**
     * LunaChatConfigを取得する
     * @return LunaChatConfig
     * @see com.github.ucchyocean.lc.PluginInterface#getLunaChatConfig()
     */
    @Override
    public LunaChatConfig getLunaChatConfig() {
        return config;
    }

    /**
     * LunaChatAPIを取得する
     * @return LunaChatAPI
     * @see com.github.ucchyocean.lc.PluginInterface#getLunaChatAPI()
     */
    @Override
    public LunaChatAPI getLunaChatAPI() {
        return manager;
    }

    /**
     * 通常チャット用のロガーを返す
     * @return normalChatLogger
     */
    @Override
    public LunaChatLogger getNormalChatLogger() {
        return normalChatLogger;
    }
}
