/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.util.regex.Pattern;

/**
 * 文字列のキーワード置き換えを行うユーティリティクラス
 * @author ucchy
 */
public class KeywordReplacer {

    private StringBuilder str;

    /**
     * 指定された文字列で初期化を行う
     * @param src
     */
    public KeywordReplacer(String src) {
        str = new StringBuilder(src);
    }

    /**
     * 文字列keywordを、文字列valueに置き換える
     * @param keyword キーワード
     * @param value 値
     */
    public void replace(String keyword, String value) {
        int start;
        while ( (start = str.indexOf(keyword)) > -1 ) {
            str.replace(start, start + keyword.length(), value);
        }
    }

    /**
     * 正規表現regexに一致する箇所を、文字列valueに置き換える
     * @param regex 正規表現
     * @param value 値
     */
    public void replaceRegex(String regex, String value) {
        str = new StringBuilder(Pattern.compile(regex).matcher(str).replaceAll(value));
    }

    /**
     * 文字列内のカラーコード候補（&a）を、カラーコード（§a）に置き換えする
     */
    public void translateColorCode() {
        str = new StringBuilder(Utility.replaceColorCode(str.toString()));
    }

    /**
     * 指定された文字列が含まれているかどうかを判定する
     * @param keyword キーワード
     * @return 含まれているかどうか
     */
    public boolean contains(String keyword) {
        return str.indexOf(keyword) > -1;
    }

    /**
     * 文字列をStringで取得する
     * @return String
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return str.toString();
    }

    /**
     * 文字列をStringBuilderのまま取得する
     * @return StringBuilder
     */
    public StringBuilder getStringBuilder() {
        return str;
    }

    public String substring(int start) {
        return str.substring(start);
    }

    public String substring(int start, int end) {
        return str.substring(start, end);
    }

    public int length() {
        return str.length();
    }
}
