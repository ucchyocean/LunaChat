/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.util.EventPriority;
import com.github.ucchyocean.lc3.util.Utility;
import com.github.ucchyocean.lc3.util.YamlConfig;

/**
 * LunaChatのコンフィグクラス
 * @author ucchy
 */
public class LunaChatConfig {

    /** メッセージの言語 */
    private String lang;

    /** チャンネルチャット機能を利用可能にするかどうか */
    private boolean enableChannelChat;

    /** チャットイベントの処理優先度 */
    private EventPriority playerChatEventListenerPriority;

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

    /** チャンネルを作成したときに、デフォルトで設定されるフォーマット */
    private String defaultFormat;

    /** プライベートメッセージを送信するときに、適用されるフォーマット */
    private String defaultFormatForPrivateMessage;

    /** OPの画面に、全チャンネルの発言内容を表示するかどうか */
    private boolean opListenAllChannel;

    /** チャンネルを新規作成するときに、チャンネル名が満たさなければならない、最低文字列長 */
    private int minChannelNameLength;

    /** チャンネルを新規作成するときに、チャンネル名が満たさなければならない、最大文字列長 */
    private int maxChannelNameLength;

    /** クイックチャンネルチャット機能を有効化するかどうか */
    private boolean enableQuickChannelChat;

    /** クイックチャンネルチャット機能に使用する記号 */
    private String quickChannelChatSeparator;

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

    /** NGワードの設定、正規表現マッチング用にコンパイルされたもの */
    private List<Pattern> ngwordCompiled;

    /** 通常チャット（非チャンネルチャット）の装飾を、LunaChatから行うかどうか */
    private boolean enableNormalChatMessageFormat;

    /** 通常チャットの装飾フォーマット */
    private String normalChatMessageFormat;

    /** 通常チャットで、カラーコードを使用可能にするかどうか */
    private boolean enableNormalChatColorCode;

    /** 通常チャットを、クリック可能にするかどうか */
    private boolean enableNormalChatClickable;

    /** 通常チャットを、コンソールに表示するかどうか */
    private boolean displayNormalChatOnConsole;

    /** Japanize変換のタイプ<br/>
     *  none = 日本語変換をしない<br/>
     *  kana = カナ変換のみする<br/>
     *  googleime = カナ変換後、GoogleIMEで漢字変換する */
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

    // === 以下、BungeeCord用設定 ===

    /** Bungeeパススルーモード */
    private boolean bungeePassThroughMode;

    /**
     * コンストラクタ
     * @param dataFolder コンフィグ格納フォルダ
     * @param jarFile プラグインJarファイル
     */
    public LunaChatConfig(File dataFolder, File jarFile) {
        reloadConfig(dataFolder, jarFile);
    }

