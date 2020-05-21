/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.io.File;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.LunaChatLogger;
import com.github.ucchyocean.lc.PluginInterface;
import com.github.ucchyocean.lc.channel.ChannelManager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * LunaChat プラグイン
 * @author ucchy
 */
public class LunaChatBungee extends Plugin implements PluginInterface {

    private static LunaChatBungee instance;

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

        // 変数などの初期化
        config = new LunaChatConfig();
        manager = new ChannelManager();
        normalChatLogger = new LunaChatLogger("==normalchat");

        // チャンネルチャット無効なら、デフォルト発言先をクリアする(see issue #59)
        if ( !config.isEnableChannelChat() ) {
            manager.removeAllDefaultChannels();
        }

        // TODO 連携プラグインのロード

        // リスナーの登録
        getProxy().getPluginManager().registerListener(this, new BungeeListener());

        // コマンドの登録
        // TODO 未実装
//        for ( String command : new String[]{
//                "tell", "msg", "message", "m", "w", "t"}) {
//            getProxy().getPluginManager().registerCommand(
//                    this, new TellCommand(this, command));
//        }
//        for ( String command : new String[]{"reply", "r"}) {
//            getProxy().getPluginManager().registerCommand(
//                    this, new ReplyCommand(this, command));
//        }
//        for ( String command : new String[]{"dictionary", "dic"}) {
//            getProxy().getPluginManager().registerCommand(
//                    this, new DictionaryCommand(this, command));
//        }

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
    public LunaChatLogger getNormalChatLogger() {
        return normalChatLogger;
    }
}
