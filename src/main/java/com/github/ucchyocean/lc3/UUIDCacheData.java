/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * UUIDのキャッシュデータを管理するクラス
 * @author ucchy
 */
public class UUIDCacheData {

    // キャッシュデータ　key=UUID文字列、value=プレイヤー名
    private Map<String, String> caches;

    private File dataFolder;

    /**
     * コンストラクタ
     * @param dataFolder プラグインのデータ格納フォルダ
     */
    public UUIDCacheData(File dataFolder) {
        caches = new HashMap<String, String>();
        this.dataFolder = dataFolder;
        reload();
    }

    /**
     * キャッシュデータを読み込む
     */
    public void reload() {

        caches.clear();

        File file = new File(dataFolder, "uuidcache.yml");
        if ( !file.exists() ) {
            // キャッシュファイルがまだ無いなら、からファイルを作成しておく。
            YamlConfig yaml = new YamlConfig();
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        YamlConfig yaml = YamlConfig.load(file);

        for ( String uuid : yaml.getKeys(false) ) {
            caches.put(uuid, yaml.getString(uuid));
        }
    }

    /**
     * キャッシュデータをファイルに保存する
     */
    public void save() {

        YamlConfig yaml = new YamlConfig();
        for ( String uuid : caches.keySet() ) {
            yaml.set(uuid, caches.get(uuid));
        }

        File file = new File(dataFolder, "uuidcache.yml");
        try {
            yaml.save(file);
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
        caches.put(uuid, name);
    }

    /**
     * プレイヤーのUUIDからプレイヤー名を取得する。
     * @param uuid UUID
     * @return プレイヤー名（キャッシュされていない場合はnullが返される）
     */
    public @Nullable String get(String uuid) {
        return caches.get(uuid);
    }
}
