/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * @author ucchy
 * LunaChatのコンフィグクラス
 */
public class LunaChatConfig {

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

    /** Japanize変換のタイプ
     *  none = 日本語変換をしない
     *  kana = カナ変換のみする
     *  googleime = カナ変換後、GoogleIMEで漢字変換する
     *  socialime = カナ変換後、SocialIMEで漢字変換する */
    protected JapanizeType japanizeType;

    /** Japanize変換の1行表示と2行表示の切り替え
     *  1 = 1行表示
     *  2 = 2行表示 */
    protected int japanizeDisplayLine;

    /** Japanize変換の1行表示時のフォーマット */
    protected String japanizeLine1Format;

    /** Japanize変換の2行表示時の2行目のフォーマット */
    protected String japanizeLine2Format;

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
        japanizeType = JapanizeType.fromID(config.getString("japanizeType", "kana"));
        japanizeDisplayLine = config.getInt("japanizeDisplayLine", 2);
        if ( japanizeDisplayLine != 1 && japanizeDisplayLine != 2 ) {
            japanizeDisplayLine = 2;
        }
        japanizeLine1Format = config.getString("japanizeLine1Format", "%msg (%japanize)");
        japanizeLine2Format = config.getString("japanizeLine2Format", "&6[JP] %japanize");

        // globalチャンネルが、使用可能なチャンネル名かどうかを調べる
        if ( !LunaChat.manager.checkForChannelName(globalChannel) ) {
            String msg = String.format(Utility.replaceColorCode(
                    Resources.get("errmsgCannotUseForGlobal")), globalChannel);
            LunaChat.instance.getLogger().warning(msg);
            globalChannel = "";
        }
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

    /**
     * @return japanizeTypeを返す
     */
    public JapanizeType getJapanizeType() {
        return japanizeType;
    }

    /**
     * @param japanizeType japanizeTypeを設定する
     */
    public void setJapanizeType(JapanizeType japanizeType) {
        this.japanizeType = japanizeType;
    }

    /**
     * @return japanizeDisplayLineを返す
     */
    public int getJapanizeDisplayLine() {
        return japanizeDisplayLine;
    }

    /**
     * @param japanizeDisplayLine japanizeDisplayLineを設定する
     */
    public void setJapanizeDisplayLine(int japanizeDisplayLine) {
        this.japanizeDisplayLine = japanizeDisplayLine;
    }

    /**
     * @return japanizeLine1Formatを返す
     */
    public String getJapanizeLine1Format() {
        return japanizeLine1Format;
    }

    /**
     * @param japanizeLine1Format japanizeLine1Formatを設定する
     */
    public void setJapanizeLine1Format(String japanizeLine1Format) {
        this.japanizeLine1Format = japanizeLine1Format;
    }

    /**
     * @return japanizeLine2Formatを返す
     */
    public String getJapanizeLine2Format() {
        return japanizeLine2Format;
    }

    /**
     * @param japanizeLine2Format japanizeLine2Formatを設定する
     */
    public void setJapanizeLine2Format(String japanizeLine2Format) {
        this.japanizeLine2Format = japanizeLine2Format;
    }
}
