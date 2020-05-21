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
import java.util.zip.ZipEntry;

import com.github.ucchyocean.lc.bukkit.BukkitUtility;
import com.github.ucchyocean.lc.bungee.BungeeUtility;

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
        if (source == null)
            return null;
        return ChatColor.translateAlternateColorCodes('&', source);
    }

    /**
     * 文字列に含まれているカラーコード（§a）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripColor(String source) {
        if (source == null)
            return null;
        return ChatColor.stripColor(source);
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

        for (ChatColor c : ChatColor.values()) {
            if (c.name().equals(color.toUpperCase())) {
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

        if (code == null) {
            return false;
        }
        return code.matches("&[0-9a-f]");
    }

    /**
     * 指定された名前のプレイヤーが接続したことがあるかどうかを検索する
     * @param name プレイヤー名
     * @return 接続したことがあるかどうか
     */
    public static boolean existsOfflinePlayer(String name) {
        if ( LunaChat.isBukkitMode() ) {
            return BukkitUtility.existsOfflinePlayer(name);
        } else {
            return BungeeUtility.existsOfflinePlayer(name);
        }
    }
}
