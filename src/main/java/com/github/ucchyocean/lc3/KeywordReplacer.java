/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

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
     * 文字列keywordえを、文字列valueに置き替える
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
}
