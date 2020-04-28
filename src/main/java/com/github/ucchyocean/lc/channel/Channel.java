/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.channel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.event.LunaChatChannelMemberChangedEvent;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * チャンネル
 * @author ucchy
 */
@SerializableAs("Channel")
public abstract class Channel implements ConfigurationSerializable {

    private static final String FOLDER_NAME_CHANNELS = "channels";

    private static final String KEY_NAME = "name";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_DESC = "desc";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_BANNED = "banned";
    private static final String KEY_MUTED = "muted";
    private static final String KEY_HIDED = "hided";
    private static final String KEY_MODERATOR = "moderator";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_VISIBLE = "visible";
    private static final String KEY_COLOR = "color";
    private static final String KEY_BROADCAST = "broadcast";
    private static final String KEY_WORLD = "world";
    private static final String KEY_RANGE = "range";
    private static final String KEY_BAN_EXPIRES = "ban_expires";
    private static final String KEY_MUTE_EXPIRES = "mute_expires";
    private static final String KEY_ALLOWCC = "allowcc";
    private static final String KEY_JAPANIZE = "japanize";

    /** 参加者 */
    private List<ChannelPlayer> members;

    /** チャンネルモデレータ */
    private List<ChannelPlayer> moderator;

    /** BANされたプレイヤー */
    private List<ChannelPlayer> banned;

    /** Muteされたプレイヤー */
    private List<ChannelPlayer> muted;

    /** Hideしているプレイヤー */
    private List<ChannelPlayer> hided;

    /** チャンネルの名称 */
    private String name;

    /** チャンネルの別名 */
    private String alias;

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

    /** ブロードキャストチャンネルかどうか */
    private boolean broadcastChannel;

    /** ワールドチャットかどうか */
    private boolean isWorldRange;

    /** チャットの可聴範囲 0は無制限 */
    private int chatRange;

    /** 期限付きBANの期限（key=プレイヤー名、value=期日（ミリ秒）） */
    private Map<ChannelPlayer, Long> banExpires;

    /** 期限付きMuteの期限（key=プレイヤー名、value=期日（ミリ秒）） */
    private Map<ChannelPlayer, Long> muteExpires;

    /** 1:1チャットの相手名 */
    private String privateMessageTo;

    /** カラーコードの使用可否 */
    private boolean allowcc;

    /** チャンネルごとのjapanize変換設定 */
    private JapanizeType japanizeType;

    /**
     * コンストラクタ
     * @param name チャンネルの名称
     */
    protected Channel(String name) {

        this.name = name;
        this.alias = "";
        this.description = "";
        this.members = new ArrayList<ChannelPlayer>();
        this.banned = new ArrayList<ChannelPlayer>();
        this.muted = new ArrayList<ChannelPlayer>();
        this.hided = new ArrayList<ChannelPlayer>();
        this.moderator = new ArrayList<ChannelPlayer>();
        this.password = "";
        this.visible = true;
        this.colorCode = "";
        this.broadcastChannel = false;
        this.isWorldRange = false;
        this.chatRange = 0;
        this.banExpires = new HashMap<ChannelPlayer, Long>();
        this.muteExpires = new HashMap<ChannelPlayer, Long>();
        this.privateMessageTo = "";
        this.allowcc = true;
        this.japanizeType = null;

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        if ( isPersonalChat() ) {
            this.format = config.getDefaultFormatForPrivateMessage();
        } else {
            this.format = config.getDefaultFormat();
        }
    }

    /**
     * 1:1チャットかどうか
     * @return 1:1チャットかどうか
     */
    public boolean isPersonalChat() {
        return name.contains(">");
    }

    /**
     * ブロードキャストチャンネルかどうか
     * @return ブロードキャストチャンネルかどうか
     */
    public boolean isBroadcastChannel() {
        return (isGlobalChannel() || broadcastChannel);
    }

    /**
     * グローバルチャンネルかどうか
     * @return グローバルチャンネルかどうか
     */
    public boolean isGlobalChannel() {
        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        return getName().equals(config.getGlobalChannel());
    }

