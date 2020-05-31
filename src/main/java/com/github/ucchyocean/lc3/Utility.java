/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * ユーティリティクラス
 * @author ucchy
 */
public class Utility {

    /**
     * jarファイルの中に格納されているファイルを、jarファイルの外にコピーするメソッド
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     * @param isBinary バイナリファイルかどうか
     */
    public static void copyFileFromJar(
            File jarFile, File targetFile, String sourceFilePath, boolean isBinary) {

        // でばっぐ
        System.out.println(jarFile + " - " + targetFile + " - " + sourceFilePath);

        File parent = targetFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try ( JarFile jar = new JarFile(jarFile) ) {
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            InputStream is = jar.getInputStream(zipEntry);

            try ( FileOutputStream fos = new FileOutputStream(targetFile) ) {

                if (isBinary) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }

                } else {

                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")) ) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文字列内のカラーコード候補（&a）を、カラーコード（§a）に置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String replaceColorCode(String source) {
        if (source == null) return null;
        return source.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    /**
     * 文字列に含まれているカラーコード（§a）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripColorCode(String source) {
        if (source == null) return null;
        return stripAltColorCode(source).replaceAll("\u00A7([0-9a-fk-or])", "");
    }

    /**
     * 文字列に含まれているカラーコード候補（&a）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripAltColorCode(String source) {
        if (source == null) return null;
        return source.replaceAll("&([0-9a-fk-or])", "");
    }

    /**
     * カラーコード（§a）かどうかを判断する
     * @param code カラーコード
     * @return カラーコードかどうか
     */
    public static boolean isColorCode(String code) {
        if (code == null) return false;
        return code.matches("\u00A7[0-9a-fk-orA-FK-OR]");
    }

    /**
     * カラーコード候補（&a）かどうかを判断する
     * @param color カラーコード候補
     * @return カラーコード候補かどうか
     */
    public static boolean isAltColorCode(String code) {
        if (code == null) return false;
        return code.matches("&[0-9a-fk-orA-FK-OR]");
    }

    /**
     * ChatColorで指定可能な色（REDとかGREENとか）かどうかを判断する
     * @param color カラー表記の文字列
     * @return 指定可能かどうか
     */
    public static boolean isValidColor(String color) {
        if ( color == null ) return false;
        for (ChatColor c : ChatColor.values()) {
            if (c.name().equals(color.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * カラー表記の文字列を、カラーコードに変換する
     * @param color カラー表記の文字列
     * @return カラーコード
     */
    public static String changeToColorCode(String color) {

        return "&" + changeToChatColor(color).getChar();
    }

    /**
     * カラー表記の文字列を、ChatColorクラスに変換する
     * @param color カラー表記の文字列
     * @return ChatColorクラス
     */
    public static ChatColor changeToChatColor(String color) {

        if (isValidColor(color)) {
            return ChatColor.valueOf(color.toUpperCase());
        }
        return ChatColor.WHITE;
    }

    /**
     * 指定された文字数のアスタリスクの文字列を返す
     * @param length アスタリスクの個数
     * @return 指定された文字数のアスタリスク
     */
    public static String getAstariskString(int length) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append("*");
        }
        return buf.toString();
    }

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    public static boolean existsOfflinePlayer(String name) {
        // TODO 未実装
        return false;
    }

    /**
     * 動作環境のロケールを取得する。
     * @return 動作環境のロケール
     */
    public static Locale getDefaultLocale() {
        Locale locale = Locale.getDefault();
        if ( locale == null ) return Locale.ENGLISH;
        return locale;
    }
}
