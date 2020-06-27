/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

/**
 * Yaml設定読み書きユーティリティクラス
 * @author ucchy
 */
public class YamlSection {

    Map<String, Object> map;
    private Map<String, Object> defaults = new HashMap<>();

    /**
     * コンストラクタ
     */
    public YamlSection() {
        map = new HashMap<>();
    }

    /**
     * コンストラクタ
     * @param map 初期データ
     */
    public YamlSection(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * 保持しているデータをStringに変換して返す
     * @return Stringに変換されたデータ
     */
    public String getRawData() {
        Yaml yaml = new Yaml();
        return yaml.dumpAsMap(map);
    }

    /**
     * 指定されたkeyにvalueを設定する
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        if ( key.contains(".") ) {
            String parent = key.substring(0, key.indexOf("."));
            YamlSection section = getSection(parent);
            if ( section == null ) {
                section = createSection(parent);
            }
            String remain = key.substring(key.indexOf(".") + 1);
            section.set(remain, value);
        } else {
            map.put(key, value);
        }
    }

    /**
     * 指定されたkeyの値を取得する
     * @param key
     * @return keyに対応する値（値が無い場合はnullが返される）
     */
    public @Nullable Object get(String key) {
        return get(key, null);
    }

    /**
     * 指定されたkeyの値を取得する
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無い場合はdefaultValueが返される）
     */
    public Object get(String key, Object defaultValue) {
        if ( key.contains(".") ) {
            String parent = key.substring(0, key.indexOf("."));
            YamlSection section = getSection(parent);
            if ( section == null ) return defaultValue;
            String remain = key.substring(key.indexOf(".") + 1);
            return section.get(remain, defaultValue);
        } else {
            Object val = map.getOrDefault(key, defaults.get(key));
            return ( val != null ) ? val : defaultValue;
        }
    }

    /**
     * 指定されたkeyの値をbooleanに変換して返す
     * @param key
     * @return keyに対応する値（値が無いかbooleanに変換できない場合はfalseが返される）
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * 指定されたkeyの値をbooleanに変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無いかbooleanに変換できない場合はdefaultValueが返される）
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object val = get(key, defaults.get(key));
        return ( val != null && val instanceof Boolean ) ? (boolean)val : defaultValue;
    }

    /**
     * 指定されたkeyの値をintに変換して返す
     * @param key
     * @return keyに対応する値（値が無いかintに変換できない場合は0が返される）
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 指定されたkeyの値をintに変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無いかintに変換できない場合はdefaultValueが返される）
     */
    public int getInt(String key, int defaultValue) {
        Object val = get(key, defaults.get(key));
        return ( val != null && val instanceof Integer ) ? (int)val : defaultValue;
    }

    /**
     * 指定されたkeyの値をStringに変換して返す
     * @param key
     * @return keyに対応する値（値が無いかStringに変換できない場合は0が返される）
     */
    public @Nullable String getString(String key) {
        return getString(key, null);
    }

    /**
     * 指定されたkeyの値をStringに変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無いかStringに変換できない場合はdefaultValueが返される）
     */
    public String getString(String key, String defaultValue) {
        Object val = get(key, defaults.get(key));
        return ( val != null ) ? val.toString() : defaultValue;
    }

    /**
     * 指定されたkeyの値をList&lt;String&gt;に変換して返す
     * @param key
     * @return keyに対応する値（値が無いかList&lt;String&gt;に変換できない場合はnullが返される）
     */
    public @Nullable List<String> getStringList(String key) {
        return getStringList(key, null);
    }

    /**
     * 指定されたkeyの値をList&lt;String&gt;に変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無いかList&lt;String&gt;に変換できない場合はdefaultValueが返される）
     */
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key, ArrayList<String> defaultValue) {
        Object val = get(key, defaults.get(key));
        return ( val != null && val instanceof ArrayList<?> ) ? (List<String>)val : defaultValue;
    }

    /**
     * 指定されたkeyの値をMap&lt;String, Object&gt;に変換して返す
     * @param key
     * @return keyに対応する値（値が無いかMap&lt;String, Object&gt;に変換できない場合はnullが返される）
     */
    public @Nullable Map<String, Object> getMap(String key) {
        return getMap(key, null);
    }

    /**
     * 指定されたkeyの値をMap&lt;String, Object&gt;に変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return keyに対応する値（値が無いかMap&lt;String, Object&gt;に変換できない場合はdefaultValueが返される）
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String key, Map<String, Object> defaultValue) {
        Object val = get(key, defaults.get(key));
        return ( val != null && val instanceof Map<?, ?> ) ? (Map<String, Object>)val : defaultValue;
    }

    /**
     * 指定されたkeyの値をYamlSectionに変換して返す
     * @param key
     * @return YamlSection（値が無いかMap&lt;String, Object&gt;に変換できない場合はnullが返される）
     */
    public @Nullable YamlSection getSection(String key) {
        return getSection(key, null);
    }

    /**
     * 指定されたkeyの値をYamlSectionに変換して返す
     * @param key
     * @param defaultValue デフォルト値
     * @return YamlSection（値が無いかMap&lt;String, Object&gt;に変換できない場合はdefaultValueが返される）
     */
    public YamlSection getSection(String key, YamlSection defaultValue) {
        Map<String, Object> m = getMap(key);
        if ( m == null ) return defaultValue;
        return new YamlSection(m);
    }

    /**
     * 指定されたkeyの値があるかどうか判定する
     * @param key
     * @return 値があるかどうか
     */
    public boolean contains(String key) {
        if ( key.contains(".") ) {
            String parent = key.substring(0, key.indexOf("."));
            YamlSection section = getSection(parent);
            if ( section == null ) return false;
            String remain = key.substring(key.indexOf(".") + 1);
            return section.contains(remain);
        } else {
            return map.containsKey(key);
        }
    }

    /**
     * 指定されたkeyに対応するサブセクションがあるかどうか判定する
     * @param key
     * @return サブセクションがあるかどうか
     */
    public boolean containsSection(String key) {
        return (getMap(key) != null);
    }

    /**
     * 指定されたkeyのサブセクションを作成する<br/>
     * 注意：階層構造指定でセクションを作成した場合、最下層のセクションが戻り値として返されることに注意すること。<br/>
     * 例えば、createSection("aa.bb.cc") の戻り値は "cc" のセクションとなる。
     * @param key
     * @return サブセクション
     */
    public YamlSection createSection(String key) {
        if ( containsSection(key) ) return getSection(key);
        Map<String, Object> map = new HashMap<String, Object>();
        set(key, map);
        return new YamlSection(map);
    }

    /**
     * 階層のkeyを羅列する
     * @param deeps 再帰的にkeyを取得するかどうか
     * @return keyの羅列
     */
    public Set<String> getKeys(boolean deeps) {
        if ( !deeps ) {
            return map.keySet();
        } else {
            Set<String> result = new TreeSet<String>();
            for ( String key : map.keySet() ) {
                result.add(key);
                if ( containsSection(key) ) {
                    YamlSection sec = getSection(key);
                    for ( String sub : sec.getKeys(true) ) {
                        result.add(key + "." + sub);
                    }
                }
            }
            return result;
        }
    }

    /**
     * デフォルト値を設定する
     * @param defaults デフォルト値
     */
    public void addDefaults(YamlSection defaults) {
        if (defaults == null) {
            this.defaults = new HashMap<String, Object>();
        } else {
            this.defaults = defaults.map;
        }
    }
}
