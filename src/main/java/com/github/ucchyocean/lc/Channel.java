/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelMemberChangedEvent;
import com.github.ucchyocean.lc.japanize.ConvertTask;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * @author ucchy
 * チャンネル
 */
@SerializableAs("Channel")
public class Channel implements ConfigurationSerializable {

    private static final String FOLDER_NAME_CHANNELS = "channels";

    private static final String INFO_FIRSTLINE = Resources.get("channelInfoFirstLine");
    private static final String INFO_PREFIX = Resources.get("channelInfoPrefix");
    private static final String INFO_GLOBAL = Resources.get("channelInfoGlobal");

    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String DEFAULT_FORMAT = Resources.get("defaultFormat");
    private static final String MSG_JOIN = Resources.get("joinMessage");
    private static final String MSG_QUIT = Resources.get("quitMessage");

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String NGWORD_PREFIX = Resources.get("ngwordPrefix");
    private static final String MSG_KICKED = Resources.get("cmdmsgKicked");
    private static final String MSG_BANNED = Resources.get("cmdmsgBanned");

    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "desc";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_BANNED = "banned";
    private static final String KEY_MODERATOR = "moderator";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_VISIBLE = "visible";
    private static final String KEY_COLOR = "color";

    /** 参加者 */
    private List<String> members;

    /** チャンネルモデレータ */
    private List<String> moderator;

    /** BANされたプレイヤー */
    private List<String> banned;

    /** チャンネルの名称 */
    private String name;

    /** チャンネルの説明文 */
    private String description;

    /** チャンネルのパスワード */
    private String password;

    /** チャンネルリストに表示されるかどうか */
    private boolean visible;

    /** チャンネルのカラー */
    private String colorCode;

    /** メッセージフォーマット<br>
     * 指定可能なキーワードは下記のとおり<br>
     * %ch - チャンネル名<br>
     * %username - ユーザー名<br>
     * %msg - メッセージ<br>
     * %prefix - PermissionsExに設定するprefix<br>
     * %suffix - PermissionsExに設定するsuffix<br>
     * %color - チャンネルのカラーコード
     * */
    private String format;

    /**
     * コンストラクタ
     * @param name チャンネルの名称
     */
    protected Channel(String name) {
        this.name = name;
        this.description = "";
        this.members = new ArrayList<String>();
        this.format = DEFAULT_FORMAT;
        this.banned = new ArrayList<String>();
        this.moderator = new ArrayList<String>();
        this.password = "";
        this.visible = true;
        this.colorCode = "";
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    public void chat(Player player, String message) {

        String preReplaceMessage = message;

        // NGワード発言をしたかどうかのチェックとマスク
        boolean isNG = false;
        String maskedMessage = message;
        for ( String word : LunaChat.config.ngword ) {
            if ( maskedMessage.contains(word) ) {
                maskedMessage = maskedMessage.replace(
                        word, Utility.getAstariskString(word.length()));
                isNG = true;
            }
        }

        // キーワード置き換え
        String msgFormat = replaceKeywords(format, player);

        // イベントコール
        LunaChatChannelChatEvent event =
                new LunaChatChannelChatEvent(this.name,
                        preReplaceMessage, maskedMessage, msgFormat);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }
        msgFormat = event.getMessageFormat();

        // Japanize変換と、発言処理
        boolean chated = false;
        if ( LunaChat.config.getJapanizeType() != JapanizeType.NONE ) {
            // 2byteコードを含まない場合にのみ、処理を行う
            if ( message.getBytes().length == message.length() ) {

                int lineType = LunaChat.config.japanizeDisplayLine;
                String lineFormat;
                String taskFormat;
                if ( lineType == 1 ) {
                    lineFormat = LunaChat.config.japanizeLine1Format;
                    taskFormat = msgFormat.replace("%msg", lineFormat);
                    chated = true;
                } else {
                    lineFormat = LunaChat.config.japanizeLine2Format;
                    taskFormat = lineFormat;
                }

                // タスクを作成して実行する
                // 発言処理は、タスクが完了しだい非同期で行われる
                ConvertTask task = new ConvertTask(maskedMessage,
                        LunaChat.config.getJapanizeType(),
                        this, taskFormat);
                Bukkit.getScheduler().runTask(LunaChat.instance, task);
            }
        }

        if ( !chated ) {

            // オンラインのプレイヤーに送信する
            String msg = msgFormat.replace("%msg", maskedMessage);
            sendInformation(msg);
        }

        // NGワード発言者に、NGワードアクションを実行する
        if ( isNG && player != null ) {
            if ( LunaChat.config.ngwordAction == NGWordAction.BAN ) {
                // BANする

                banned.add(player.getName());
                removeMember(player.getName());
                String temp = PREINFO + NGWORD_PREFIX + MSG_BANNED;
                String m = String.format(
                        Utility.replaceColorCode(temp), name);
                player.sendMessage(m);

            } else if ( LunaChat.config.ngwordAction == NGWordAction.KICK ) {
                // キックする

                removeMember(player.getName());
                String temp = PREINFO + NGWORD_PREFIX + MSG_KICKED;
                String m = String.format(
                        Utility.replaceColorCode(temp), name);
                player.sendMessage(m);
            }
        }
    }

