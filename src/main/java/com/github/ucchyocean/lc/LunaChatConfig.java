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

    /** チャンネルチャット機能を利用可能にするかどうか */
    private boolean enableChannelChat;
    
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

    /** /ch join コマンドで存在しないチャンネルを指定したときに、
     *  チャンネルを新規作成して入室するかどうか */
    private boolean createChannelOnJoinCommand;

    /** サーバーに初参加したユーザーを参加させる、既定のチャンネル。<br/>
     *  参加させない場合は、から文字列 "" を指定すること。 */
    private String globalChannel;

    /** サーバーに参加したユーザーに必ず参加させるチャンネル。<br/>
     *  グローバルチャンネルとは別で指定できる。 */
    private List<String> forceJoinChannels;
    
    /** formatコマンド実行時に、必ず含まれる必要があるキーワード。 */
    private List<String> formatConstraint;

    /** ブロードキャストチャンネルの発言内容を、dynmapに送信するかどうか。<br/>
     *  dynmapがロードされていない場合は、この設定は無視される（false扱い）。 */
    private boolean sendBroadcastChannelChatToDynmap;
    
    /** dynmapへ送信するときに、チャンネルのフォーマットを反映して送信するかどうか。*/
    private boolean sendFormattedMessageToDynmap;
    
    /** dynmapのWebUIから発言された発言内容を表示するチャンネル。 */
    private String dynmapChannel;

    /** NGワードの設定 */
    private List<String> ngword;

    /** NGワードを発言した人に対して実行するアクション<br/>
     *  mask = マスクするのみ<br/>
     *  kick = マスクしてチャンネルからキックする<br/>
     *  ban = マスクしてチャンネルからBANする */
    private NGWordAction ngwordAction;

    /** 通常チャット（非チャンネルチャット）の装飾を、LunaChatから行うかどうか */
    private boolean enableNormalChatMessageFormat;

    /** 通常チャットの装飾フォーマット */
    private String normalChatMessageFormat;

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
    
    /** 発言に含まれているプレイヤー名を、Japanize変換から除外するかどうか */
    private boolean japanizeIgnorePlayerName;
    
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

        enableChannelChat = config.getBoolean("enableChannelChat", true);
        noJoinAsGlobal = config.getBoolean("noJoinAsGlobal", true);
        loggingChat = config.getBoolean("loggingChat", true);
        displayChatOnConsole = config.getBoolean("displayChatOnConsole", false);
        globalMarker = config.getString("globalMarker", "!");
        zeroMemberRemove = config.getBoolean("zeroMemberRemove", false);
        showListOnJoin = config.getBoolean("showListOnJoin", false);
        createChannelOnJoinCommand =
            config.getBoolean("createChannelOnJoinCommand", true);
        
        // チャンネルチャット有効のときだけ、globalChannel設定を読み込む
        // (see issue #58)
        if ( enableChannelChat ) {
            globalChannel = config.getString("globalChannel", "");
        } else {
            globalChannel = "";
        }
        // チャンネルチャット有効のときだけ、enableChannelChat設定を読み込む
        // (see issue #58)
        if ( enableChannelChat ) {
            forceJoinChannels = config.getStringList("forceJoinChannels");
        } else {
            forceJoinChannels = new ArrayList<String>();
        }
        
        if ( config.contains("formatConstraint") ) {
            formatConstraint = config.getStringList("formatConstraint");
        } else {
            formatConstraint = new ArrayList<String>();
            formatConstraint.add("%username");
            formatConstraint.add("%msg");
        }

        sendBroadcastChannelChatToDynmap =
            config.getBoolean("sendBroadcastChannelChatToDynmap", true);
        sendFormattedMessageToDynmap = 
            config.getBoolean("sendFormattedMessageToDynmap", false);
        dynmapChannel = config.getString("dynmapChannel", "");
        ngword = config.getStringList("ngword");
        ngwordAction = NGWordAction.fromID(config.getString("ngwordAction", "mask"));
        
        enableNormalChatMessageFormat = 
                config.getBoolean("enableNormalChatMessageFormat", true);
        normalChatMessageFormat = 
                config.getString("normalChatMessageFormat", "&f<%prefix%username%suffix&f> %msg");
        
        japanizeType = JapanizeType.fromID(config.getString("japanizeType", "kana"));
        japanizeDisplayLine = config.getInt("japanizeDisplayLine", 2);
        if ( japanizeDisplayLine != 1 && japanizeDisplayLine != 2 ) {
            japanizeDisplayLine = 2;
        }
        japanizeLine1Format = config.getString("japanizeLine1Format", "%msg &7(%japanize)");
        japanizeLine2Format = config.getString("japanizeLine2Format", "&6[JP] %japanize");
        japanizeIgnorePlayerName = config.getBoolean("japanizeIgnorePlayerName", true);
        noneJapanizeMarker = config.getString("noneJapanizeMarker", "#");
        japanizeWait = config.getInt("japanizeWait", 1);

        // globalチャンネルが、使用可能なチャンネル名かどうかを調べる
        if ( !globalChannel.equals("") && !LunaChat.manager.checkForChannelName(globalChannel) ) {
            String msg = String.format(
                    Resources.get("errmsgCannotUseForGlobal"), globalChannel);
            LunaChat.instance.getLogger().warning(msg);
            globalChannel = "";
        }

        // チャンネルチャット無効なら、デフォルト発言先をクリアする(see issue #59)
        if ( !enableChannelChat ) {
            LunaChat.manager.removeAllDefaultChannels();
        }
    }

    /** 
     * チャンネルチャット機能を利用可能にするかどうか
     * @return enableChannelChatを返す
     */
    public boolean isEnableChannelChat() {
        return enableChannelChat;
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
     * formatコマンド実行時に、必ず含まれる必要があるキーワード。
     * @return formatConstraintを返す
     */
    public List<String> getFormatConstraint() {
        return formatConstraint;
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
     * dynmapへ送信するときに、チャンネルのフォーマットを反映して送信するかどうか。
     * @return sendFormattedMessageToDynmapを返す
     */
    public boolean isSendFormattedMessageToDynmap() {
        return sendFormattedMessageToDynmap;
    }

    /**
     * dynmapのWebUIから発言された発言内容を表示するチャンネル。
     * @return dynmapChannel dynmapの発言を表示するチャンネル名を返す
     */
    public String getDynmapChannel() {
        return dynmapChannel;
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
     * 通常チャット（非チャンネルチャット）の装飾を、LunaChatから行うかどうか
     * @return enableNormalChatMessageFormatを返す
     */
    public boolean isEnableNormalChatMessageFormat() {
        return enableNormalChatMessageFormat;
    }

    /**
     * 通常チャットの装飾フォーマット
     * @return normalChatMessageFormatを返す
     */
    public String getNormalChatMessageFormat() {
        return normalChatMessageFormat;
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
     * 発言に含まれているプレイヤー名を、Japanize変換から除外するかどうか
     * @return japanizeIgnorePlayerName
     */
    public boolean isJapanizeIgnorePlayerName() {
        return japanizeIgnorePlayerName;
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
