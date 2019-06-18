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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * ユーティリティクラス
 * @author ucchy
 */
public class Utility {

    private static Boolean isCB178orLaterCache;
    private static Boolean isCB19orLaterCache;

    /**
     * jarファイルの中に格納されているファイルを、jarファイルの外にコピーするメソッド
     * @param jarFile jarファイル
     * @param targetFile コピー先
     * @param sourceFilePath コピー元
     * @param isBinary バイナリファイルかどうか
     */
    public static void copyFileFromJar(
            File jarFile, File targetFile, String sourceFilePath, boolean isBinary) {

        JarFile jar = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        File parent = targetFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try {
            jar = new JarFile(jarFile);
            ZipEntry zipEntry = jar.getEntry(sourceFilePath);
            is = jar.getInputStream(zipEntry);

            fos = new FileOutputStream(targetFile);

            if (isBinary) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }

            } else {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                // CB190以降は、書き出すファイルエンコードにUTF-8を強制する。see issue #141.
                if ( isCB19orLater() ) {
                    writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                } else {
                    writer = new BufferedWriter(new OutputStreamWriter(fos));
                }

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
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
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
     * 現在動作中のCraftBukkitが、v1.7.8 以上かどうかを確認する
     * @return v1.7.8以上ならtrue、そうでないならfalse
     */
    public static boolean isCB178orLater() {
        if ( isCB178orLaterCache == null ) {
            isCB178orLaterCache = isUpperVersion(Bukkit.getBukkitVersion(), "1.7.8");
        }
        return isCB178orLaterCache;
    }

    /**
     * 現在動作中のCraftBukkitが、v1.9 以上かどうかを確認する
     * @return v1.9以上ならtrue、そうでないならfalse
     */
    public static boolean isCB19orLater() {
        if ( isCB19orLaterCache == null ) {
            isCB19orLaterCache = isUpperVersion(Bukkit.getBukkitVersion(), "1.9");
        }
        return isCB19orLaterCache;
    }

    /**
     * 指定されたバージョンが、基準より新しいバージョンかどうかを確認する
     * @param version 確認するバージョン
     * @param border 基準のバージョン
     * @return 基準より確認対象の方が新しいバージョンかどうか<br/>
     * ただし、無効なバージョン番号（数値でないなど）が指定された場合はfalseに、
     * 2つのバージョンが完全一致した場合はtrueになる。
     */
    public static boolean isUpperVersion(String version, String border) {

        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") )
                return false;
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        String[] borderArray = border.split("\\.");
        int[] borderNumbers = new int[borderArray.length];
        for ( int i=0; i<borderArray.length; i++ ) {
            if ( !borderArray[i].matches("[0-9]+") )
                return false;
            borderNumbers[i] = Integer.parseInt(borderArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 現在接続中のプレイヤーを全て取得する
     * @return 接続中の全てのプレイヤー
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Player> getOnlinePlayers() {
        // CB179以前と、CB1710以降で戻り値が異なるため、
        // リフレクションを使って互換性を（無理やり）保つ。
        try {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class) {
                Collection<?> temp =
                        ((Collection<?>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0])
                                .invoke(null, new Object[0]));
                return new ArrayList<Player>((Collection<? extends Player>) temp);
            } else {
                Player[] temp =
                        ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0])
                                .invoke(null, new Object[0]));
                ArrayList<Player> players = new ArrayList<Player>();
                for (Player t : temp) {
                    players.add(t);
                }
                return players;
            }
        } catch (NoSuchMethodException ex) {} // never happen
        catch (InvocationTargetException ex) {} // never happen
        catch (IllegalAccessException ex) {} // never happen
        return new ArrayList<Player>();
    }

    /**
     * 現在のサーバー接続人数を返します。
     * @return サーバー接続人数
     */
    public static int getOnlinePlayersCount() {
        return getOnlinePlayers().size();
    }

    /**
     * 指定された名前のオフラインプレイヤーを取得する
     * @param name プレイヤー名
     * @return オフラインプレイヤー
     */
    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null || (!player.hasPlayedBefore() && !player.isOnline()))
            return null;
        return player;
    }

    /**
     * 指定された名前のプレイヤーを取得する
     * @param name プレイヤー名
     * @return プレイヤー
     */
    @SuppressWarnings("deprecation")
    public static Player getPlayerExact(String name) {
        return Bukkit.getPlayer(stripColor(name));
    }

    /**
     * イベントを同期処理で呼び出します
     *
     * @param event 対象のイベント
     * @return タスクのID (登録に失敗した場合は-1)
     * @since 2.8.10
     */
    public static int callEventSync(final Event event) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(LunaChat.getInstance(), new Runnable() {

            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        });
    }
}
