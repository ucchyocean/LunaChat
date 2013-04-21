/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

/**
 * @author ucchy
 * ローマ字からかな文字へ変換するクラス
 */
public class KanaConverter {

    private static final String[][] TABLE = {
        {   "", "あ","い","う","え","お" },
        {  "k", "か","き","く","け","こ" },
        {  "s", "さ","し","す","せ","そ" },
        {  "t", "た","ち","つ","て","と" },
        {  "n", "な","に","ぬ","ね","の" },
        {  "h", "は","ひ","ふ","へ","ほ" },
        {  "m", "ま","み","む","め","も" },
        {  "y", "や","い","ゆ","いぇ","よ" },
        {  "r", "ら","り","る","れ","ろ" },
        {  "w", "わ","うぃ","う","うぇ","を" },

        {  "g", "が","ぎ","ぐ","げ","ご" },
        {  "z", "ざ","じ","ず","ぜ","ぞ" },
        {  "j", "じゃ","じ","じゅ","じぇ","じょ" },
        {  "d", "だ","ぢ","づ","で","ど" },
        {  "b", "ば","び","ぶ","べ","ぼ" },
        {  "p", "ぱ","ぴ","ぷ","ぺ","ぽ" },
        { "gy", "ぎゃ","ぎぃ","ぎゅ","ぎぇ","ぎょ" },
        { "zy", "じゃ","じぃ","じゅ","じぇ","じょ" },
        { "jy", "じゃ","じぃ","じゅ","じぇ","じょ" },
        { "dy", "ぢゃ","ぢぃ","ぢゅ","ぢぇ","ぢょ" },
        { "by", "びゃ","びぃ","びゅ","びぇ","びょ" },
        { "py", "ぴゃ","ぴぃ","ぴゅ","ぴぇ","ぴょ" },

        {  "l", "ぁ","ぃ","ぅ","ぇ","ぉ" },
        {  "v", "ヴぁ","ヴぃ","ヴ","ヴぇ","ヴぉ" },
        { "sh", "しゃ","し","しゅ","しぇ","しょ" },
        { "sy", "しゃ","し","しゅ","しぇ","しょ" },
        { "ch", "ちゃ","ち","ちゅ","ちぇ","ちょ" },
        { "cy", "ちゃ","ち","ちゅ","ちぇ","ちょ" },

        {  "f", "ふぁ","ふぃ","ふ","ふぇ","ふぉ" },
        {  "q", "くぁ","くぃ","く","くぇ","くぉ" },
        { "ky", "きゃ","きぃ","きゅ","きぇ","きょ" },
        { "ty", "ちゃ","ちぃ","ちゅ","ちぇ","ちょ" },
        { "ny", "にゃ","にぃ","にゅ","にぇ","にょ" },
        { "hy", "ひゃ","ひぃ","ひゅ","ひぇ","ひょ" },
        { "my", "みゃ","みぃ","みゅ","みぇ","みょ" },
        { "ry", "りゃ","りぃ","りゅ","りぇ","りょ" },
        { "ly", "ゃ","ぃ","ゅ","ぇ","ょ" },
        { "lt", "た","ち","っ","て","と" },
        { "xy", "ゃ","ぃ","ゅ","ぇ","ょ" },
        { "xt", "た","ち","っ","て","と" },
        {  "x", "ぁ","ぃ","ぅ","ぇ","ぉ" },
    };

    private static String R2K(String s, int n) {

        for ( int i=0; i<TABLE.length; i++ ) {
            if ( s.equals(TABLE[i][0]) ) {
                return TABLE[i][n+1];
            }
        }
        return s + TABLE[0][n+1];
    }

    /**
     * ローマ字をかな文字へ変換する
     * @param org 変換元文字列
     * @return 変換後の文字列
     */
    protected static String conv(String org) {

        String last = "";
        StringBuilder line = new StringBuilder();

        for ( int i=0; i<org.length(); i++ ) {
            String tmp = org.substring(i,i+1);

            if ( tmp.equals("a") ) {
                line.append( R2K(last, 0) );
                last = "";
            } else if ( tmp.equals("i") ) {
                line.append( R2K(last, 1) );
                last = "";
            } else if ( tmp.equals("u") ) {
                line.append( R2K(last, 2) );
                last = "";
            } else if ( tmp.equals("e") ) {
                line.append( R2K(last, 3) );
                last = "";
            } else if ( tmp.equals("o") ) {
                line.append( R2K(last, 4) );
                last = "";
            } else {
                if ( last.equals("n") && !(tmp.equals("y")) ) {
                    line.append("ん");
                    last = "";
                    if ( tmp.equals("n") ) {
                        continue;
                    }
                }
                if ( java.lang.Character.isLetter(tmp.charAt(0)) ) {
                    if ( last.equals(tmp) ) {
                        line.append("っ");
                        last = tmp;
                    } else {
                        last = last + tmp;
                    }
                } else {
                    line.append(last + tmp);
                    last = "";
                }
            }
        }
        line.append(last);

        return line.toString();
    }
}
