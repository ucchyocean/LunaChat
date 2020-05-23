/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.channel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.github.ucchyocean.lc.CommandSenderInterface;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.YamlConfig;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.member.ChannelMember;

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
    private static final String FILE_NAME_HIDELIST = "hidelist.yml";

    private File fileDefaults;
    private File fileTemplates;
    private File fileJapanize;
    private File fileDictionary;
    private File fileHidelist;
    private HashMap<String, Channel> channels;
    private HashMap<String, String> defaultChannels;
    private HashMap<String, String> templates;
    private HashMap<String, Boolean> japanize;
    private HashMap<String, String> dictionary;
    private HashMap<String, List<ChannelMember>> hidelist;

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
        fileDefaults = new File(LunaChat.getDataFolder(), FILE_NAME_DCHANNELS);

        if ( !fileDefaults.exists() ) {
            makeEmptyFile(fileDefaults);
        }

        YamlConfig config = YamlConfig.load(fileDefaults);

        defaultChannels = new HashMap<String, String>();
        for ( String key : config.getKeys(false) ) {
            String value = config.getString(key);
            if ( value != null) {
                defaultChannels.put(key, value.toLowerCase());
            }
        }

        // テンプレート設定のロード
        fileTemplates = new File(LunaChat.getDataFolder(), FILE_NAME_TEMPLATES);

        if ( !fileTemplates.exists() ) {
            makeEmptyFile(fileTemplates);
        }

        YamlConfig configTemplates = YamlConfig.load(fileTemplates);

        templates = new HashMap<String, String>();
        for ( String key : configTemplates.getKeys(false) ) {
            templates.put(key, configTemplates.getString(key));
        }

        // Japanize設定のロード
        fileJapanize = new File(LunaChat.getDataFolder(), FILE_NAME_JAPANIZE);

        if ( !fileJapanize.exists() ) {
            makeEmptyFile(fileJapanize);
        }

        YamlConfig configJapanize = YamlConfig.load(fileJapanize);

        japanize = new HashMap<String, Boolean>();
        for ( String key : configJapanize.getKeys(false) ) {
            japanize.put(key, configJapanize.getBoolean(key));
        }

        // dictionaryのロード
        fileDictionary = new File(LunaChat.getDataFolder(), FILE_NAME_DICTIONARY);

        if ( !fileDictionary.exists() ) {
            makeEmptyFile(fileDictionary);
        }

        YamlConfig configDictionary = YamlConfig.load(fileDictionary);

        dictionary = new HashMap<String, String>();
        for ( String key : configDictionary.getKeys(false) ) {
            dictionary.put(key, configDictionary.getString(key));
        }

        // hideリストのロード
        fileHidelist = new File(LunaChat.getDataFolder(), FILE_NAME_HIDELIST);

        if ( !fileHidelist.exists() ) {
            makeEmptyFile(fileHidelist);
        }

        YamlConfig configHidelist = YamlConfig.load(fileHidelist);

        hidelist = new HashMap<String, List<ChannelMember>>();
        for ( String key : configHidelist.getKeys(false) ) {
            hidelist.put(key, new ArrayList<ChannelMember>());
            for ( String id : configHidelist.getStringList(key) ) {
                hidelist.get(key).add(ChannelMember.getChannelMember(id));
            }
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
            YamlConfig config = new YamlConfig();
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
            YamlConfig config = new YamlConfig();
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
            YamlConfig config = new YamlConfig();
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
            YamlConfig config = new YamlConfig();
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
     * Hidelist設定を保存する
     * @return 保存したかどうか
     */
    private boolean saveHidelist() {

        try {
            YamlConfig config = new YamlConfig();
            for ( String key : hidelist.keySet() ) {
                config.set(key, getIdList(hidelist.get(key)));
            }
            config.save(fileHidelist);
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

        ChannelMember cp = ChannelMember.getChannelMember(playerName);
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
     * @param channelName チャンネル名、または、チャンネルの別名
     * @return チャンネル
     * @see com.github.ucchyocean.lc.LunaChatAPI#getChannel(java.lang.String)
     */
    @Override
    public Channel getChannel(String channelName) {
        if ( channelName == null ) return null;
        Channel channel = channels.get(channelName.toLowerCase());
        if ( channel != null ) return channel;
        for ( Channel ch : channels.values() ) {
            String alias = ch.getAlias();
            if ( alias != null && alias.length() > 0
                    && channelName.equalsIgnoreCase(ch.getAlias()) ) {
                return ch;
            }
        }
        return null;
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
     * @see com.github.ucchyocean.lc.LunaChatAPI#createChannel(java.lang.String, com.github.ucchyocean.lc.CommandSenderInterface)
     */
    @Override
    public Channel createChannel(String channelName, CommandSenderInterface sender) {

        // イベントコール
//        EventResult result =
//                LunaChat.getEventSender().sendLunaChatChannelCreateEvent(channelName, sender);
//
//        if ( result.isCancelled() ) {
//            return null;
//        }
//        channelName = result.getValueAsString("channelName");

        Channel channel = new BukkitChannel(channelName);
        channels.put(channelName.toLowerCase(), channel);
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
     * @see com.github.ucchyocean.lc.LunaChatAPI#removeChannel(java.lang.String, com.github.ucchyocean.lc.CommandSenderInterface)
     */
    @Override
    public boolean removeChannel(String channelName, CommandSenderInterface sender) {

        channelName = channelName.toLowerCase();

        // イベントコール
//        EventResult result =
//                LunaChat.getEventSender().sendLunaChatChannelRemoveEvent(channelName, sender);
//        if ( result.isCancelled() ) {
//            return false;
//        }

        Channel channel = getChannel(channelName);
        if ( channel != null ) {

            // 強制解散のメッセージを、残ったメンバーに流す
            if ( !channel.isPersonalChat() && !MSG_BREAKUP.equals("") ) {
                String message = new String(MSG_BREAKUP);
                message = message.replace("%ch", channel.getName());
                message = message.replace("%color", channel.getColorCode());
                for ( ChannelMember cp : channel.getMembers() ) {
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
        saveDictionary();
    }

    /**
     * 該当のプレイヤーに関連するhidelistを取得する。
     * @param key プレイヤー
     * @return 指定されたプレイヤーをhideしているプレイヤー(非null)
     */
    public List<ChannelMember> getHidelist(ChannelMember key) {
        if ( key == null ) {
            return new ArrayList<ChannelMember>();
        }
        if ( hidelist.containsKey(key.toString()) ) {
            return hidelist.get(key.toString());
        }
        return new ArrayList<ChannelMember>();
    }

    /**
     * 該当のプレイヤーがhideしているプレイヤーのリストを返す。
     * @param player プレイヤー
     * @return 指定したプレイヤーがhideしているプレイヤーのリスト(非null)
     */
    public ArrayList<ChannelMember> getHideinfo(ChannelMember player) {
        if ( player == null ) {
            return new ArrayList<ChannelMember>();
        }
        ArrayList<ChannelMember> info = new ArrayList<ChannelMember>();
        for ( String key : hidelist.keySet() ) {
            if ( hidelist.get(key).contains(player) ) {
                info.add(ChannelMember.getChannelMember(key));
            }
        }
        return info;
    }

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーをhideするように設定する。
     * @param player hideする側のプレイヤー
     * @param hided hideされる側のプレイヤー
     */
    public void addHidelist(ChannelMember player, ChannelMember hided) {
        String hidedId = hided.toString();
        if ( !hidelist.containsKey(hidedId) ) {
            hidelist.put(hidedId, new ArrayList<ChannelMember>());
        }
        if ( !hidelist.get(hidedId).contains(player) ) {
            hidelist.get(hidedId).add(player);
            saveHidelist();
        }
    }

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーのhideを解除するように設定する。
     * @param player hideしていた側のプレイヤー
     * @param hided hideされていた側のプレイヤー
     */
    public void removeHidelist(ChannelMember player, ChannelMember hided) {
        String hidedId = hided.toString();
        if ( !hidelist.containsKey(hidedId) ) {
            return;
        }
        if ( hidelist.get(hidedId).contains(player) ) {
            hidelist.get(hidedId).remove(player);
            if ( hidelist.get(hidedId).size() <= 0 ) {
                hidelist.remove(hidedId);
            }
            saveHidelist();
        }
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
        // TODO スレッド作成はサーバーに任せる
        JapanizeConvertTask task = new JapanizeConvertTask(
                message, type, null, null, "%japanize");
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

    /**
     * ChannelMemberのリストを、IDのStringリストに変換して返す
     * @param players
     * @return
     */
    private List<String> getIdList(List<ChannelMember> players) {
        List<String> results = new ArrayList<String>();
        for ( ChannelMember cp : players ) {
            results.add(cp.toString());
        }
        return results;
    }

    /**
     * 指定されたファイル出力先に、空のYamlファイルを作成する
     * @param file 出力先
     */
    private void makeEmptyFile(File file) {
        YamlConfig conf = new YamlConfig();
        try {
            conf.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
