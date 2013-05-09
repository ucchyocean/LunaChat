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
import java.util.Set;

import org.bukkit.ChatColor;
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

    private static final String FILE_NAME_DCHANNELS = "defaults.yml";

    private File fileDefaults;
    private HashMap<String, Channel> channels;
    private HashMap<String, String> defaultChannels;

    /**
     * コンストラクタ
     */
    public ChannelManager() {
        loadAllChannels();
    }

    /**
     * 読み込みする
     */
    protected void loadAllChannels() {

        // デフォルトチャンネル設定のロード
        fileDefaults = new File(
                LunaChat.instance.getDataFolder() +
                File.separator + FILE_NAME_DCHANNELS);

        if ( !fileDefaults.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(fileDefaults);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(fileDefaults);

        defaultChannels = new HashMap<String, String>();
        Set<String> keyset = config.getValues(false).keySet();
        for ( String key : keyset ) {
            defaultChannels.put(key, config.getString(key));
        }

        // チャンネル設定のロード
        channels = Channel.loadAllChannels();
    }

    /**
     * 保存する
     */
    public boolean saveDefaults() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("", defaultChannels);
            config.save(fileDefaults);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
            if ( channel.getBanned().contains(playerName) ) {
                continue;
            }

            String disp = ChatColor.WHITE + key;
            if ( key.equals(dchannel) ) {
                disp = ChatColor.RED + key;
            }
            if ( player != null && !channel.getMembers().contains(playerName) &&
                    !key.equals(LunaChat.config.globalChannel) ) {

                // 未参加で visible=false のチャンネルは表示しない
                if ( !channel.isVisible() ) {
                    continue;
                }
                disp = ChatColor.GRAY + key;
            }
            String desc = channel.getDescription();
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
            if ( !channel.getMembers().contains(playerName) &&
                    !key.equals(LunaChat.config.globalChannel) ) {
                continue;
            }

            String disp = ChatColor.WHITE + key;
            if ( key.equals(dchannel) ) {
                disp = ChatColor.RED + key;
            }
            String desc = channel.getDescription();
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
            if ( channel.getMembers().contains(name) ||
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
        saveDefaults();
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
    protected Channel createChannel(String name) {
        Channel channel = new Channel(name);
        channels.put(name, channel);
        channel.save();
        return channel;
    }

    /**
     * チャンネルを削除する
     * @param name 削除するチャンネル名
     */
    protected void removeChannel(String name) {
        Channel channel = getChannel(name);
        if ( channel != null ) {
            channel.remove();
            channels.remove(name);

            // チャンネルのメンバーを強制解散させる
            String message = String.format(Utility.replaceColorCode(
                    Resources.get("breakupMessage")), name);
            for ( String pname : channel.getMembers() ) {
                Player player = LunaChat.getPlayerExact(pname);
                if ( player != null ) {
                    player.sendMessage(message);
                }
            }
        }
    }

    /**
     * 指定された名前がチャンネル名として使用可能かどうかを判定する
     * @param name 名前
     * @return チャンネル名として使用可能かどうか
     */
    protected boolean checkForChannelName(String name) {
        return name.matches("[0-9a-zA-Z\\-_]{1,20}");
    }
}
