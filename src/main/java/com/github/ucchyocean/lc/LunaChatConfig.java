/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * LunaChatのコンフィグクラス
 * @author ucchy
 */
public class LunaChatConfig {

    /** チャンネルチャットに入っていない人の発言を、グローバルとして扱うかどうか */
    private boolean noJoinAsGlobal;

    /** チャンネルチャットの発言内容を、ログに残すかどうか */
    private boolean loggingChat;

    /** チャンネルチャットの発言内容を、コンソールに表示するかどうか */
    private boolean displayChatOnConsole;

    /** グローバルマーカー  これが発言の頭に入っている場合は、強制的にグローバル発言になる */
    private String globalMarker;

    /** 全てのメンバーが退出したときに、チャンネルを削除するかどうか */
    private boolean zeroMemberRemove;

    /** ログイン時に、参加中チャンネルを表示するかどうか */
    private boolean showListOnJoin;

    /** /ch join コマンドで存在しないチャンネルを指定したときに、チャンネルを新規作成して入室するかどうか */
    private boolean createChannelOnJoinCommand;

    /** サーバーに初参加したユーザーを参加させる、既定のチャンネル。<br/>
     *  参加させない場合は、から文字列 "" を指定すること。 */
    private String globalChannel;

    /** サーバーに参加したユーザーに必ず参加させるチャンネル。<br/>
     *  グローバルチャンネルとは別で指定できる。 */
    private List<String> forceJoinChannels;

    /** ブロードキャストチャンネルの発言内容を、dynmapに送信するかどうか。<br/>
     *  dynmapがロードされていない場合は、この設定は無視される（false扱い）。 */
    private boolean sendBroadcastChannelChatToDynmap;

    /** NGワードの設定 */
    private List<String> ngword;

    /** NGワードを発言した人に対して実行するアクション<br/>
     *  mask = マスクするのみ<br/>
     *  kick = マスクしてチャンネルからキックする<br/>
     *  ban = マスクしてチャンネルからBANする */
    private NGWordAction ngwordAction;

    /** Japanize変換のタイプ<br/>
     *  none = 日本語変換をしない<br/>
     *  kana = カナ変換のみする<br/>
     *  googleime = カナ変換後、GoogleIMEで漢字変換する<br/>
     *  socialime = カナ変換後、SocialIMEで漢字変換する */
    private JapanizeType japanizeType;

    /** Japanize変換の1行表示と2行表示の切り替え<br/>
     *  1 = 1行表示<br/>
     *  2 = 2行表示 */
    private int japanizeDisplayLine;

    /** Japanize変換の1行表示時のフォーマット */
    private String japanizeLine1Format;

    /** Japanize変換の2行表示時の2行目のフォーマット */
    private String japanizeLine2Format;
    
    /** ノンジャパナイズマーカー これが発言の頭に入っている場合は、一時的にjapanizeを実行しない */
    private String noneJapanizeMarker;
    
