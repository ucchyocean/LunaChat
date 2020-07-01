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
}
