/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * ユーティリティクラス
 * @author ucchy
 */
public class Utility {

    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN =
            Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

    /**
     * jarファイルの中に格納されているファイルを、jarファイルの外にコピーするメソッド
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     * @param isBinary バイナリファイルかどうか
     */
    public static void copyFileFromJar(
            File jarFile, File targetFile, String sourceFilePath, boolean isBinary) {

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
        char[] b = source.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    /**
     * 文字列に含まれているカラーコード（§a）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripColor(String source) {
        if (source == null) return null;
        return STRIP_COLOR_PATTERN.matcher(source).replaceAll("");
    }

    /**
     * カラーコード（§a）かどうかを判断する
     * @param code カラーコード
     * @return カラーコードかどうか
     */
    public static boolean isColorCode(String code) {
        if (code == null) return false;
        return code.matches(COLOR_CHAR + "[0-9a-fA-F]");
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
}
