/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;

/**
 * プラグインのリソース管理クラス
 * @author ucchy
 */
public class Resources {

    private static final String FILE_NAME = "messages.yml";

    private static YamlConfig resources = new YamlConfig();

    /**
     * 初期化する
     */
    protected static void initialize() {

        File file = new File(
                LunaChatBukkit.getInstance().getDataFolder() +
                File.separator + FILE_NAME);

        if ( !file.exists() ) {
            Utility.copyFileFromJar(LunaChat.getPlugin().getPluginJarFile(),
                    file, FILE_NAME, false);
        }

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
     * Jarファイル内から直接 messages.yml を読み込み、YamlConfigurationにして返すメソッド
     * @return
     */
    private static YamlConfig loadDefaultMessages() {

        YamlConfig messages = new YamlConfig();

        try ( JarFile jarFile = new JarFile(LunaChat.getPlugin().getPluginJarFile()) ) {
            ZipEntry zipEntry = jarFile.getEntry(FILE_NAME);
            InputStream inputStream = jarFile.getInputStream(zipEntry);
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")) ) {
                String line;
                while ( (line = reader.readLine()) != null ) {
                    if ( line.contains(":") && !line.startsWith("#") ) {
                        String key = line.substring(0, line.indexOf(":")).trim();
                        String value = line.substring(line.indexOf(":") + 1).trim();
                        if ( value.startsWith("'") && value.endsWith("'") )
                            value = value.substring(1, value.length()-1);
                        messages.set(key, value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }
}
