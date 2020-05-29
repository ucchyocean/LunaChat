/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * プラグインのメッセージリソース管理クラス
 * @author ucchy
 */
public class Messages {

    private static final String BASE_NAME = "messages";
    private static final String FILE_NAME = BASE_NAME + ".yml";

    private static YamlConfig resources;

    /**
     * 初期化する
     */
    protected static void initialize() {

        File file = new File(
                LunaChat.getDataFolder() +
                File.separator + FILE_NAME);

        if ( !file.exists() ) {
            Utility.copyFileFromJar(LunaChat.getPluginJarFile(),
                    file, FILE_NAME, false);
        }

        resources = YamlConfig.load(file);
        resources.addDefaults(loadDefaultMessages());
    }

    /**
     * リソースを取得する
     * @param key リソースキー
     * @return リソース
     */
    public static String get(String key) {

        if ( resources == null ) {
            initialize();
        }
        return Utility.replaceColorCode(resources.getString(key));
    }

    /**
     * Jarファイル内から直接 messages.yml を読み込み、YamlConfigにして返すメソッド
     * @return
     */
    private static YamlConfig loadDefaultMessages() {
        try {
            return YamlConfig.load(getResourceInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Jarファイル内を検索して、現在のlanguageやcountryになるべく一致するリソースを見つけて、InputStreamで返す
     * @return　InputStream
     */
    private static InputStream getResourceInputStream() {

        String baseName = BASE_NAME;
        Locale locale = Locale.getDefault();

        ArrayList<String> candidates = new ArrayList<String>();
        candidates.add(String.format("/%s_%s_%s.yaml", baseName, locale.getLanguage(), locale.getCountry()));
        candidates.add(String.format("/%s_%s_%s.yml", baseName, locale.getLanguage(), locale.getCountry()));
        candidates.add(String.format("/%s_%s.yaml", baseName, locale.getLanguage()));
        candidates.add(String.format("/%s_%s.yml", baseName, locale.getLanguage()));
        candidates.add("/" + baseName + ".yaml");
        candidates.add("/" + baseName + ".yml");

        for ( String name : candidates ) {
            InputStream stream = Messages.class.getResourceAsStream(name);
            if ( stream != null ) return stream;
        }
        return null;
    }
}