    /** 通常チャットで、JapanizeDisplayLine=2のとき、Japanize変換したあと表示するまでのウェイト(tick)
     *  隠し設定。 */
    private int japanizeWait;

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
            //LunaChat.instance.saveDefaultConfig();
            Utility.copyFileFromJar(LunaChat.getPluginJarFile(), 
                    configFile, "config_ja.yml", false);
        }

        LunaChat.instance.reloadConfig();
        FileConfiguration config = LunaChat.instance.getConfig();

        noJoinAsGlobal = config.getBoolean("noJoinAsGlobal", true);
        loggingChat = config.getBoolean("loggingChat", true);
        displayChatOnConsole = config.getBoolean("displayChatOnConsole", false);
        globalMarker = config.getString("globalMarker", "!");
        zeroMemberRemove = config.getBoolean("zeroMemberRemove", false);
        showListOnJoin = config.getBoolean("showListOnJoin", false);
        createChannelOnJoinCommand =
            config.getBoolean("createChannelOnJoinCommand", true);
        globalChannel = config.getString("globalChannel", "");
        forceJoinChannels = config.getStringList("forceJoinChannels");
        if ( forceJoinChannels == null ) {
            forceJoinChannels = new ArrayList<String>();
        }
        sendBroadcastChannelChatToDynmap =
            config.getBoolean("sendBroadcastChannelChatToDynmap", true);
        ngword = config.getStringList("ngword");
        ngwordAction = NGWordAction.fromID(config.getString("ngwordAction", "mask"));
        japanizeType = JapanizeType.fromID(config.getString("japanizeType", "kana"));
        japanizeDisplayLine = config.getInt("japanizeDisplayLine", 2);
        if ( japanizeDisplayLine != 1 && japanizeDisplayLine != 2 ) {
            japanizeDisplayLine = 2;
        }
        japanizeLine1Format = config.getString("japanizeLine1Format", "%msg (%japanize)");
        japanizeLine2Format = config.getString("japanizeLine2Format", "&6[JP] %japanize");
        noneJapanizeMarker = config.getString("noneJapanizeMarker", "#");
        japanizeWait = config.getInt("japanizeWait", 1);

        // globalチャンネルが、使用可能なチャンネル名かどうかを調べる
        if ( !globalChannel.equals("") && !LunaChat.manager.checkForChannelName(globalChannel) ) {
            String msg = String.format(
                    Resources.get("errmsgCannotUseForGlobal"), globalChannel);
            LunaChat.instance.getLogger().warning(msg);
            globalChannel = "";
        }
    }

    /**
     * チャンネルチャットに入っていない人の発言を、グローバルとして扱うかどうか
     * @return noJoinAsGlobalを返す
     */
    public boolean isNoJoinAsGlobal() {
        return noJoinAsGlobal;
    }

    /**
     * チャンネルチャットの発言内容を、ログに残すかどうか
     * @return loggingChatを返す
     */
    public boolean isLoggingChat() {
        return loggingChat;
    }

    /**
     * チャンネルチャットの発言内容を、コンソールに表示するかどうか
     * @return displayChatOnConsoleを返す
     */
    public boolean isDisplayChatOnConsole() {
        return displayChatOnConsole;
    }

    /**
     * グローバルマーカー  これが発言の頭に入っている場合は、強制的にグローバル発言になる
     * @return globalMarkerを返す
     */
    public String getGlobalMarker() {
        return globalMarker;
    }

    /**
     * 全てのメンバーが退出したときに、チャンネルを削除するかどうか
     * @return zeroMemberRemoveを返す
     */
    public boolean isZeroMemberRemove() {
        return zeroMemberRemove;
    }

    /**
     * ログイン時に、参加中チャンネルを表示するかどうか
     * @return showListOnJoinを返す
     */
    public boolean isShowListOnJoin() {
        return showListOnJoin;
    }

    /**
     * /ch join コマンドで存在しないチャンネルを指定したときに、チャンネルを新規作成して入室するかどうか
     * @return createChannelOnJoinCommandを返す
     */
    public boolean isCreateChannelOnJoinCommand() {
        return createChannelOnJoinCommand;
    }

    /**
     * サーバーに初参加したユーザーを参加させる、既定のチャンネル。<br/>
     * 参加させない場合は、から文字列 "" を指定すること。
     * @return globalChannelを返す
     */
    public String getGlobalChannel() {
        return globalChannel;
    }

    /**
     * サーバーに参加したユーザーに必ず参加させるチャンネル。<br/>
     * グローバルチャンネルとは別で指定できる。
     * @return globalChannelを返す
     */
    public List<String> getForceJoinChannels() {
        return forceJoinChannels;
    }

    /**
     * NGワード
     * @return ngwordを返す
     */
    public List<String> getNgword() {
        return ngword;
    }

    /**
     * ブロードキャストチャンネルの発言内容を、dynmapに送信するかどうか。<br/>
     * dynmapがロードされていない場合は、この設定は無視される（false扱い）。
     * @return sendBroadcastChannelChatToDynmapを返す
     */
    public boolean isSendBroadcastChannelChatToDynmap() {
        return sendBroadcastChannelChatToDynmap;
    }

    /**
     * NGワードを発言した人に対して実行するアクション<br/>
     *  mask = マスクするのみ<br/>
     *  kick = マスクしてチャンネルからキックする<br/>
     *  ban = マスクしてチャンネルからBANする
     * @return ngwordActionを返す
     */
    public NGWordAction getNgwordAction() {
        return ngwordAction;
    }

    /**
     * Japanize変換のタイプ<br/>
     *  none = 日本語変換をしない<br/>
     *  kana = カナ変換のみする<br/>
     *  googleime = カナ変換後、GoogleIMEで漢字変換する<br/>
     *  socialime = カナ変換後、SocialIMEで漢字変換する
     * @return japanizeTypeを返す
     */
    public JapanizeType getJapanizeType() {
        return japanizeType;
    }

    /**
     * Japanize変換の1行表示と2行表示の切り替え<br/>
     *  1 = 1行表示<br/>
     *  2 = 2行表示<br/>
     * @return japanizeDisplayLineを返す
     */
    public int getJapanizeDisplayLine() {
        return japanizeDisplayLine;
    }

    /**
     * Japanize変換の1行表示時のフォーマット
     * @return japanizeLine1Formatを返す
     */
    public String getJapanizeLine1Format() {
        return japanizeLine1Format;
    }

    /**
     * Japanize変換の2行表示時の2行目のフォーマット
     * @return japanizeLine2Formatを返す
     */
    public String getJapanizeLine2Format() {
        return japanizeLine2Format;
    }

    /**
     * ノンジャパナイズマーカー これが発言の頭に入っている場合は、一時的にjapanizeを実行しない
     * @return noneJapanizeMarkerを返す
     */
    public String getNoneJapanizeMarker() {
        return noneJapanizeMarker;
    }
    
    /**
     * 通常チャットで、JapanizeDisplayLine=2のとき、Japanize変換したあと表示するまでのウェイト(tick)
     * @return japanizeWaitを返す
     */
    public int getJapanizeWait() {
        return japanizeWait;
    }
}
