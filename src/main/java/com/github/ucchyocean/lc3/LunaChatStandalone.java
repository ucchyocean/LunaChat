/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;

import com.github.ucchyocean.lc3.channel.ChannelManager;

/**
 * LunaChatのスタンドアロンサーバー
 * @author ucchy
 */
public class LunaChatStandalone implements PluginInterface {

    private LunaChatConfig config;
    private ChannelManager manager;
    private UUIDCacheData uuidCacheData;
    private File dataFolder;

    public LunaChatStandalone(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void onEnable() {

        LunaChat.setPlugin(this);
        LunaChat.setMode(LunaChatMode.STANDALONE);

        // 初期化
        manager = new ChannelManager();

        // コンフィグ取得
        config = new LunaChatConfig(getDataFolder(), getPluginJarFile());

        // UUIDキャッシュデータ
        uuidCacheData = new UUIDCacheData(getDataFolder());
    }

    @Override
    public File getPluginJarFile() {
        try {
            return new File(LunaChatStandalone.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LunaChatConfig getLunaChatConfig() {
        return config;
    }

    @Override
    public LunaChatAPI getLunaChatAPI() {
        return manager ;
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public LunaChatLogger getNormalChatLogger() {
        return null;
    }

    /**
     * オンラインのプレイヤー名一覧を取得する
     * @return オンラインのプレイヤー名一覧
     */
    public Set<String> getOnlinePlayerNames() {
        return null;
    }

    @Override
    public void log(Level level, String msg) {
        // 画面に表示する
        System.out.println(String.format("[%s]%s", level.toString(), msg));
    }

    /**
     * UUIDキャッシュデータを取得する
     * @return UUIDキャッシュデータ
     * @see com.github.ucchyocean.lc3.PluginInterface#getUUIDCacheData()
     */
    @Override
    public UUIDCacheData getUUIDCacheData() {
        return uuidCacheData;
    }

    /**
     * 非同期タスクを実行する
     * @param task タスク
     * @see com.github.ucchyocean.lc3.PluginInterface#runAsyncTask(java.lang.Runnable)
     */
    @Override
    public void runAsyncTask(Runnable task) {
        new Thread(task).start();
    }
}