    /**
     * メンバーを追加する
     * @param name 追加するメンバー名
     */
    public void addMember(String name) {

        // イベントコール
        LunaChatChannelMemberChangedEvent event =
                new LunaChatChannelMemberChangedEvent(this.name, this.members);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }

        // メンバー追加
        if ( members.size() == 0 ) {
            moderator.add(name);
        }
        if ( !members.contains(name) ) {
            members.add(name);
            sendJoinQuitMessage(true, name);
            save();
        }
    }

    /**
     * メンバーを削除する
     * @param name 削除するメンバー名
     */
    public void removeMember(String name) {

        // イベントコール
        LunaChatChannelMemberChangedEvent event =
                new LunaChatChannelMemberChangedEvent(this.name, this.members);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }

        // デフォルト発言先が退出するチャンネルと一致する場合、
        // デフォルト発言先を削除する
        Channel def = LunaChat.manager.getDefaultChannel(name);
        if ( def != null && def.name.equals(this.name) ) {
            LunaChat.manager.removeDefaultChannel(name);
        }

        // 実際にメンバーから削除する
        if ( members.contains(name) ) {
            members.remove(name);
            sendJoinQuitMessage(false, name);
            if ( LunaChat.config.zeroMemberRemove && members.size() <= 0 ) {
                LunaChat.manager.removeChannel(this.name);
                return;
            } else {
                save();
            }
        }

        // モデレーターだった場合は、モデレーターから除去する
        if ( moderator.contains(name) ) {
            moderator.remove(name);
        }
    }

    /**
     * 入退室メッセージを流す
     * @param isJoin 入室かどうか（falseなら退室）
     * @param player 入退室したプレイヤー名
     */
    private void sendJoinQuitMessage(boolean isJoin, String player) {

        String msg;
        if ( isJoin ) {
            msg = MSG_JOIN;
        } else {
            msg = MSG_QUIT;
        }

        // キーワード置き換え
        Player p = LunaChat.getPlayerExact(player);
        msg = replaceKeywords(msg, p, "");

        sendInformation(msg);
    }

    /**
     * 情報をチャンネルメンバーに流します。
     * @param message メッセージ
     */
    public void sendInformation(String message) {

        // グローバルチャンネルは、そのままブロードキャスト
        if ( name.equals(LunaChat.config.globalChannel) ) {
            Bukkit.broadcastMessage(message);
            return;
        }

        // オンラインのプレイヤーに送信する
        for ( String member : members ) {
            Player p = LunaChat.getPlayerExact(member);
            if ( p != null ) {
                p.sendMessage(message);
            }
        }

        // ロギング
        if ( LunaChat.config.loggingChat ) {
            LunaChat.log(message);
        }
    }

    /**
     * チャンネル情報を返す
     * @return チャンネル情報
     */
    protected ArrayList<String> getInfo() {

        ArrayList<String> info = new ArrayList<String>();
        info.add(Utility.replaceColorCode(INFO_FIRSTLINE));

        info.add( String.format(
                Utility.replaceColorCode(LIST_FORMAT),
                name, getOnlineNum(), getTotalNum(), description) );

        if ( !name.equals(LunaChat.config.globalChannel) ) {
            // メンバーを、5人ごとに表示する
            StringBuffer buf = new StringBuffer();
            for ( int i=0; i<members.size(); i++ ) {

                if ( i%5 == 0 ) {
                    if ( i != 0 ) {
                        info.add(buf.toString());
                        buf = new StringBuffer();
                    }
                    buf.append(Utility.replaceColorCode(INFO_PREFIX));
                }

                String name = members.get(i);
                String disp;
                if ( moderator.contains(name) ) {
                    name = "@" + name;
                }
                if ( isOnlinePlayer(members.get(i)) ) {
                    disp = ChatColor.WHITE + name;
                } else {
                    disp = ChatColor.GRAY + name;
                }
                buf.append(disp + ",");
            }

            info.add(buf.toString());

        } else {

            info.add(Utility.replaceColorCode(INFO_GLOBAL));
        }

        info.add(Utility.replaceColorCode(LIST_ENDLINE));

        return info;
    }

    /**
     * 指定された名前のプレイヤーがオンラインかどうかを確認する
     * @param playerName プレイヤー名
     * @return オンラインかどうか
     */
    private boolean isOnlinePlayer(String playerName) {
        Player p = LunaChat.getPlayerExact(playerName);
        return ( p != null && p.isOnline() );
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param player プレイヤー
     * @param message プレイヤーの発言内容
     * @return 置き換え結果
     */
    private String replaceKeywords(String format, Player player, String message) {

        String msg = format;

        // テンプレートのキーワードを、まず最初に置き換える
        for ( int i=0; i<=9; i++ ) {
            String key = "%" + i;
            if ( msg.contains(key) ) {
                if ( LunaChat.manager.getTemplate("" + i) != null ) {
                    msg = msg.replace(key, LunaChat.manager.getTemplate("" + i));
                    break;
                }
            }
        }

        msg = msg.replace("%ch", name);
        msg = msg.replace("%msg", message);
        msg = msg.replace("%color", colorCode);

        if ( player != null ) {
            msg = msg.replace("%username", player.getDisplayName());

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                String prefix = "";
                String suffix = "";
                if ( LunaChat.chatPlugin != null ) {
                    prefix = LunaChat.chatPlugin.getPlayerPrefix(player);
                    suffix = LunaChat.chatPlugin.getPlayerSuffix(player);
                }
                msg = msg.replace("%prefix", prefix);
                msg = msg.replace("%suffix", suffix);
            }
        }

        return Utility.replaceColorCode(msg);
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param player プレイヤー
     * @return 置き換え結果
     */
    private String replaceKeywords(String format, Player player) {

        String msg = format;

        // テンプレートのキーワードを、まず最初に置き換える
        for ( int i=0; i<=9; i++ ) {
            String key = "%" + i;
            if ( msg.contains(key) ) {
                if ( LunaChat.manager.getTemplate("" + i) != null ) {
                    msg = msg.replace(key, LunaChat.manager.getTemplate("" + i));
                    break;
                }
            }
        }

        msg = msg.replace("%ch", name);
        //msg = msg.replace("%msg", message);
        msg = msg.replace("%color", colorCode);

        if ( player != null ) {
            msg = msg.replace("%username", player.getDisplayName());

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                String prefix = "";
                String suffix = "";
                if ( LunaChat.chatPlugin != null ) {
                    prefix = LunaChat.chatPlugin.getPlayerPrefix(player);
                    suffix = LunaChat.chatPlugin.getPlayerSuffix(player);
                }
                msg = msg.replace("%prefix", prefix);
                msg = msg.replace("%suffix", suffix);
            }
        }

        return Utility.replaceColorCode(msg);
    }

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     */
    public int getOnlineNum() {

        // グローバルチャンネルならサーバー接続人数を返す
        if ( name.equals(LunaChat.config.globalChannel) ) {
            return Bukkit.getOnlinePlayers().length;
        }

        // メンバーの人数を数える
        int onlineNum = 0;
        for ( String pname : members ) {
            if ( isOnlinePlayer(pname) ) {
                onlineNum++;
            }
        }
        return onlineNum;
    }

    /**
     * チャンネルの総参加人数を返す
     * @return 総参加人数
     */
    public int getTotalNum() {

        // グローバルチャンネルならサーバー接続人数を返す
        if ( name.equals(LunaChat.config.globalChannel) ) {
            return Bukkit.getOnlinePlayers().length;
        }

        return members.size();
    }

    /**
     * シリアライズ<br>
     * ConfigurationSerializable互換のための実装。
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
     */
    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_NAME, name);
        map.put(KEY_DESC, description);
        map.put(KEY_FORMAT, format);
        map.put(KEY_MEMBERS, members);
        map.put(KEY_BANNED, banned);
        map.put(KEY_MODERATOR, moderator);
        map.put(KEY_PASSWORD, password);
        map.put(KEY_VISIBLE, visible);
        map.put(KEY_COLOR, colorCode);
        return map;
    }

    /**
     * デシリアライズ<br>
     * ConfigurationSerializable互換のための実装。
     * @param data デシリアライズ元のMapデータ。
     * @return デシリアライズされたクラス
     */
    public static Channel deserialize(Map<String, Object> data) {

        String name = castWithDefault(data.get(KEY_NAME), (String)null);
        name = name.toLowerCase();
        List<String> members = castToStringList(data.get(KEY_MEMBERS));

        Channel channel = new Channel(name);
        channel.members = members;
        channel.description = castWithDefault(data.get(KEY_DESC), "");
        channel.format =
            castWithDefault(data.get(KEY_FORMAT), DEFAULT_FORMAT);
        channel.banned = castToStringList(data.get(KEY_BANNED));
        channel.moderator = castToStringList(data.get(KEY_MODERATOR));
        channel.password = castWithDefault(data.get(KEY_PASSWORD), "");
        channel.visible = castWithDefault(data.get(KEY_VISIBLE), true);
        channel.colorCode = castWithDefault(data.get(KEY_COLOR), "");
        return channel;
    }

    /**
     * Objectを、クラスTに変換する。nullならデフォルトを返す。
     * @param obj 変換元
     * @param def nullだった場合のデフォルト
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static <T> T castWithDefault(Object obj, T def) {

        if ( obj == null ) {
            return def;
        }
        return (T)obj;
    }

    /**
     * Objectを、List&lt;String&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static List<String> castToStringList(Object obj) {

        if ( obj == null ) {
            return new ArrayList<String>();
        }
        if ( !(obj instanceof List<?>) ) {
            return new ArrayList<String>();
        }
        return (List<String>)obj;
    }

    /**
     * チャンネルの説明文を返す
     * @return チャンネルの説明文
     */
    public String getDescription() {
        return description;
    }

    /**
     * チャンネルの説明文を設定する
     * @param description チャンネルの説明文
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * チャンネルのパスワードを返す
     * @return チャンネルのパスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * チャンネルのパスワードを設定する
     * @param password チャンネルのパスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * チャンネルの可視性を返す
     * @return チャンネルの可視性
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * チャンネルの可視性を設定する
     * @param visible チャンネルの可視性
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * チャンネルのメッセージフォーマットを返す
     * @return チャンネルのメッセージフォーマット
     */
    public String getFormat() {
        return format;
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     * @param format チャンネルのメッセージフォーマット
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * チャンネルのメンバーを返す
     * @return チャンネルのメンバー
     */
    public List<String> getMembers() {
        return members;
    }

    /**
     * チャンネルのモデレーターを返す
     * @return チャンネルのモデレーター
     */
    public List<String> getModerator() {
        return moderator;
    }

    /**
     * チャンネルのBANリストを返す
     * @return チャンネルのBANリスト
     */
    public List<String> getBanned() {
        return banned;
    }

    /**
     * チャンネル名を返す
     * @return チャンネル名
     */
    public String getName() {
        return name;
    }

    /**
     * チャンネルのカラーコードを返す
     * @return チャンネルのカラーコード
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * チャンネルのカラーコードを設定する
     * @param colorCode カラーコード
     */
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * チャンネルの情報をファイルに保存する。
     * @return 保存をしたかどうか。
     */
    public boolean save() {

        // フォルダーの取得と、必要に応じて作成
        File folder = new File(
                LunaChat.instance.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        // 1:1チャットチャンネルの場合は、何もしない。
        if ( name.contains(">") ) {
            return false;
        }

        File file = new File(folder, name + ".yml");

        // ファイルへ保存する
        YamlConfiguration conf = new YamlConfiguration();
        Map<String, Object> data = this.serialize();
        for ( String key : data.keySet() ) {
            conf.set(key, data.get(key));
        }
        try {
            conf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * チャンネルの情報を保存したファイルを、削除する。
     * @return 削除したかどうか。
     */
    public boolean remove() {

        // フォルダーの取得
        File folder = new File(
                LunaChat.instance.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            return false;
        }
        File file = new File(folder, name + ".yml");
        if ( !file.exists() ) {
            return false;
        }

        // ファイルを削除
        return file.delete();
    }

    /**
     * チャンネルの情報を保存したファイルから全てのチャンネルを復元して返す。
     * @return 全てのチャンネル
     */
    protected static HashMap<String, Channel> loadAllChannels() {

        // フォルダーの取得
        File folder = new File(
                LunaChat.instance.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            return new HashMap<String, Channel>();
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });

        HashMap<String, Channel> result = new HashMap<String, Channel>();
        for ( File file : files ) {
            YamlConfiguration config =
                YamlConfiguration.loadConfiguration(file);
            Map<String, Object> data = new HashMap<String, Object>();
            for ( String key : config.getKeys(false) ) {
                data.put(key, config.get(key));
            }
            Channel channel = deserialize(data);
            result.put(channel.name, channel);
        }

        return result;
    }
}
