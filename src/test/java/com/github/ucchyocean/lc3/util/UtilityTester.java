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
public class UtilityTester extends TestCase {

    public void testColorCode() {

        String testee = "#FFEEDDtesuto#112233aiueo";
        System.out.println(Utility.replaceColorCode(testee));

        assertTrue("§x§F§F§E§E§D§Dtesuto§x§1§1§2§2§3§3aiueo".equals(Utility.replaceColorCode(testee)));

        testee = "#FEDtesuto#123aiueo";
        System.out.println(Utility.replaceColorCode(testee));

        assertTrue("§x§F§F§E§E§D§Dtesuto§x§1§1§2§2§3§3aiueo".equals(Utility.replaceColorCode(testee)));
    }
}