    /**
     * 強制参加チャンネルかどうか
     * @return 強制参加チャンネルかどうか
     */
    public boolean isForceJoinChannel() {
        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        return config.getForceJoinChannels().contains(getName());
    }

    /**
     * このチャンネルのモデレータ権限を持っているかどうかを確認する
     * @param sender 権限を確認する対象
     * @return チャンネルのモデレータ権限を持っているかどうか
     */
    public boolean hasModeratorPermission(CommandSender sender) {
        if (sender.isOp() ||
                sender.hasPermission("lunachat-admin.mod-all-channels")) {
            return true;
        }
        ChannelPlayer player = ChannelPlayer.getChannelPlayer(sender);
        return moderator.contains(player);
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    public abstract void chat(ChannelPlayer player, String message);

    /**
     * ほかの連携先などから、このチャットに発言する
     * @param player プレイヤー名
     * @param source 連携元を判別する文字列
     * @param message メッセージ
     */
    public abstract void chatFromOtherSource(String player, String source, String message);

    /**
     * メンバーを追加する
     * @param name 追加するプレイヤー
     */
    public void addMember(ChannelPlayer player) {

        // 既に参加しているなら、何もしない
        if ( members.contains(player) ) {
            return;
        }

        // 変更後のメンバーリストを作成
        ArrayList<ChannelPlayer> after = new ArrayList<ChannelPlayer>(members);
        after.add(player);

        // イベントコール
        LunaChatChannelMemberChangedEvent event =
                new LunaChatChannelMemberChangedEvent(this.name, this.members, after);
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }

        // メンバー更新
        if ( members.size() == 0 && moderator.size() == 0 ) {
            moderator.add(player);
        }
        members = after;

        sendSystemMessage("joinMessage", player);

        save();
    }

    /**
     * メンバーを削除する
     * @param name 削除するプレイヤー
     */
    public void removeMember(ChannelPlayer player) {

        // 既に削除しているなら、何もしない
        if ( !members.contains(player) ) {
            return;
        }

        // 変更後のメンバーリストを作成
        ArrayList<ChannelPlayer> after = new ArrayList<ChannelPlayer>(members);
        after.remove(player);

        // イベントコール
        LunaChatChannelMemberChangedEvent event =
                new LunaChatChannelMemberChangedEvent(this.name, this.members, after);
        Bukkit.getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return;
        }

        // デフォルト発言先が退出するチャンネルと一致する場合、
        // デフォルト発言先を削除する
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        Channel def = api.getDefaultChannel(player.getName());
        if ( def != null && def.getName().equals(getName()) ) {
            api.removeDefaultChannel(player.getName());
        }

        // 実際にメンバーから削除する
        members.remove(player);

        sendSystemMessage("quitMessage", player);

        // 0人で削除する設定がオンで、0人になったなら、チャンネルを削除する
        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        if ( config.isZeroMemberRemove() && members.size() <= 0 ) {
            api.removeChannel(this.name);
            return;
        }

        // 非表示設定プレイヤーだったら、リストから削除する
        if ( hided.contains(player) ) {
            hided.remove(player);
        }

        // モデレーターだった場合は、モデレーターから除去する
        if ( moderator.contains(player) ) {
            moderator.remove(player);
        }

