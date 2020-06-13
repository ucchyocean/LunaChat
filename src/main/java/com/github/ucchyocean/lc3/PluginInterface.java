/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;

/**
 * プラグインインターフェイス
 * @author ucchy
 */
public interface PluginInterface {

    /**
     * このプラグインのJarファイル自身を示すFileクラスを返す。
     * @return Jarファイル
     */
    public File getPluginJarFile();

    /**
     * LunaChatConfigを取得する
     * @return LunaChatConfig
     */
    public LunaChatConfig getLunaChatConfig();

    /**
     * LunaChatAPIを取得する
     * @return LunaChatAPI
     */
    public LunaChatAPI getLunaChatAPI();

    /**
     * プラグインのデータ格納フォルダを取得する
     * @return データ格納フォルダ
     */
    public File getDataFolder();

    /**
     * 通常チャット用のロガーを返す
     * @return normalChatLogger
     */
    public LunaChatLogger getNormalChatLogger();

    /**
     * オンラインのプレイヤー名一覧を取得する
     * @return オンラインのプレイヤー名一覧
     */
    public Set<String> getOnlinePlayerNames();

    /**
     * このプラグインのログを記録する
     * @param level ログレベル
     * @param msg ログメッセージ
     */
    public void log(Level level, String msg);

    /**
     * UUIDキャッシュデータを取得する
     * @return UUIDキャッシュデータ
     */
    public UUIDCacheData getUUIDCacheData();

    /**
     * 非同期タスクを実行する
     * @param task タスク
     */
    public void runAsyncTask(Runnable task);
}
