/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author ucchy
 * LunaChatのコンフィグクラス
 */
public class LunaChatConfig {

    /** Japanize変換をおこなうかどうか */
    protected boolean displayJapanize;

    /** チャンネルチャットに入っていない人の発言を、グローバルとして扱うかどうか */
    protected boolean noJoinAsGlobal;

    /** チャンネルチャットの発言内容を、ログに残すかどうか */
    protected boolean loggingChat;

    /** グローバルマーカー  これが発言の頭に入っている場合は、強制的にグローバル発言になる */
    protected String globalMarker;

    /** 発言先移動時に、移動元のチャンネルから退出するかどうか */
    protected boolean autoLeavingOnMove;

    /** サーバーから退出したときに、全ての参加チャンネルから退出するかどうか */
    protected boolean autoLeavingOnQuit;

    /** 全てのメンバーが退出したときに、チャンネルを削除するかどうか */
    protected boolean zeroMemberRemove;

    /** ログイン時に、参加中チャンネルを表示するかどうか */
    protected boolean showListOnJoin;

    /** /ch join コマンドで存在しないチャンネルを指定したときに、チャンネルを新規作成して入室するかどうか */
    protected boolean createChannelOnJoinCommand;

    /** サーバーに初参加したユーザーを参加させる、既定のチャンネル。
     *  参加させない場合は、から文字列 "" を指定すること。 */
    protected String globalChannel;

    /**
     * コンストラクタ
     */
    protected LunaChatConfig() {
        reloadConfig();
    }

    /**
     * config.yml を再読み込みする
     */
    protected void reloadConfig() {

        File configFile = new File(LunaChat.instance.getDataFolder(), "config.yml");
        if ( !configFile.exists() ) {
            LunaChat.instance.saveDefaultConfig();
        }

        LunaChat.instance.reloadConfig();
        FileConfiguration config = LunaChat.instance.getConfig();

        displayJapanize = config.getBoolean("displayJapanize", true);
        noJoinAsGlobal = config.getBoolean("noJoinAsGlobal", true);
        loggingChat = config.getBoolean("loggingChat", true);
        globalMarker = config.getString("globalMarker", "!");
        autoLeavingOnMove = config.getBoolean("autoLeavingOnMove", false);
        autoLeavingOnQuit = config.getBoolean("autoLeavingOnQuit", false);
        zeroMemberRemove = config.getBoolean("zeroMemberRemove", false);
        showListOnJoin = config.getBoolean("showListOnJoin", false);
        createChannelOnJoinCommand =
                config.getBoolean("createChannelOnJoinCommand", true);
        globalChannel = config.getString("globalChannel", "");
    }

    /**
     * config.yml に、設定値を保存する
     * @param key 設定値のキー
     * @param value 設定値の値
     */
    public static void setConfigValue(String key, Object value) {

        FileConfiguration config = LunaChat.instance.getConfig();
        config.set(key, value);
        LunaChat.instance.saveConfig();
    }
}
