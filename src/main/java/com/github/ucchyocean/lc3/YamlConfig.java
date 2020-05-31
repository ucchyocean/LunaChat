/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

/**
 * Yaml設定読み書きユーティリティクラス
 * このクラスは、階層構造のデータ形式に対応していないので注意。
 * @author ucchy
 */
public class YamlConfig {

    private Map<String, Object> map = new HashMap<>();
    private Map<String, Object> defaults = new HashMap<>();

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

    public static YamlConfig load(File file) {
        try ( InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8") ) {
            return load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(File file) throws IOException {

        Yaml yaml = new Yaml();
        String data = yaml.dumpAsMap(map);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        }
    }

    public String getRawData() {
        Yaml yaml = new Yaml();
        return yaml.dumpAsMap(map);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object val = map.getOrDefault(key, defaults.get(key));
        return ( val != null && val instanceof Boolean ) ? (boolean)val : defaultValue;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        Object val = map.getOrDefault(key, defaults.get(key));
        return ( val != null && val instanceof Integer ) ? (int)val : defaultValue;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        Object val = map.getOrDefault(key, defaults.get(key));
        return ( val != null ) ? val.toString() : null;
    }

    public List<String> getStringList(String key) {
        return getStringList(key, null);
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key, ArrayList<String> defaultValue) {
        Object val = map.getOrDefault(key, defaults.get(key));
        return ( val != null && val instanceof ArrayList<?> ) ? (List<String>)val : null;
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public Set<String> getKeys(boolean deeps) {
        if ( !deeps ) {
            return map.keySet();
        } else {
            // TODO 未実装
            return map.keySet();
        }
    }

    public void addDefaults(YamlConfig defaults) {
        this.defaults = defaults.map;
    }
}
