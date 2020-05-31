/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * プラグインのメッセージリソース管理クラス
 * @author ucchy
 */
public class Messages {

    private static YamlConfig resources;
    private static File _messageFolder;
    private static File _jar;

    /**
     * Jarファイル内から直接 messages_en.yml をdefaultMessagesとしてロードし、
     * langに対応するメッセージをファイルからロードする。
     * @param messagesFolder メッセージ格納フォルダ
     * @param jar jarファイル
     * @param lang デフォルト言語
     */
    public static void initialize(File messagesFolder, File jar, String lang) {

        _jar = jar;
        _messageFolder = messagesFolder;
        if ( !_messageFolder.exists() ) {
            _messageFolder.mkdirs();
        }

        // コンフィグフォルダにメッセージファイルがまだ無いなら、コピーしておく
        for ( String filename : new String[]{
                "messages_en.yml", "messages_ja.yml"} ) {
            File file = new File(_messageFolder, filename);
            if ( !file.exists() ) {
                Utility.copyFileFromJar(_jar, file, filename, true);
            }
        }

        // デフォルトメッセージを、jarファイル内からロードする
        YamlConfig defaultMessages = null;
        try ( JarFile jarFile = new JarFile(_jar) ) {

            ZipEntry zipEntry = jarFile.getEntry(String.format("messages_%s.yml", lang));
            if ( zipEntry == null ) {
                zipEntry = jarFile.getEntry("messages_en.yml");
            }

            defaultMessages = YamlConfig.load(jarFile.getInputStream(zipEntry));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 対応する言語のメッセージをロードする
        File file = new File(_messageFolder, String.format("messages_%s.yml", lang));
        if ( !file.exists() ) {
            file = new File(_messageFolder, "messages_en.yml");
        }

        resources = YamlConfig.load(file);
        resources.addDefaults(defaultMessages);
    }

    /**
     * リソースを取得する
     * @param key リソースキー
     * @return リソース
     */
    public static String get(String key) {
        return Utility.replaceColorCode(resources.getString(key));
    }

    /**
     * 指定された言語でリロードを行う。
     * @param lang 言語
     */
    public static void reload(String lang) {
        initialize(_jar, _messageFolder, lang);
    }
}
