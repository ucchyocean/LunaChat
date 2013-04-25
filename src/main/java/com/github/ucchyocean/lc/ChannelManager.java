/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author ucchy
 * チャンネルマネージャー
 */
public class ChannelManager {

    private static final String LIST_FIRSTLINE = Resources.get("listFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String MOTD_FIRSTLINE = Resources.get("motdFirstLine");

    private static final String DEFAULT_FORMAT = Resources.get("defaultFormat");

    private static final String FILE_NAME = "channels.yml";

    private static final String KEY_DESC = "desc";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_BANNED = "banned";
    private static final String KEY_MODERATOR = "moderator";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_VISIBLE = "visible";

    private File file;
    private HashMap<String, Channel> channels;
    private HashMap<String, String> defaultChannels;

    /**
     * コンストラクタ
     */
    public ChannelManager() {
        load();
    }

    /**
     * 読み込みする
     */
    private void load() {

        file = new File(
                LunaChat.instance.getDataFolder() +
                File.separator + FILE_NAME);

        if ( !file.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(file);

        // チャンネル設定のロード
        if ( channels == null ) {
            channels = new HashMap<String, Channel>();
        } else {
            channels.clear();
        }

        if ( config.contains("channels") ) {
            ConfigurationSection section = config.getConfigurationSection("channels");
            Set<String> keyset = section.getValues(false).keySet();
            for ( String key : keyset ) {
                ConfigurationSection sec = section.getConfigurationSection(key);
                Channel channel = getChannelFromSection(sec);
                channels.put(key, channel);
            }
        }

        // デフォルト設定のロード
        if ( defaultChannels == null ) {
            defaultChannels = new HashMap<String, String>();
        } else {
            defaultChannels.clear();
        }

        if ( config.contains("defaults") ) {
            ConfigurationSection section = config.getConfigurationSection("defaults");
            Set<String> keyset = section.getValues(false).keySet();
            for ( String key : keyset ) {
                defaultChannels.put(key, section.getString(key));
            }
        }
    }

    /**
     * セクションからChannelクラスを生成して返す
     * @param section セクション
     * @return Channel
     */
    private Channel getChannelFromSection(ConfigurationSection section) {

        String name = section.getName();
        String desc = section.getString(KEY_DESC, "");
        String format = section.getString(KEY_FORMAT, DEFAULT_FORMAT);
        List<String> members = section.getStringList(KEY_MEMBERS);
        List<String> banned = section.getStringList(KEY_BANNED);
        List<String> moderator = section.getStringList(KEY_MODERATOR);
        String password = section.getString(KEY_PASSWORD, "");
        boolean visible = section.getBoolean(KEY_VISIBLE, true);

        // グローバルチャンネルのメンバー情報はクリアする
        if ( LunaChat.config.globalChannel.equals(name) ) {
            members = new ArrayList<String>();
        }

        Channel channel = new Channel(name, desc, members);
        channel.format = format;
        channel.banned = banned;
        channel.moderator = moderator;
        channel.password = password;
        channel.visible = visible;
        return channel;
    }

    /**
     * 保存する
     */
    protected boolean save() {

        try {
            YamlConfiguration config =
                    YamlConfiguration.loadConfiguration(file);

            for ( String key : channels.keySet() ) {
                Channel channel = channels.get(key);
                config.set("channels." + key + "." + KEY_DESC, channel.description);
                config.set("channels." + key + "." + KEY_FORMAT, channel.format);
                config.set("channels." + key + "." + KEY_MEMBERS, channel.members);
                config.set("channels." + key + "." + KEY_BANNED, channel.banned);
                config.set("channels." + key + "." + KEY_MODERATOR, channel.moderator);
                config.set("channels." + key + "." + KEY_PASSWORD, channel.password);
                config.set("channels." + key + "." + KEY_VISIBLE, channel.visible);
            }

            for ( String key : defaultChannels.keySet() ) {
                config.set("defaults." + key, defaultChannels.get(key));
            }

            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * チャンネルをアップデートする
     * @param channel
     */
    protected void updateChannel(Channel channel) {
        String key = channel.name;
        channels.put(key, channel);
        save();
    }

    /**
     * チャンネルの名前リストを返す
     * @return チャンネルの名前
     */
    protected ArrayList<String> getNames() {

        ArrayList<String> names = new ArrayList<String>();
        for ( String k : channels.keySet() ) {
            names.add(k);
        }
        return names;
    }

    /**
     * リスト表示用のリストを返す
     * @param player プレイヤー、指定しない場合はnullにする
     * @return リスト
     */
    protected ArrayList<String> getList(Player player) {

        ArrayList<String> items = new ArrayList<String>();
        String dchannel = "";
        String playerName = "";
        if ( player != null ) {
            playerName = player.getName();
            dchannel = defaultChannels.get(player.getName());
            if ( dchannel == null ) {
                dchannel = "";
            }
        }

        items.add(Utility.replaceColorCode(LIST_FIRSTLINE));
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);

            // BANされているチャンネルは表示しない
            if ( channel.banned.contains(playerName) ) {
                continue;
            }

            // visible=false のチャンネルは表示しない
            if ( !channel.visible ) {
                continue;
            }

            String disp = ChatColor.WHITE + key;
            if ( key.equals(dchannel) ) {
                disp = ChatColor.RED + key;
            }
            if ( player != null && !channel.members.contains(playerName) &&
                    !key.equals(LunaChat.config.globalChannel) ) {
                disp = ChatColor.GRAY + key;
            }
            String desc = channel.description;
            int onlineNum = channel.getOnlineNum();
            int memberNum = channel.getTotalNum();
            String item = String.format(
                    Utility.replaceColorCode(LIST_FORMAT),
                    disp, onlineNum, memberNum, desc);
            items.add(item);
        }
        items.add(Utility.replaceColorCode(LIST_ENDLINE));

        return items;
    }

    /**
     * プレイヤーのサーバー参加時用の参加チャンネルリストを返す
     * @param player プレイヤー
     * @return リスト
     */
    protected ArrayList<String> getListForMotd(Player player) {

        ArrayList<String> items = new ArrayList<String>();
        String playerName = player.getName();
        String dchannel = defaultChannels.get(player.getName());
        if ( dchannel == null ) {
            dchannel = "";
        }

        items.add(Utility.replaceColorCode(MOTD_FIRSTLINE));
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            if ( !channel.members.contains(playerName) &&
                    !key.equals(LunaChat.config.globalChannel) ) {
                continue;
            }

            String disp = ChatColor.WHITE + key;
            if ( key.equals(dchannel) ) {
                disp = ChatColor.RED + key;
            }
            String desc = channel.description;
            int onlineNum = channel.getOnlineNum();
            int memberNum = channel.getTotalNum();
            String item = String.format(
                    Utility.replaceColorCode(LIST_FORMAT),
                    disp, onlineNum, memberNum, desc);
            items.add(item);
        }
        items.add(Utility.replaceColorCode(LIST_ENDLINE));

        return items;
    }

    /**
     * プレイヤーが参加しているチャンネルを返す
     * @param player プレイヤー
     * @return チャンネル
     */
    protected ArrayList<Channel> getChannelByPlayer(Player player) {

        ArrayList<Channel> result = new ArrayList<Channel>();
        String name = player.getName();
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            if ( channel.members.contains(name) ||
                    key.equals(LunaChat.config.globalChannel) ) {
                result.add(channel);
            }
        }
        return result;
    }

