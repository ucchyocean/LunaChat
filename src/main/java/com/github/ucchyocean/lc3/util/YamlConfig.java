/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

/**
 * Yaml設定読み書きユーティリティクラス
 * @author ucchy
 */
public class YamlConfig extends YamlSection {

    /**
     * InputStreamからYamlConfigをロードする
     * @param stream InputStream
     * @return ロードされたYamlConfig
     * @throws IOException InputStreamの入力がyamlとして解析できない場合など
     */
    public static YamlConfig load(InputStream stream) throws IOException {

        YamlConfig config = new YamlConfig();
        if ( stream == null ) return config;

        Yaml yaml = new Yaml();

        @SuppressWarnings("unchecked")
        Map<String, Object> map = yaml.loadAs(stream, Map.class);
        if ( map == null ) {
            throw new IOException("Cannot load stream as yaml.");
        }

        config.map = map;
        return config;
    }

    /**
     * ReaderからYamlConfigをロードする
     * @param reader Reader
     * @return ロードされたYamlConfig
     * @throws IOException Readerの入力がyamlとして解析できない場合など
     */
    public static YamlConfig load(Reader reader) throws IOException {

        YamlConfig config = new YamlConfig();
        if ( reader == null ) return config;

        Yaml yaml = new Yaml();

        @SuppressWarnings("unchecked")
        Map<String, Object> map = yaml.loadAs(reader, Map.class);
        if ( map == null ) {
            throw new IOException("Cannot load reader as yaml.");
        }

        config.map = map;
        return config;
    }

    /**
     * FileからYamlConfigをロードする
     * @param file File
     * @return ロードされたYamlConfig（ロードに失敗した場合は空のYamlConfigが返される）
     */
    public static @NotNull YamlConfig load(File file) {

        // 対象ファイルが存在しない場合やからっぽの場合は、からっぽのYamlConfigを返す
        if ( !file.exists() || !file.isFile() || file.length() == 0 ) return new YamlConfig();

        // 読み込む
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8") ) {
            return load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 読み込みに失敗した場合は、からっぽのYamlConfigを返す
        return new YamlConfig();
    }

    /**
     * 保存する
     * @param file 保存先
     * @throws IOException 保存に失敗した場合など
     */
    public void save(File file) throws IOException {

        Yaml yaml = new Yaml();
        String data = yaml.dumpAsMap(map);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        }
    }
}
