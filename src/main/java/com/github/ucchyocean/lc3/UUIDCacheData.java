/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

/**
 * UUIDのキャッシュデータを管理するクラス
 * @author ucchy
 */
public class UUIDCacheData {

    private static final String FILE_NAME = "uuidcache.yml";

    // キャッシュデータ key=UUID文字列、value=プレイヤー名
    private YamlConfig cache;

    private File dataFolder;

    /**
     * コンストラクタ
     * @param dataFolder プラグインのデータ格納フォルダ
     */
    public UUIDCacheData(File dataFolder) {
        cache = new YamlConfig();
        this.dataFolder = dataFolder;
        reload();
    }

    /**
     * キャッシュデータを読み込む
     */
    public void reload() {
        File file = new File(dataFolder, FILE_NAME);
        if ( !file.exists() ) {
            // キャッシュファイルがまだ無いなら、からファイルを作成しておく。
            cache = new YamlConfig();
            try {
                cache.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        cache = YamlConfig.load(file);
    }

    /**
     * キャッシュデータをファイルに保存する
     */
    public void save() {
        File file = new File(dataFolder, FILE_NAME);
        try {
            cache.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * プレイヤーのUUIDとプレイヤー名を追加する。
     * @param uuid UUID
     * @param name プレイヤー名
     */
    public void put(String uuid, String name) {
        cache.set(uuid, name);
    }

    /**
     * プレイヤーのUUIDからプレイヤー名を取得する。
     * @param uuid UUID
     * @return プレイヤー名（キャッシュされていない場合はnullが返される）
     */
    public @Nullable String get(String uuid) {
        return cache.getString(uuid);
    }
}
