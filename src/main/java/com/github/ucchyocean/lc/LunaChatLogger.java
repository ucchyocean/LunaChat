/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * LunaChatロガー
 * @author ucchy
 */
public class LunaChatLogger {

    private SimpleDateFormat lformat;
    private SimpleDateFormat dformat;

    private SimpleDateFormat logYearDateFormat;

    private File file;
    private String dirPath;
    private String name;

    /**
     * コンストラクタ
     * @param name ログ名
     */
    public LunaChatLogger(String name) {

        lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dformat = new SimpleDateFormat("yyyy-MM-dd");

        logYearDateFormat = new SimpleDateFormat("yyyyMMdd");

        this.name = name;
        checkDir();
    }

    /**
     * ログを出力する
     * @param message ログ内容
     * @param player 発言者名
     */
    public synchronized void log(final String message, final String player) {

        checkDir();

        // 以降の処理を、発言処理の負荷軽減のため、非同期実行にする。(see issue #40.)
        new BukkitRunnable() {
            @Override
            public void run() {

                String msg = ChatColor.stripColor(message);
                if ( msg == null ) msg = "";
                msg = msg.replace(",", "，");
                FileWriter writer = null;
                try {
                    writer = new FileWriter(file, true);
                    String str = lformat.format(new Date()) +
                            "," + msg + "," + player;
                    writer.write(str + "\r\n");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if ( writer != null ) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            // do nothing.
                        }
                    }
                }

            }
        }.runTaskAsynchronously(LunaChat.getInstance());
    }

    /**
     * ログファイルを読み込んで、ログデータを取得する
     * @param player プレイヤー名、フィルタしないならnullを指定すること
     * @param filter フィルタ、フィルタしないならnullを指定すること
     * @param date 日付、今日のデータを取得するならnullを指定すること
     * @param reverse 逆順取得
     * @return ログデータ
     */
    public ArrayList<String> getLog(
            String player, String filter, String date, boolean reverse) {

        // 指定された日付のログを取得する
        File f = getLogFile(date);
        if ( f == null ) {
            return new ArrayList<String>();
        }

        // ログファイルの読み込み
        ArrayList<String> data = readAllLines(f);

        // プレイヤー指定なら、一致するプレイヤー名が含まれているログに絞る
        if ( player != null ) {
            ArrayList<String> temp = new ArrayList<String>(data);
            data = new ArrayList<String>();
            for ( String t : temp ) {
                String[] line = t.split(",");
                if ( line.length >= 3 && line[2].contains(player) ) {
                    data.add(t);
                }
            }
        }

        // フィルタ指定なら、指定のキーワードが含まれているログに絞る
        if ( filter != null ) {
            ArrayList<String> temp = new ArrayList<String>(data);
            data = new ArrayList<String>();
            for ( String t : temp ) {
                String[] line = t.split(",");
                if ( line.length >= 2 && line[1].contains(filter) ) {
                    data.add(t);
                }
            }
        }

        // 逆順が指定されているなら、逆順に並び替える
        if ( reverse ) {
            Collections.reverse(data);
        }

        return data;
    }

    /**
     * テキストファイルの内容を読み出す。
     * @param file ファイル
     * @return 内容
     */
    private ArrayList<String> readAllLines(File file) {

        ArrayList<String> data = new ArrayList<String>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ( (line = reader.readLine()) != null ) {
                line = line.trim();
                if ( line.length() > 0 ) {
                    data.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing.
                }
            }
        }

        return data;
    }

    /**
     * 指定された日付のログファイルを取得します。
     * 取得できない場合は、nullを返します。
     * @param date 日付
     * @return 指定された日付のログファイル
     */
    private File getLogFile(String date) {

        if ( date == null ) {
            return file;
        }

        Date d;
        try {
            if ( date.matches("[0-9]{4}") ) {
                date = Calendar.getInstance().get(Calendar.YEAR) + date;
            }
            if ( date.matches("[0-9]{8}") ) {
                d = logYearDateFormat.parse(date);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        File folder = new File(getFolderPath(d));
        if ( !folder.exists() || !folder.isDirectory() ) {
            return null;
        }

        File f = new File(folder, name + ".log");
        if ( !f.exists() ) {
            return null;
        }

        return f;
    }

    /**
     * ログの出力先フォルダをチェックし、変更されるなら更新する。
     */
    private void checkDir() {

        String temp = getFolderPath(new Date());
        if ( temp.equals(dirPath) ) {
            return;
        }
        dirPath = temp;

        File dir = new File(dirPath);
        if ( !dir.exists() || !dir.isDirectory() ) {
            dir.mkdirs();
        }

        file = new File(dir, name + ".log");
    }

    /**
     * 指定された日付のログファイル名を生成して返します。
     * @param date 日付
     * @return ログファイル名
     */
    private String getFolderPath(Date date) {

        return LunaChat.getInstance().getDataFolder() +
                File.separator + "logs" +
                File.separator + dformat.format(date);
    }
}
