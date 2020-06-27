/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * チャットカラー
 * @author ucchy
 */
public final class ChatColor {

    public static final ChatColor BLACK = new ChatColor("BLACK", '0', 0x00);
    public static final ChatColor DARK_BLUE = new ChatColor("DARK_BLUE", '1', 0x1);
    public static final ChatColor DARK_GREEN = new ChatColor("DARK_GREEN", '2', 0x2);
    public static final ChatColor DARK_AQUA = new ChatColor("DARK_AQUA", '3', 0x3);
    public static final ChatColor DARK_RED = new ChatColor("DARK_RED", '4', 0x4);
    public static final ChatColor DARK_PURPLE = new ChatColor("DARK_PURPLE", '5', 0x5);
    public static final ChatColor GOLD = new ChatColor("GOLD", '6', 0x6);
    public static final ChatColor GRAY = new ChatColor("GRAY", '7', 0x7);
    public static final ChatColor DARK_GRAY = new ChatColor("DARK_GRAY", '8', 0x8);
    public static final ChatColor BLUE = new ChatColor("BLUE", '9', 0x9);
    public static final ChatColor GREEN = new ChatColor("GREEN", 'a', 0xA);
    public static final ChatColor AQUA = new ChatColor("AQUA", 'b', 0xB);
    public static final ChatColor RED = new ChatColor("RED", 'c', 0xC);
    public static final ChatColor LIGHT_PURPLE = new ChatColor("LIGHT_PURPLE", 'd', 0xD);
    public static final ChatColor YELLOW = new ChatColor("YELLOW", 'e', 0xE);
    public static final ChatColor WHITE = new ChatColor("WHITE", 'f', 0xF);
    public static final ChatColor MAGIC = new ChatColor("MAGIC", 'k', 0x10, true);
    public static final ChatColor BOLD = new ChatColor("BOLD", 'l', 0x11, true);
    public static final ChatColor STRIKETHROUGH = new ChatColor("STRIKETHROUGH", 'm', 0x12, true);
    public static final ChatColor UNDERLINE = new ChatColor("UNDERLINE", 'n', 0x13, true);
    public static final ChatColor ITALIC = new ChatColor("ITALIC", 'o', 0x14, true);
    public static final ChatColor RESET = new ChatColor("RESET", 'r', 0x15);

    public static final char COLOR_CHAR = '\u00A7';

    @SuppressWarnings("unused")
    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;
    private final String name;

    private ChatColor(String name, char code, int intCode) {
        this(name, code, intCode, false);
    }

    private ChatColor(String name, char code, int intCode, boolean isFormat) {
        this.name = name;
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        this.toString = new String(new char[] {COLOR_CHAR, code});
    }

    private ChatColor(String name, String toString) {
        this.name = name;
        this.toString = toString;
        this.isFormat = false;
        this.code = 'c';
        this.intCode =0xFF;
    }

    public String name() {
        return name;
    }

    public char getChar() {
        return code;
    }

    @NotNull
    @Override
    public String toString() {
        return toString;
    }

    public boolean isFormat() {
        return isFormat;
    }

    public static @Nullable ChatColor getChatColorFromWebColor(String code) {
        if ( code == null ) return null;
        if ( code.matches("#[0-9a-fA-F]{3}") ) {
            return new ChatColor(code, code.replaceAll(
                    "#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                    "\u00A7x\u00A7$1\u00A7$1\u00A7$2\u00A7$2\u00A7$3\u00A7$3"));
        } else if ( code.matches("#[0-9a-fA-F]{6}") ) {
            return new ChatColor(code, code.replaceAll(
                    "#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                    "\u00A7x\u00A7$1\u00A7$2\u00A7$3\u00A7$4\u00A7$5\u00A7$6"));
        }
        return null;
    }

    public static ChatColor[] values() {
        return new ChatColor[] {
                BLACK,
                DARK_BLUE,
                DARK_GREEN,
                DARK_AQUA,
                DARK_RED,
                DARK_PURPLE,
                GOLD,
                GRAY,
                DARK_GRAY,
                BLUE,
                GREEN,
                AQUA,
                RED,
                LIGHT_PURPLE,
                YELLOW,
                WHITE,
                MAGIC,
                BOLD,
                STRIKETHROUGH,
                UNDERLINE,
                ITALIC,
                RESET,
        };
    }

    public static ChatColor valueOf(String code) {
        if ( code.matches("#[0-9a-fA-F]{6}") ) {
            return new ChatColor(code, code.replaceAll(
                    "#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                    "\u00A7x\u00A7$1\u00A7$2\u00A7$3\u00A7$4\u00A7$5\u00A7$6"));
        } else {
            for ( ChatColor v : values() ) {
                if ( v.name.equals(code) ) return v;
            }
        }
        return null;
    }
}
