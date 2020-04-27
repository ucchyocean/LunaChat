/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * プラグインのリソース管理クラス
 * @author ucchy
 */
public class Resources {

    private static final String FILE_NAME = "messages.yml";

    private static YamlConfiguration defaultMessages;
    private static YamlConfiguration resources;

    /**
     * 初期化する
     */
    protected static void initialize() {

        File file = new File(
                LunaChat.getInstance().getDataFolder() +
                File.separator + FILE_NAME);

        if ( !file.exists() ) {
            Utility.copyFileFromJar(LunaChat.getPluginJarFile(),
                    file, FILE_NAME, false);
        }

        defaultMessages = loadDefaultMessages();
        resources = YamlConfiguration.loadConfiguration(file);
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
        String def = defaultMessages.getString(key);
        return Utility.replaceColorCode(resources.getString(key, def));
    }

    /**
     * Jarファイル内から直接 messages.yml を読み込み、YamlConfigurationにして返すメソッド
     * @return
     */
    private static YamlConfiguration loadDefaultMessages() {

        YamlConfiguration messages = new YamlConfiguration();
        JarFile jarFile = null;
        BufferedReader reader = null;
        try {
            jarFile = new JarFile(LunaChat.getPluginJarFile());
            ZipEntry zipEntry = jarFile.getEntry(FILE_NAME);
            InputStream inputStream = jarFile.getInputStream(zipEntry);
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( jarFile != null ) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }

        return messages;
    }
}
