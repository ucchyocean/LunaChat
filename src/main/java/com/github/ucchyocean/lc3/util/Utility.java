/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

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

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatMode;
import com.google.common.io.Files;

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

        if ( jarFile.isDirectory() ) {
            File file = new File(jarFile, sourceFilePath);

            try {
                Files.copy(file, targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

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
    }

    /**
     * 文字列内のカラーコード候補（&a）を、カラーコード（§a）に置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String replaceColorCode(String source) {
        if (source == null) return null;
        return replaceWebColorCode(source)
                .replaceAll("&([0-9a-fk-orA-FK-OR])", "\u00A7$1");
    }

    /**
     * Webカラーコード（#99AABBなど）を、カラーコードに置き換えする
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    private static String replaceWebColorCode(String source) {
        return source
                .replaceAll(
                        "#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                        "\u00A7x\u00A7$1\u00A7$2\u00A7$3\u00A7$4\u00A7$5\u00A7$6")
                .replaceAll(
                        "#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])",
                        "\u00A7x\u00A7$1\u00A7$1\u00A7$2\u00A7$2\u00A7$3\u00A7$3");
    }

    /**
     * 文字列に含まれているカラーコード（§a）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripColorCode(String source) {
        if (source == null) return null;
        return stripAltColorCode(source).replaceAll("\u00A7([0-9a-fk-orxA-FK-ORX])", "");
    }

    /**
     * 文字列に含まれているカラーコード候補（&aや#99AABB）を除去して返す
     * @param source 置き換え元の文字列
     * @return 置き換え後の文字列
     */
    public static String stripAltColorCode(String source) {
        if (source == null) return null;
        source = source.replaceAll("#[0-9a-fA-F]{6}", "").replaceAll("#[0-9a-fA-F]{3}", "");
        return source.replaceAll("&([0-9a-fk-orxA-FK-ORX])", "");
    }

    /**
     * カラーコード（§a）かどうかを判断する
     * @param code カラーコード
     * @return カラーコードかどうか
     */
    public static boolean isColorCode(String code) {
        if (code == null) return false;
        return code.matches("\u00A7[0-9a-fk-orxA-FK-ORX]");
    }

    /**
     * カラーコード候補（&aや#99AABB）かどうかを判断する
     * @param color カラーコード候補
     * @return カラーコード候補かどうか
     */
    public static boolean isAltColorCode(String code) {
        if (code == null) return false;
        return code.matches("(&[0-9a-fk-orA-FK-OR]|#[0-9a-fA-F]{3}|#[0-9a-fA-F]{6})");
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
     * カラー表記の文字列（REDとかGREENとか）を、カラーコード候補（&a）に変換する
     * @param color カラー表記の文字列
     * @return カラーコード候補
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
        if ( LunaChat.getUUIDCacheData().getUUIDFromName(name) != null ) {
            return true;
        }
        if ( LunaChat.getMode() == LunaChatMode.BUKKIT ) {
            return UtilityBukkit.existsOfflinePlayer(name);
        }
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
