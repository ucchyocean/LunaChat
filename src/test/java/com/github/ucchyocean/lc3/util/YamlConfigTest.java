/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * YamlConfigのテスト
 * @author ucchy
 */
public class YamlConfigTest extends TestCase {

    public void testYamlConfig() {

        YamlConfig config = new YamlConfig();

        config.set("aaa.bbb", "test");
        config.set("aaa.int", 1);

        YamlSection section = config.createSection("bbb");

        section.set("aiueo.aaa", "testdesu");
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        section.set("list", list);

        System.out.println("=== Yaml Values ===");
        System.out.println(config.getRawData());
        System.out.println();
        System.out.println("=== Yaml Keys (deeps=true) ===");
        for ( String key : config.getKeys(true) ) {
            System.out.println(key);
        }

        assertTrue("test".equals(config.getString("aaa.bbb")));
        assertTrue("testdesu".equals(config.getString("bbb.aiueo.aaa")));
        assertTrue(config.getInt("aaa.int") == 1);
        assertTrue(config.getStringList("bbb.list").size() == 2);
    }
}
