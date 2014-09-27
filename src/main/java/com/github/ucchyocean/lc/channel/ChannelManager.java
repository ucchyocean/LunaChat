/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.channel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.event.LunaChatChannelCreateEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelRemoveEvent;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * チャンネルマネージャー
 * @author ucchy
 */
public class ChannelManager implements LunaChatAPI {

    private static final String MSG_BREAKUP = Resources.get("breakupMessage");

    private static final String FILE_NAME_DCHANNELS = "defaults.yml";
    private static final String FILE_NAME_TEMPLATES = "templates.yml";
    private static final String FILE_NAME_JAPANIZE = "japanize.yml";
    private static final String FILE_NAME_DICTIONARY = "dictionary.yml";

    private File fileDefaults;
    private File fileTemplates;
    private File fileJapanize;
    private File fileDictionary;
    private HashMap<String, Channel> channels;
    private HashMap<String, String> defaultChannels;
    private HashMap<String, String> templates;
    private HashMap<String, Boolean> japanize;
    private HashMap<String, String> dictionary;

    /**
     * コンストラクタ
     */
    public ChannelManager() {
        reloadAllData();
    }

    /**
     * すべて読み込みする
     */
    @Override
    public void reloadAllData() {

        // デフォルトチャンネル設定のロード
        fileDefaults = new File(
                LunaChat.getInstance().getDataFolder(), FILE_NAME_DCHANNELS);

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
        for ( String key : config.getKeys(false) ) {
            defaultChannels.put(key, config.getString(key).toLowerCase());
        }

        // テンプレート設定のロード
        fileTemplates = new File(
                LunaChat.getInstance().getDataFolder(), FILE_NAME_TEMPLATES);

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
        for ( String key : configTemplates.getKeys(false) ) {
            templates.put(key, configTemplates.getString(key));
        }

        // Japanize設定のロード
        fileJapanize = new File(
                LunaChat.getInstance().getDataFolder(), FILE_NAME_JAPANIZE);

        if ( !fileJapanize.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(fileJapanize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configJapanize =
                YamlConfiguration.loadConfiguration(fileJapanize);

        japanize = new HashMap<String, Boolean>();
        for ( String key : configJapanize.getKeys(false) ) {
            japanize.put(key, configJapanize.getBoolean(key));
        }

        // dictionaryのロード
        fileDictionary = new File(
                LunaChat.getInstance().getDataFolder(), FILE_NAME_DICTIONARY);
        if ( !fileDictionary.exists() ) {
            YamlConfiguration conf = new YamlConfiguration();
            try {
                conf.save(fileDictionary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration configDictionary =
                YamlConfiguration.loadConfiguration(fileDictionary);

        dictionary = new HashMap<String, String>();
        for ( String key : configDictionary.getKeys(false) ) {
            dictionary.put(key, configDictionary.getString(key));
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
    private boolean saveDefaults() {

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
    private boolean saveTemplates() {

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
     * Japanize設定を保存する
     * @return 保存したかどうか
     */
    private boolean saveJapanize() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            for ( String key : japanize.keySet() ) {
                config.set(key, japanize.get(key));
            }
            config.save(fileJapanize);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Dictionary設定を保存する
     * @return 保存したかどうか
     */
    private boolean saveDictionary() {

        try {
            YamlConfiguration config = new YamlConfiguration();
            for ( String key : dictionary.keySet() ) {
                config.set(key, dictionary.get(key));
            }
            config.save(fileDictionary);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * デフォルトチャンネル設定を全て削除する
     */
    public void removeAllDefaultChannels() {
        defaultChannels.clear();
        saveDefaults();
    }

    /**
     * プレイヤーのJapanize設定を返す
     * @param playerName プレイヤー名
     * @return Japanize設定
     */
    @Override
    public boolean isPlayerJapanize(String playerName) {
        if ( !japanize.containsKey(playerName) ) {
            return true;
        }
        return japanize.get(playerName);
    }

    /**
     * 指定したチャンネル名が存在するかどうかを返す
     * @param channelName チャンネル名
     * @return 存在するかどうか
     * @see com.github.ucchyocean.lc.LunaChatAPI#isExistChannel(java.lang.String)
     */
    @Override
    public boolean isExistChannel(String channelName) {
        if ( channelName == null ) {
            return false;
        }
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

        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(playerName);
        Collection<Channel> result = new ArrayList<Channel>();
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            if ( channel.getMembers().contains(cp) ||
                    channel.isGlobalChannel() ) {
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

        if ( cname == null || !isExistChannel(cname) ) {
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
        if ( channelName == null ) {
            removeDefaultChannel(playerName);
            return;
        }
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
        saveDefaults();
    }

    /**
     * チャンネルを取得する
     * @param channelName チャンネル名
     * @return チャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getChannel(java.lang.String)
     */
    @Override
    public Channel getChannel(String channelName) {
        if ( channelName == null ) return null;
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
        return createChannel(channelName, null);
    }

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @return 作成されたチャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#createChannel(java.lang.String, org.bukkit.command.CommandSender)
     */
    @Override
    public Channel createChannel(String channelName, CommandSender sender) {

        // イベントコール
        LunaChatChannelCreateEvent event =
                new LunaChatChannelCreateEvent(channelName, sender);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return null;
        }
        String name = event.getChannelName();

        Channel channel = new ChannelImpl(name);
        channels.put(name.toLowerCase(), channel);
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
        return removeChannel(channelName, null);
    }

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @return 削除したかどうか
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeChannel(java.lang.String, org.bukkit.command.CommandSender)
     */
    @Override
    public boolean removeChannel(String channelName, CommandSender sender) {

        channelName = channelName.toLowerCase();

        // イベントコール
        LunaChatChannelRemoveEvent event =
                new LunaChatChannelRemoveEvent(channelName, sender);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return false;
        }

        Channel channel = getChannel(channelName);
        if ( channel != null ) {

            // 強制解散のメッセージを、残ったメンバーに流す
            if ( !channel.isPersonalChat() && !MSG_BREAKUP.equals("") ) {
                String message = new String(MSG_BREAKUP);
                message = message.replace("%ch", channel.getName());
                message = message.replace("%color", channel.getColorCode());
                for ( ChannelPlayer cp : channel.getMembers() ) {
                    cp.sendMessage(message);
                }
            }

            // チャンネルの削除
            channel.remove();
            channels.remove(channelName);
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
        saveTemplates();
    }

    /**
     * テンプレートを削除する
     * @param id テンプレートID
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeTemplate(java.lang.String)
     */
    @Override
    public void removeTemplate(String id) {
        templates.remove(id);
        saveTemplates();
    }

    /**
     * 辞書データを全て取得する
     * @return 辞書データ
     */
    public HashMap<String, String> getAllDictionary() {
        return dictionary;
    }

    /**
     * 新しい辞書データを追加する
     * @param key キー
     * @param value 値
     */
    public void setDictionary(String key, String value) {
        dictionary.put(key, value);
        saveDictionary();
    }

    /**
     * 指定したキーの辞書データを削除する
     * @param key キー
     */
    public void removeDictionary(String key) {
        dictionary.remove(key);
    }

    /**
     * Japanize変換を行う
     * @param message 変換するメッセージ
     * @param type 変換タイプ
     * @return 変換後のメッセージ、ただしイベントでキャンセルされた場合はnullが返されるので注意
     */
    @Override
    public String japanize(String message, JapanizeType type) {

        if ( type == JapanizeType.NONE ) {
            return message;
        }

        // Japanize変換タスクを作成して、同期で実行する。
        DelayedJapanizeConvertTask task = new DelayedJapanizeConvertTask(
                message, type, null, null, "%japanize", null);
        if ( task.runSync() ) {
            return task.getResult();
        }
        return null;
    }

    /**
     * 該当プレイヤーのJapanize変換をオン/オフする
     * @param playerName 設定するプレイヤー名
     * @param doJapanize Japanize変換するかどうか
     */
    @Override
    public void setPlayersJapanize(String playerName, boolean doJapanize) {
        japanize.put(playerName, doJapanize);
        saveJapanize();
    }
}
