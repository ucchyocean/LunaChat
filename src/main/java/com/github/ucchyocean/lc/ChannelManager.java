/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.event.LunaChatChannelCreateEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelRemoveEvent;

/**
 * @author ucchy
 * チャンネルマネージャー
 */
public class ChannelManager implements LunaChatAPI {

    private static final String LIST_FIRSTLINE = Resources.get("listFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String MOTD_FIRSTLINE = Resources.get("motdFirstLine");

    private static final String FILE_NAME_DCHANNELS = "defaults.yml";
    private static final String FILE_NAME_TEMPLATES = "templates.yml";

    private File fileDefaults;
    private File fileTemplates;
    private HashMap<String, Channel> channels;
    private HashMap<String, String> defaultChannels;
    private HashMap<String, String> templates;

    /**
     * コンストラクタ
     */
    public ChannelManager() {
        loadAllData();
    }

    /**
     * すべて読み込みする
     */
    protected void loadAllData() {

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
            defaultChannels.put(key.toLowerCase(), config.getString(key));
        }

        // テンプレート設定のロード
        fileTemplates = new File(
                LunaChat.instance.getDataFolder() +
                File.separator + FILE_NAME_TEMPLATES);

        if ( !fileTemplates.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(fileTemplates);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configTemplates =
                YamlConfiguration.loadConfiguration(fileTemplates);

        templates = new HashMap<String, String>();
        for ( String key : configTemplates.getValues(false).keySet() ) {
            templates.put(key, configTemplates.getString(key));
        }

        // チャンネル設定のロード
        channels = Channel.loadAllChannels();
    }

    /**
     * すべて保存する
     */
    protected void saveAllChannels() {

        saveDefaults();

        for ( Channel channel : channels.values() ) {
            channel.save();
        }
    }

    /**
     * デフォルトチャンネル設定を保存する
     * @return 保存したかどうか
     */
    public boolean saveDefaults() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            for ( String key : defaultChannels.keySet() ) {
                config.set(key, defaultChannels.get(key));
            }
            config.save(fileDefaults);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * テンプレート設定を保存する
     * @return 保存したかどうか
     */
    public boolean saveTemplates() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            for ( String key : templates.keySet() ) {
                config.set(key, templates.get(key));
            }
            config.save(fileTemplates);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
     * 指定したチャンネル名が存在するかどうかを返す
     * @param channelName チャンネル名
     * @return 存在するかどうか
     * @see com.github.ucchyocean.lc.LunaChatAPI#isExistChannel(java.lang.String)
     */
    public boolean isExistChannel(String channelName) {
        return channels.containsKey(channelName.toLowerCase());
    }

    /**
     * 全てのチャンネルを返す
     * @return 全てのチャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getChannels()
     */
    @Override
    public Collection<Channel> getChannels() {

        return channels.values();
    }

    /**
     * プレイヤーが参加しているチャンネルを返す
     * @param playerName プレイヤー名
     * @return チャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getChannelsByPlayer(java.lang.String)
     */
    @Override
    public Collection<Channel> getChannelsByPlayer(String playerName) {

        Collection<Channel> result = new ArrayList<Channel>();
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            if ( channel.getMembers().contains(playerName) ||
                    key.equals(LunaChat.config.globalChannel) ) {
                result.add(channel);
            }
        }
        return result;
    }

    /**
     * プレイヤーが参加しているデフォルトのチャンネルを返す
     * @param playerName プレイヤー
     * @return チャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getDefaultChannel(java.lang.String)
     */
    @Override
    public Channel getDefaultChannel(String playerName) {

        String cname = defaultChannels.get(playerName);
        if ( cname == null || !channels.containsKey(cname) ) {
            return null;
        }
        return channels.get(cname);
    }

    /**
     * プレイヤーのデフォルトチャンネルを設定する
     * @param playerName プレイヤー
     * @param channelName チャンネル名
     * @see com.github.ucchyocean.lc.LunaChatAPI#setDefaultChannel(java.lang.String, java.lang.String)
     */
    @Override
    public void setDefaultChannel(String playerName, String channelName) {
        defaultChannels.put(playerName, channelName.toLowerCase());
        saveDefaults();
    }

    /**
     * 指定した名前のプレイヤーに設定されている、デフォルトチャンネルを削除する
     * @param playerName プレイヤー名
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeDefaultChannel(java.lang.String)
     */
    @Override
    public void removeDefaultChannel(String playerName) {
        if ( defaultChannels.containsKey(playerName) ) {
            defaultChannels.remove(playerName);
        }
    }

    /**
     * チャンネルを取得する
     * @param channelName チャンネル名
     * @return チャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getChannel(java.lang.String)
     */
    @Override
    public Channel getChannel(String channelName) {
        return channels.get(channelName.toLowerCase());
    }

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @return 作成されたチャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#createChannel(java.lang.String)
     */
    @Override
    public Channel createChannel(String channelName) {

        channelName = channelName.toLowerCase();

        // イベントコール
        LunaChatChannelCreateEvent event =
                new LunaChatChannelCreateEvent(channelName);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return null;
        }
        String name = event.getChannelName();

        Channel channel = new Channel(name);
        channels.put(name, channel);
        channel.save();
        return channel;
    }

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @return 削除したかどうか
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeChannel(java.lang.String)
     */
    @Override
    public boolean removeChannel(String channelName) {

        channelName = channelName.toLowerCase();

        // イベントコール
        LunaChatChannelRemoveEvent event =
                new LunaChatChannelRemoveEvent(channelName);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return false;
        }

        Channel channel = getChannel(channelName);
        if ( channel != null ) {
            channel.remove();
            channels.remove(channelName);

            // チャンネルのメンバーを強制解散させる
            String message = String.format(Utility.replaceColorCode(
                    Resources.get("breakupMessage")), channelName);
            for ( String pname : channel.getMembers() ) {
                Player player = LunaChat.getPlayerExact(pname);
                if ( player != null ) {
                    player.sendMessage(message);
                }
            }
        }

        return true;
    }

    /**
     * テンプレートを取得する
     * @param id テンプレートID
     * @return テンプレート
     * @see com.github.ucchyocean.lc.LunaChatAPI#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String id) {
        return templates.get(id);
    }

    /**
     * テンプレートを登録する
     * @param id テンプレートID
     * @param template テンプレート
     * @see com.github.ucchyocean.lc.LunaChatAPI#setTemplate(java.lang.String, java.lang.String)
     */
    @Override
    public void setTemplate(String id, String template) {
        templates.put(id, template);
    }

    /**
     * テンプレートを削除する
     * @param id テンプレートID
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeTemplate(java.lang.String)
     */
    @Override
    public void removeTemplate(String id) {
        templates.remove(id);
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