        save();
    }

    /**
     * モデレータを追加する
     * @param player 追加するプレイヤー
     */
    public void addModerator(ChannelPlayer player) {

        // 既にモデレータなら何もしない
        if ( moderator.contains(player) ) {
            return;
        }

        // モデレータへ追加
        moderator.add(player);

        // メッセージ
        sendSystemMessage("addModeratorMessage", player);

        save();
    }

    /**
     * モデレータを削除する
     * @param player 削除するプレイヤー
     */
    public void removeModerator(ChannelPlayer player) {

        // 既にモデレータでないなら何もしない
        if ( !moderator.contains(player) ) {
            return;
        }

        // モデレータから削除
        moderator.remove(player);

        // メッセージ
        sendSystemMessage("removeModeratorMessage", player);

        save();
    }

    /**
     * プレイヤーに関連する、システムメッセージをチャンネルに流す
     * @param key リソースキー
     * @param player プレイヤー
     */
    protected abstract void sendSystemMessage(String key, ChannelPlayer player);

    /**
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param player プレイヤー（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     * @param displayName 発言者の表示名（APIに使用されます）
     */
    public abstract void sendMessage(
            ChannelPlayer player, String message, String format, boolean sendDynmap, String displayName);

    /**
     * チャンネル情報を返す
     * @param forModerator モデレータ向けの情報を含めるかどうか
     * @return チャンネル情報
     */
    public abstract ArrayList<String> getInfo(boolean forModerator);

    /**
     * ログファイルを読み込んで、ログデータを取得する
     * @param player プレイヤー名、フィルタしないならnullを指定すること
     * @param filter フィルタ、フィルタしないならnullを指定すること
     * @param date 日付、今日のデータを取得するならnullを指定すること
     * @param reverse 逆順取得
     * @return ログデータ
     */
    public abstract ArrayList<String> getLog(
            String player, String filter, String date, boolean reverse);

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     */
    public int getOnlineNum() {

        // ブロードキャストチャンネルならサーバー接続人数を返す
        if ( isBroadcastChannel() ) {
            return Utility.getOnlinePlayersCount();
        }

        // メンバーの人数を数える
        int onlineNum = 0;
        for ( ChannelPlayer player : members ) {
            if ( player.isOnline() ) {
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

        // ブロードキャストチャンネルならサーバー接続人数を返す
        if ( isBroadcastChannel() ) {
            return Utility.getOnlinePlayersCount();
        }

        return members.size();
    }

    /**
     * 期限付きBanや期限付きMuteをチェックし、期限が切れていたら解除を行う
     */
    public abstract void checkExpires();

    /**
     * シリアライズ<br>
     * ConfigurationSerializable互換のための実装。
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
     */
    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_NAME, name);
        map.put(KEY_ALIAS, alias);
        map.put(KEY_DESC, description);
        map.put(KEY_FORMAT, format);
        map.put(KEY_MEMBERS, getStringList(members));
        map.put(KEY_BANNED, getStringList(banned));
        map.put(KEY_MUTED, getStringList(muted));
        map.put(KEY_HIDED, getStringList(hided));
        map.put(KEY_MODERATOR, getStringList(moderator));
        map.put(KEY_PASSWORD, password);
        map.put(KEY_VISIBLE, visible);
        map.put(KEY_COLOR, colorCode);
        map.put(KEY_BROADCAST, broadcastChannel);
        map.put(KEY_WORLD, isWorldRange);
        map.put(KEY_RANGE, chatRange);
        map.put(KEY_BAN_EXPIRES, getStringLongMap(banExpires));
        map.put(KEY_MUTE_EXPIRES, getStringLongMap(muteExpires));
        map.put(KEY_ALLOWCC, allowcc);
        map.put(KEY_JAPANIZE, japanizeType == null ? null : japanizeType.toString());
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
        if ( name == null ) {
            return null;
        }

        Channel channel = new ChannelImpl(name);
        channel.alias = castWithDefault(data.get(KEY_ALIAS), "");
        channel.description = castWithDefault(data.get(KEY_DESC), "");
        channel.format = castWithDefault(data.get(KEY_FORMAT), channel.format);
        channel.members = castToChannelPlayerList(data.get(KEY_MEMBERS));
        channel.banned = castToChannelPlayerList(data.get(KEY_BANNED));
        channel.muted = castToChannelPlayerList(data.get(KEY_MUTED));
        channel.hided = castToChannelPlayerList(data.get(KEY_HIDED));
        channel.moderator = castToChannelPlayerList(data.get(KEY_MODERATOR));
        channel.password = castWithDefault(data.get(KEY_PASSWORD), "");
        channel.visible = castWithDefault(data.get(KEY_VISIBLE), true);
        channel.colorCode = castWithDefault(data.get(KEY_COLOR), "");
        channel.broadcastChannel = castWithDefault(data.get(KEY_BROADCAST), false);
        channel.isWorldRange = castWithDefault(data.get(KEY_WORLD), false);
        channel.chatRange = castWithDefault(data.get(KEY_RANGE), 0);
        channel.banExpires = castToChannelPlayerLongMap(data.get(KEY_BAN_EXPIRES));
        channel.muteExpires = castToChannelPlayerLongMap(data.get(KEY_MUTE_EXPIRES));
        channel.allowcc = castWithDefault(data.get(KEY_ALLOWCC), true);
        channel.japanizeType = JapanizeType.fromID(data.get(KEY_JAPANIZE) + "", null);
        return channel;
    }

    /**
     * List&lt;ChannelPlayer&gt;を、List&lt;String&gt;に変換する。
     * @param org 変換元
     * @return 変換後
     */
    private static List<String> getStringList(List<ChannelPlayer> org) {

        ArrayList<String> result = new ArrayList<String>();
        for ( ChannelPlayer cp : org ) {
            result.add(cp.toString());
        }
        return result;
    }

    /**
     * Map&lt;ChannelPlayer, Long&gt;を、Map&lt;String, Long&gt;に変換する。
     * @param org 変換元
     * @return 変換後
     */
    private static Map<String, Long> getStringLongMap(Map<ChannelPlayer, Long> org) {

        HashMap<String, Long> result = new HashMap<String, Long>();
        for ( ChannelPlayer cp : org.keySet() ) {
            result.put(cp.toString(), org.get(cp));
        }
        return result;
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
     * Objectを、List&lt;ChannelPlayer&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    private static List<ChannelPlayer> castToChannelPlayerList(Object obj) {

        List<String> entries = castToStringList(obj);
        ArrayList<ChannelPlayer> players = new ArrayList<ChannelPlayer>();

        for ( String entry : entries ) {
            players.add(ChannelPlayer.getChannelPlayer(entry));
        }

        return players;
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
     * Objectを、Map&lt;ChannelPlayer, Long&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    private static Map<ChannelPlayer, Long> castToChannelPlayerLongMap(Object obj) {

        Map<String, Long> entries = castToStringLongMap(obj);
        HashMap<ChannelPlayer, Long> map = new HashMap<ChannelPlayer, Long>();

        for ( String key : entries.keySet() ) {
            ChannelPlayer cp = ChannelPlayer.getChannelPlayer(key);
            map.put(cp, entries.get(key));
        }

        return map;
    }

    /**
     * Objectを、Map&lt;String, Long&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Long> castToStringLongMap(Object obj) {

        if ( obj == null ) {
            return new HashMap<String, Long>();
        }
        if ( !(obj instanceof HashMap<?, ?>) ) {
            return new HashMap<String, Long>();
        }
        return (Map<String, Long>)obj;
    }

    /**
     * チャンネルの設定ファイルが、UUID化のために一旦保存が必要かどうかを返す
     * @param data チャンネルの実コンフィグデータ
     */
    private static boolean isNeedToSaveForUUIDUpdate(Map<String, Object> data) {

        if ( !Utility.isCB178orLater() ) {
            return false;
        }

        List<String> members = castToStringList(data.get(KEY_MEMBERS));
        if ( members.size() == 0 ) {
            return false;
        }

        for ( String member : members ) {
            if ( member.startsWith("$") ) {
                return false;
            }
        }

        return true;
    }

    /**
     * チャンネルの別名を返す
     * @return チャンネルの別名
     */
    public String getAlias() {
        return alias;
    }

    /**
     * チャンネルの別名を設定する
     * @param alias チャンネルの別名
     */
    public void setAlias(String alias) {
        this.alias = alias;
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
    public List<ChannelPlayer> getMembers() {

        // ブロードキャストチャンネルなら、
        // 現在サーバーに接続している全プレイヤーをメンバーとして返す
        if ( isBroadcastChannel() ) {
            List<ChannelPlayer> mem = new ArrayList<ChannelPlayer>();
            for ( Player p : Utility.getOnlinePlayers() ) {
                mem.add(ChannelPlayer.getChannelPlayer(p));
            }
            return mem;
        }

        return members;
    }

    /**
     * チャンネルのモデレーターを返す
     * @return チャンネルのモデレーター
     */
    public List<ChannelPlayer> getModerator() {
        return moderator;
    }

    /**
     * チャンネルのBANリストを返す
     * @return チャンネルのBANリスト
     */
    public List<ChannelPlayer> getBanned() {
        return banned;
    }

    /**
     * チャンネルのMuteリストを返す
     * @return チャンネルのMuteリスト
     */
    public List<ChannelPlayer> getMuted() {
        return muted;
    }

    /**
     * 期限付きBANの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return banExpires
     */
    public Map<ChannelPlayer, Long> getBanExpires() {
        return banExpires;
    }

    /**
     * 期限付きMuteの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return muteExpires
     */
    public Map<ChannelPlayer, Long> getMuteExpires() {
        return muteExpires;
    }

    /**
     * 非表示プレイヤーの一覧を返す
     * @return チャンネルの非表示プレイヤーの一覧
     */
    public List<ChannelPlayer> getHided() {
        return hided;
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
     * ブロードキャストチャンネルを設定する
     * @param broadcast ブロードキャストチャンネルにするかどうか
     */
    public void setBroadcast(boolean broadcast) {
        this.broadcastChannel = broadcast;
    }

    /**
     * チャットを同ワールド内に制限するかどうかを設定する
     * @param isWorldRange 同ワールド制限するかどうか
     */
    public void setWorldRange(boolean isWorldRange) {
        this.isWorldRange = isWorldRange;
    }

    /**
     * チャットの可聴範囲を設定する
     * @param range 可聴範囲
     */
    public void setChatRange(int range) {
        this.chatRange = range;
    }

    /**
     * 1:1チャットのときに、会話の相手先を取得する
     * @return 会話の相手のプレイヤー名
     */
    public String getPrivateMessageTo() {
        return privateMessageTo;
    }

    /**
     * 1:1チャットのときに、会話の相手先を設定する
     * @param name 会話の相手のプレイヤー名
     */
    public void setPrivateMessageTo(String name) {
        this.privateMessageTo = name;
    }

    /**
     * ワールドチャットかどうか
     * @return ワールドチャットかどうか
     */
    public boolean isWorldRange() {
        return isWorldRange;
    }

    /**
     * チャットの可聴範囲、0の場合は無制限
     * @return チャットの可聴範囲
     */
    public int getChatRange() {
        return chatRange;
    }

    /**
     * カラーコードが使用可能な設定かどうか
     * @return allowccを返す
     */
    public boolean isAllowCC() {
        return allowcc;
    }

    /**
     * カラーコードの使用可否を設定する
     * @param allowcc 使用可否
     */
    public void setAllowCC(boolean allowcc) {
        this.allowcc = allowcc;
    }

    /**
     * Japanize変換設定を取得する
     * @return japanize
     */
    public JapanizeType getJapanizeType() {
        return japanizeType;
    }

    /**
     * Japanize変換設定を再設定する
     * @param japanize japanize
     */
    public void setJapanizeType(JapanizeType japanize) {
        this.japanizeType = japanize;
    }

    /**
     * チャンネルの情報をファイルに保存する。
     * @return 保存をしたかどうか。
     */
    public boolean save() {

        // フォルダーの取得と、必要に応じて作成
        File folder = new File(
                LunaChat.getInstance().getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        // 1:1チャットチャンネルの場合は、何もしない。
        if ( isPersonalChat() ) {
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
    protected boolean remove() {

        // フォルダーの取得
        File folder = new File(
                LunaChat.getInstance().getDataFolder(), FOLDER_NAME_CHANNELS);
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
                LunaChat.getInstance().getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            return new HashMap<String, Channel>();
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });
        if ( files == null ) files = new File[0];

        HashMap<String, Channel> result = new HashMap<String, Channel>();
        for ( File file : files ) {
            YamlConfiguration config =
                YamlConfiguration.loadConfiguration(file);
            Map<String, Object> data = new HashMap<String, Object>();
            for ( String key : config.getKeys(false) ) {
                data.put(key, config.get(key));
            }
            Channel channel = deserialize(data);

            // 自動アップデート
            if ( isNeedToSaveForUUIDUpdate(data) ) {
                channel.save();
            }

            result.put(channel.name.toLowerCase(), channel);
        }

        return result;
    }
}
