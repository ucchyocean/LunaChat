/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Japanize変換に使用するユーザー辞書
 * @author ucchy
 */
public class JapanizeDictionary {

    private LunaChatBungee parent;
    private HashMap<String, String> dictionary;

    /**
     * コンストラクタ
     * @param parent
     */
    public JapanizeDictionary(LunaChatBungee parent) {
        this.parent = parent;
        reload();
    }

    /**
     * ファイルから設定を読み直す
     */
    public void reload() {

        dictionary = new HashMap<String, String>();

        // Fileを取得
        File folder = new File(
                parent.getProxy().getPluginsFolder(),
                "BungeeJapanizeMessenger");
        if ( !folder.exists() ) {
            return;
        }

        File file = new File(folder, "dictionary.yml");
        if ( !file.exists() ) {
            return;
        }

        ConfigurationProvider provider =
                ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            Configuration config = provider.load(file);

            for ( String key : config.getKeys() ) {
                dictionary.put(key, config.getString(key));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ファイルに保存する
     */
    public void save() {

        Configuration config = new Configuration();

        for ( String key : dictionary.keySet() ) {
            config.set(key, dictionary.get(key));
        }

        File folder = new File(
                parent.getProxy().getPluginsFolder(),
                "BungeeJapanizeMessenger");
        ConfigurationProvider provider =
                ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            provider.save(config, new File(folder, "dictionary.yml"));
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
