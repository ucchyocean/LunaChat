/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if ( !parent.exists() ) {
            parent.mkdirs();
        }

        try {
            JarFile jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            if ( isBinary ) {
                byte[] buf = new byte[8192];
                int len;
                while ( (len = is.read(buf)) != -1 ) {
                    fos.write(buf, 0, len);
                }

            } else {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                writer = new BufferedWriter(new OutputStreamWriter(fos));

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( writer != null ) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( fos != null ) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }
    }

    /**
     * 文字列内のカラーコードを置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String replaceColorCode(String source) {
        return source.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
    }

    /**
     * 指定された文字数のアスタリスクの文字列を返す
     * @param length アスタリスクの個数
     * @return 指定された文字数のアスタリスク
     */
    public static String getAstariskString(int length) {
        StringBuilder buf = new StringBuilder();
        for ( int i=0; i<length; i++ ) {
            buf.append("*");
        }
        return buf.toString();
    }

    /**
     * カラー表記の文字列を、ChatColorクラスに変換する
     * @param color カラー表記の文字列
     * @return ChatColorクラス
     */
    public static ChatColor changeToChatColor(String color) {

        if ( isValidColor(color) ) {
            return ChatColor.valueOf(color.toUpperCase());
        }
        return ChatColor.WHITE;
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
     * ChatColorで指定可能な色かどうかを判断する
     * @param color カラー表記の文字列
     * @return 指定可能かどうか
     */
    public static boolean isValidColor(String color) {

        for ( ChatColor c : ChatColor.values() ) {
            if ( c.name().equals(color.toUpperCase()) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * カラーコードかどうかを判断する
     * @param color カラー表記の文字列
     * @return 指定可能かどうか
     */
    public static boolean isValidColorCode(String code) {

        if ( code == null ) {
            return false;
        }
        return code.matches("&[0-9a-f]");
    }

    /**
     * 指定された名前のプレイヤーを返す
     * @param name プレイヤー名
     * @return プレイヤー、該当プレイヤーがオンラインでない場合はnullになる。
     */
    @SuppressWarnings("deprecation")
    public static Player getPlayerExact(String name) {
        return Bukkit.getPlayerExact(name);
    }
}
