/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import junit.framework.TestCase;

/**
 *
 * @author ucchy
 */
public class KeywordReplacerTest extends TestCase {

    private static final int MAX = 10;

    public void test() {

        String testee = "";
        for ( int i=1; i<=MAX; i++ ) {
            testee += String.format("%%test%d%%%d", i, i);
        }


        String test1 = new String(testee);
        long start = System.currentTimeMillis();
        for ( int i=1; i<=MAX; i++ ) {
            test1 = test1.replace("%test" + i + "%", i + "");
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("test1 time = " + time);


        KeywordReplacer test2 = new KeywordReplacer(testee);
        start = System.currentTimeMillis();
        for ( int i=1; i<=MAX; i++ ) {
            test2.replace("%test" + i + "%", i + "");
        }
        time = System.currentTimeMillis() - start;
        System.out.println("test2 time = " + time);

        assertTrue(test1.equals(test2.toString()));
    }

    public void testaaa() {

        String testee = "%prefix%username&f: %msg";
        String keyword = "%msg";
        String val1 = "&f%japanize&7 #%msg";
        //String val2 = "発言の内容";
        String val2 = "%msg発%msg言%msgの%msg内%msg容%msg";


        KeywordReplacer test1 = new KeywordReplacer(testee);
        long start = System.currentTimeMillis();
        test1.replace(keyword, val1);
        test1.replace(keyword, val2);
        long time = System.currentTimeMillis() - start;
        System.out.println("test1 time = " + time + ", str = " + test1.toString());


        String test2 = new String(testee);
        start = System.currentTimeMillis();
        test2 = test2.replace(keyword, val1);
        test2 = test2.replace(keyword, val2);
        time = System.currentTimeMillis() - start;
        System.out.println("test2 time = " + time + ", str = " + test2);


        assertTrue(test1.toString().equals(test2));
    }
}
