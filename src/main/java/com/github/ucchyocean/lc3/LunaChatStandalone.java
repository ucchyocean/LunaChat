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

    public void onEnable() {

        LunaChat.setPlugin(this);
        LunaChat.setMode(LunaChatMode.STANDALONE);

        // 初期化
        manager = new ChannelManager();

        // コンフィグ取得
        config = new LunaChatConfig(getDataFolder(), getPluginJarFile());

        // UUIDキャッシュデータ （これ要る？）
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

        // 現在のフォルダ下にLunaChatフォルダを作成して返す
        String cd = new File(".").getAbsoluteFile().getParent();
        File folder = new File(cd, "LunaChat");
        if ( !folder.exists() ) folder.mkdirs();
        return folder;
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
        // TODO Standaloneサーバーのログ出力 未実装
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