    /**
     * config.yml を再読み込みする
     */
    public void reloadConfig(File dataFolder, File jarFile) {

        File configFile = new File(dataFolder, "config.yml");
        if ( !configFile.exists() ) {
            Utility.copyFileFromJar(jarFile, configFile, "config_ja.yml", false);
            String language = Utility.getDefaultLocale().getLanguage();
            if ( language.equals("ja") ) {
                Utility.copyFileFromJar(jarFile, configFile, "config_ja.yml", false);
            } else {
                Utility.copyFileFromJar(jarFile, configFile, "config.yml", false);
            }
        }

        YamlConfig config = YamlConfig.load(configFile);

        lang = config.getString("lang", "en");
        enableChannelChat = config.getBoolean("enableChannelChat", true);
        playerChatEventListenerPriority
            = getEventPriority(config.getString("playerChatEventListenerPriority"), EventPriority.HIGHEST);
        noJoinAsGlobal = config.getBoolean("noJoinAsGlobal", true);
        loggingChat = config.getBoolean("loggingChat", true);
        displayChatOnConsole = config.getBoolean("displayChatOnConsole", true);
        globalMarker = config.getString("globalMarker", "!");
        zeroMemberRemove = config.getBoolean("zeroMemberRemove", false);
        showListOnJoin = config.getBoolean("showListOnJoin", false);
        createChannelOnJoinCommand =
            config.getBoolean("createChannelOnJoinCommand", false);

        // チャンネルチャット有効のときだけ、globalChannel設定を読み込む
        // (see issue #58)
        if ( enableChannelChat ) {
            globalChannel = config.getString("globalChannel", "");
        } else {
            globalChannel = "";
        }
        // チャンネルチャット有効のときだけ、forceJoinChannels設定を読み込む
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

        defaultFormat = config.getString("defaultFormat",
                "&f[%color%ch&f]%prefix%username%suffix&a:&f %msg");
        defaultFormatForPrivateMessage =
                config.getString("defaultFormatForPrivateMessage",
                "&7[%player -> %to]&f %msg");

        opListenAllChannel = config.getBoolean("opListenAllChannel", false);

        minChannelNameLength = config.getInt("minChannelNameLength", 4);
        maxChannelNameLength = config.getInt("maxChannelNameLength", 20);

        enableQuickChannelChat = config.getBoolean("enableQuickChannelChat", true);
        quickChannelChatSeparator = config.getString("quickChannelChatSeparator", ":");

        sendBroadcastChannelChatToDynmap =
            config.getBoolean("sendBroadcastChannelChatToDynmap", true);
        sendFormattedMessageToDynmap =
            config.getBoolean("sendFormattedMessageToDynmap", false);
        dynmapChannel = config.getString("dynmapChannel", "");
        ngword = config.getStringList("ngword");
        ngwordAction = NGWordAction.fromID(config.getString("ngwordAction", "mask"));

        ngwordCompiled = new ArrayList<Pattern>();
        for ( String word : ngword ) {
            ngwordCompiled.add(Pattern.compile(word));
        }

        enableNormalChatMessageFormat =
                config.getBoolean("enableNormalChatMessageFormat", true);
        normalChatMessageFormat =
                config.getString("normalChatMessageFormat", "&f%prefix%username%suffix&a:&f %msg");
        enableNormalChatColorCode =
                config.getBoolean("enableNormalChatColorCode", true);
        enableNormalChatClickable =
                config.getBoolean("enableNormalChatClickable", false);
        displayNormalChatOnConsole =
                config.getBoolean("displayNormalChatOnConsole", true);

        japanizeType = JapanizeType.fromID(config.getString("japanizeType"), null);
        japanizeDisplayLine = config.getInt("japanizeDisplayLine", 2);
        if ( japanizeDisplayLine != 1 && japanizeDisplayLine != 2 ) {
            japanizeDisplayLine = 2;
        }
        japanizeLine1Format = config.getString("japanizeLine1Format", "%msg &6(%japanize)");
        japanizeLine2Format = config.getString("japanizeLine2Format", "&6[JP] %japanize");
        japanizeIgnorePlayerName = config.getBoolean("japanizeIgnorePlayerName", true);
        noneJapanizeMarker = config.getString("noneJapanizeMarker", "$");
        japanizeWait = config.getInt("japanizeWait", 1);

        bungeePassThroughMode = config.getBoolean("bungeePassThroughMode", false);

        // globalチャンネルが、使用可能なチャンネル名かどうかを調べる
        if ( globalChannel != null && !globalChannel.equals("") &&
                !globalChannel.matches("[0-9a-zA-Z\\-_]{1,20}") ) {

            // コンソールに警告を表示する
            LunaChat.getPlugin().log(Level.WARNING, Messages.errmsgCannotUseForGlobal(globalChannel));
            globalChannel = "";
        }
    }

    /**
     * メッセージの言語
     * @return lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * チャンネルチャット機能を利用可能にするかどうか
     * @return enableChannelChatを返す
     */
    public boolean isEnableChannelChat() {
        return enableChannelChat;
    }

