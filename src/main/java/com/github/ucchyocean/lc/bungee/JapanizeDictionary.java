/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.YamlConfig;

/**
 * Japanize変換に使用するユーザー辞書
 * @author ucchy
 */
public class JapanizeDictionary {

    private HashMap<String, String> dictionary;

    /**
     * コンストラクタ
     */
    public JapanizeDictionary() {
        reload();
    }

    /**
     * ファイルから設定を読み直す
     */
    public void reload() {

        dictionary = new HashMap<String, String>();

        // Fileを取得
        File file = new File(LunaChat.getDataFolder(), "dictionary.yml");
        if ( !file.exists() ) {
            return;
        }

        YamlConfig config = YamlConfig.load(file);

        for ( String key : config.getKeys(false) ) {
            dictionary.put(key, config.getString(key));
        }
    }

    /**
     * ファイルに保存する
     */
    public void save() {

        YamlConfig config = new YamlConfig();

        for ( String key : dictionary.keySet() ) {
            config.set(key, dictionary.get(key));
        }

        try {
            config.save(new File(LunaChat.getDataFolder(), "dictionary.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 辞書に単語を登録する
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        dictionary.put(key, value);
        save();
    }

    /**
     * 辞書から単語を消去する
     * @param key
     */
    public void remove(String key) {
        dictionary.remove(key);
        save();
    }

    /**
     * 全てのデータを返す
     * @return dictionary
     */
    public HashMap<String, String> getDictionary() {
        return dictionary;
    }

}