    /**
     * プレイヤーが参加しているデフォルトのチャンネルを返す
     * @param player プレイヤー
     * @return チャンネル
     */
    protected Channel getDefaultChannelByPlayer(String name) {

        String cname = defaultChannels.get(name);
        if ( cname == null || !channels.containsKey(cname) ) {
            return null;
        }
        return channels.get(cname);
    }

    /**
     * 指定した名前のプレイヤーに設定されている、デフォルトチャンネル名を取得する
     * @param name プレイヤー名
     * @return デフォルトチャンネル名
     */
    protected String getDefault(String name) {
        return defaultChannels.get(name);
    }

    /**
     * 指定した名前のプレイヤーに設定されている、デフォルトチャンネルを削除する
     * @param name プレイヤー名
     */
    protected void removeDefault(String name) {
        if ( defaultChannels.containsKey(name) ) {
            defaultChannels.remove(name);
        }
    }

    /**
     * プレイヤーのデフォルトチャンネルを設定する
     * @param player プレイヤー
     * @param cname チャンネル名
     */
    protected void setDefaultChannel(String name, String cname) {
        defaultChannels.put(name, cname);
        save();
    }

    /**
     * チャンネルを取得する
     * @param name チャンネル名
     * @return チャンネル
     */
    protected Channel getChannel(String name) {
        return channels.get(name);
    }

    /**
     * 新しいチャンネルを作成する
     * @param name チャンネル名
     * @param desc チャンネルの説明文
     * @return 作成されたチャンネル
     */
    protected Channel createChannel(String name, String desc) {
        Channel channel = new Channel(name, desc);
        channels.put(name, channel);
        save();
        return channel;
    }

    /**
     * チャンネルを削除する
     * @param name 削除するチャンネル名
     */
    protected void removeChannel(String name) {
        if ( channels.containsKey(name) ) {
            channels.remove(name);
        }
        Channel channel = getChannel(name);
        if ( channel != null ) {
            // チャンネルのメンバーを強制解散させる
            String message = String.format(Utility.replaceColorCode(
                    Resources.get("breakupMessage")), name);
            for ( String pname : channel.members ) {
                Player player = LunaChat.getPlayerExact(pname);
                if ( player != null ) {
                    player.sendMessage(message);
                }
            }
        }
        save();
    }
}
