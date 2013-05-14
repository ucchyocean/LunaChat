/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.List;

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

    /** 全てのメンバーが退出したときに、チャンネルを削除するかどうか */
    protected boolean zeroMemberRemove;

    /** ログイン時に、参加中チャンネルを表示するかどうか */
    protected boolean showListOnJoin;

    /** /ch join コマンドで存在しないチャンネルを指定したときに、チャンネルを新規作成して入室するかどうか */
    protected boolean createChannelOnJoinCommand;

    /** サーバーに初参加したユーザーを参加させる、既定のチャンネル。
     *  参加させない場合は、から文字列 "" を指定すること。 */
    protected String globalChannel;

    /** NGワードの設定 */
    protected List<String> ngword;

    /** NGワードを発言した人に対して実行するアクション<br>
     *  mask = マスクするのみ<br>
     *  kick = マスクしてチャンネルからキックする<br>
     *  ban = マスクしてチャンネルからBANする */
    protected NGWordAction ngwordAction;

    /**
     * コンストラクタ
     */
    protected LunaChatConfig() {
        reloadConfig();
    }

    /**
     * config.yml を再読み込みする
     */
    public void reloadConfig() {

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
        zeroMemberRemove = config.getBoolean("zeroMemberRemove", false);
        showListOnJoin = config.getBoolean("showListOnJoin", false);
        createChannelOnJoinCommand =
                config.getBoolean("createChannelOnJoinCommand", true);
        globalChannel = config.getString("globalChannel", "");
        ngword = config.getStringList("ngword");
        ngwordAction = NGWordAction.fromID(config.getString("ngwordAction", "mask"));

        // globalチャンネルが、使用可能なチャンネル名かどうかを調べる
        if ( !LunaChat.manager.checkForChannelName(globalChannel) ) {
            String msg = String.format(Utility.replaceColorCode(
                    Resources.get("errmsgCannotUseForGlobal")), globalChannel);
            LunaChat.instance.getLogger().warning(msg);
            globalChannel = "";
        }
    }

    /**
     * @return displayJapanizeを返す
     */
    public boolean isDisplayJapanize() {
        return displayJapanize;
    }

    /**
     * @param displayJapanize displayJapanizeを設定する
     */
    public void setDisplayJapanize(boolean displayJapanize) {
        this.displayJapanize = displayJapanize;
    }

    /**
     * @return noJoinAsGlobalを返す
     */
    public boolean isNoJoinAsGlobal() {
        return noJoinAsGlobal;
    }

    /**
     * @param noJoinAsGlobal noJoinAsGlobalを設定する
     */
    public void setNoJoinAsGlobal(boolean noJoinAsGlobal) {
        this.noJoinAsGlobal = noJoinAsGlobal;
    }

    /**
     * @return loggingChatを返す
     */
    public boolean isLoggingChat() {
        return loggingChat;
    }

    /**
     * @param loggingChat loggingChatを設定する
     */
    public void setLoggingChat(boolean loggingChat) {
        this.loggingChat = loggingChat;
    }

    /**
     * @return globalMarkerを返す
     */
    public String getGlobalMarker() {
        return globalMarker;
    }

    /**
     * @param globalMarker globalMarkerを設定する
     */
    public void setGlobalMarker(String globalMarker) {
        this.globalMarker = globalMarker;
    }

    /**
     * @return zeroMemberRemoveを返す
     */
    public boolean isZeroMemberRemove() {
        return zeroMemberRemove;
    }

    /**
     * @param zeroMemberRemove zeroMemberRemoveを設定する
     */
    public void setZeroMemberRemove(boolean zeroMemberRemove) {
        this.zeroMemberRemove = zeroMemberRemove;
    }

    /**
     * @return showListOnJoinを返す
     */
    public boolean isShowListOnJoin() {
        return showListOnJoin;
    }

    /**
     * @param showListOnJoin showListOnJoinを設定する
     */
    public void setShowListOnJoin(boolean showListOnJoin) {
        this.showListOnJoin = showListOnJoin;
    }

    /**
     * @return createChannelOnJoinCommandを返す
     */
    public boolean isCreateChannelOnJoinCommand() {
        return createChannelOnJoinCommand;
    }

    /**
     * @param createChannelOnJoinCommand createChannelOnJoinCommandを設定する
     */
    public void setCreateChannelOnJoinCommand(boolean createChannelOnJoinCommand) {
        this.createChannelOnJoinCommand = createChannelOnJoinCommand;
    }

    /**
     * @return globalChannelを返す
     */
    public String getGlobalChannel() {
        return globalChannel;
    }

    /**
     * @param globalChannel globalChannelを設定する
     */
    public void setGlobalChannel(String globalChannel) {
        this.globalChannel = globalChannel;
    }

    /**
     * @return ngwordを返す
     */
    public List<String> getNgword() {
        return ngword;
    }

    /**
     * @param ngword ngwordを設定する
     */
    public void setNgword(List<String> ngword) {
        this.ngword = ngword;
    }

    /**
     * @return ngwordActionを返す
     */
    public NGWordAction getNgwordAction() {
        return ngwordAction;
    }

    /**
     * @param ngwordAction ngwordActionを設定する
     */
    public void setNgwordAction(NGWordAction ngwordAction) {
        this.ngwordAction = ngwordAction;
    }

//    /**
//     * config.yml に、設定値を保存する
//     * @param key 設定値のキー
//     * @param value 設定値の値
//     */
//    public static void setConfigValue(String key, Object value) {
//
//        FileConfiguration config = LunaChat.instance.getConfig();
//        config.set(key, value);
//        LunaChat.instance.saveConfig();
//    }
}
