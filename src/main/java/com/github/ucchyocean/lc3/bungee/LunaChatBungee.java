/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.LunaChatLogger;
import com.github.ucchyocean.lc3.LunaChatMode;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.PluginInterface;
import com.github.ucchyocean.lc3.channel.ChannelManager;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
        config = new LunaChatConfig(getDataFolder(), getFile());
        Messages.initialize(new File(getDataFolder(), "messages"), getFile(), config.getLang());
        history = new HashMap<String, String>();

        manager = new ChannelManager();
        normalChatLogger = new LunaChatLogger("==normalchat");

        // チャンネルチャット無効なら、デフォルト発言先をクリアする
        if ( !config.isEnableChannelChat() ) {
            manager.removeAllDefaultChannels();
        }

        // コマンド登録
        getProxy().getPluginManager().registerCommand(this,
                new LunaChatCommandImpl("lunachat", "", "lc", "ch"));
        getProxy().getPluginManager().registerCommand(this,
                new TellCommand(this, "tell", "", "msg", "message", "m", "w", "t"));
        getProxy().getPluginManager().registerCommand(this,
                new ReplyCommand(this, "reply", "", "r"));
        getProxy().getPluginManager().registerCommand(this,
                new DictionaryCommand(this, "dictionary", "", "dic"));

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
     * @see com.github.ucchyocean.lc3.PluginInterface#getPluginJarFile()
     */
    @Override
    public File getPluginJarFile() {
        return getFile();
    }

    /**
     * LunaChatConfigを取得する
     * @return LunaChatConfig
     * @see com.github.ucchyocean.lc3.PluginInterface#getLunaChatConfig()
     */
    @Override
    public LunaChatConfig getLunaChatConfig() {
        return config;
    }

    /**
     * LunaChatAPIを取得する
     * @return LunaChatAPI
     * @see com.github.ucchyocean.lc3.PluginInterface#getLunaChatAPI()
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

    /**
     * オンラインのプレイヤー名一覧を取得する
     * @return オンラインのプレイヤー名一覧
     */
    @Override
    public Set<String> getOnlinePlayerNames() {
        Set<String> list = new HashSet<>();
        for ( ProxiedPlayer p : ProxyServer.getInstance().getPlayers() ) {
            list.add(p.getName());
        }
        return list;
    }

    /**
     * このプラグインのログを記録する
     * @param level ログレベル
     * @param msg ログメッセージ
     */
    @Override
    public void log(Level level, String msg) {
        getLogger().log(level, msg);
    }
}