    /**
     * チャットイベントの処理優先度
     * @return playerChatEventListenerPriorityを返す
     */
    public EventPriority getPlayerChatEventListenerPriority() {
        return playerChatEventListenerPriority;
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
     * チャンネルを作成したときに、デフォルトで設定されるフォーマット。
     * @return defaultFormatを返す
     */
    public String getDefaultFormat() {
        return defaultFormat;
    }

    /**
     * プライベートメッセージを送信するときに、適用されるフォーマット。
     * @return defaultFormatForPrivateMessageを返す
     */
    public String getDefaultFormatForPrivateMessage() {
        return defaultFormatForPrivateMessage;
    }

    /**
     * OPの画面に、全チャンネルの発言内容を表示するかどうか
     * @return opListenAllChannel opListenAllChannelを返す
     */
    public boolean isOpListenAllChannel() {
        return opListenAllChannel;
    }

    /**
     * チャンネルを新規作成するときに、チャンネル名が満たさなければならない、最低文字列長
     * @return minChannelNameLength
     */
    public int getMinChannelNameLength() {
        return minChannelNameLength;
    }

    /**
     * チャンネルを新規作成するときに、チャンネル名が満たさなければならない、最大文字列長
     * @return maxChannelNameLength
     */
    public int getMaxChannelNameLength() {
        return maxChannelNameLength;
    }

    /**
     * クイックチャンネルチャット機能を有効化するかどうかを取得する
     * @return enableQuickChannelChat
     */
    public boolean isEnableQuickChannelChat() {
        return enableQuickChannelChat;
    }

    /**
     * クイックチャンネルチャット機能に使用する記号
     * @return quickChannelChatSeparator
     */
    public String getQuickChannelChatSeparator() {
        return quickChannelChatSeparator;
    }

    /**
     * NGワード
     * @return ngwordを返す
     * @deprecated 全て正規表現に変更するため、getNgwordCompiledを使用してください
     */
    @Deprecated
    public List<String> getNgword() {
        return ngword;
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
     * コンパイルされたNGワード
     * @return ngwordCompiledを返す
     */
    public List<Pattern> getNgwordCompiled() {
        return ngwordCompiled;
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
     *  googleime = カナ変換後、GoogleIMEで漢字変換する
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

    /**
     * 通常チャットで、カラーコードを使用可能にするかどうか
     * @return enableNormalChatColorCodeを返す
     */
    public boolean isEnableNormalChatColorCode() {
        return enableNormalChatColorCode;
    }

    /**
     * 通常チャットを、クリック可能にするかどうか
     * @return enableNormalChatClickable
     */
    public boolean isEnableNormalChatClickable() {
        return enableNormalChatClickable;
    }

    /**
     * 通常チャットを、コンソールに表示するかどうか
     * @return displayNormalChatOnConsole
     */
    public boolean isDisplayNormalChatOnConsole() {
        return displayNormalChatOnConsole;
    }

    /**
     * Bungeeパススルーモードかどうかを返す
     * @return bungeePassThroughMode
     */
    public boolean isBungeePassThroughMode() {
        return bungeePassThroughMode;
    }

    /**
     * 指定された文字列から、対応するEventPriorityを返す。
     * @param value 文字列
     * @param def デフォルト
     * @return EventPriority
     */
    private static EventPriority getEventPriority(String value, EventPriority def) {

        if ( value == null ) return def;

        if ( value.equalsIgnoreCase("LOWEST") ) {
            return EventPriority.LOWEST;
        } else if ( value.equalsIgnoreCase("LOW") ) {
            return EventPriority.LOW;
        } else if ( value.equalsIgnoreCase("NORMAL") ) {
            return EventPriority.NORMAL;
        } else if ( value.equalsIgnoreCase("HIGH") ) {
            return EventPriority.HIGH;
        } else if ( value.equalsIgnoreCase("HIGHEST") ) {
            return EventPriority.HIGHEST;
        }

        return def;
    }
}
