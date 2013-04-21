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
    
    private static final String DEFAULT_FORMAT = Resources.get("defaultFormat");
    
    private static final String FILE_NAME = "channels.yml";
    
    private static final String KEY_DESC = "desc";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_BANNED = "banned";
    
    private File file;
    private HashMap<String, Channel> channels;

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
        
        if ( channels == null ) {
            channels = new HashMap<String, Channel>();
        } else {
            channels.clear();
        }
        
        Set<String> keyset = config.getValues(false).keySet();
        for ( String key : keyset ) {
            ConfigurationSection section = config.getConfigurationSection(key);
            Channel channel = getChannelFromSection(section);
            channels.put(key, channel);
        }
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
                config.set(key + "." + KEY_DESC, channel.description);
                config.set(key + "." + KEY_FORMAT, channel.format);
                config.set(key + "." + KEY_MEMBERS, channel.members);
                config.set(key + "." + KEY_BANNED, channel.banned);
            }
            
            config.save(file);
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
     * @return リスト
     */
    protected ArrayList<String> getList() {
        
        ArrayList<String> items = new ArrayList<String>();
        items.add(Utility.replaceColorCode(LIST_FIRSTLINE));
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            String desc = channel.description;
            int onlineNum = 0;
            for ( String pname : channel.members ) {
                Player p = LunaChat.getPlayerExact(pname);
                if ( p != null && p.isOnline() ) {
                    onlineNum++;
                }
            }
            int memberNum = channel.members.size();
            String item = String.format(
                    Utility.replaceColorCode(LIST_FORMAT), 
                    key, onlineNum, memberNum, desc);
            items.add(item);
        }
        items.add(LIST_ENDLINE);
        
        return items;
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
        if ( members == null ) {
            members = new ArrayList<String>();
        }
        
        Channel channel = new Channel(name, desc, members);
        channel.format = format;
        channel.banned = banned;
        return channel;
    }
    
    /**
     * プレイヤーが参加しているチャンネルを返す
     * @param player プレイヤー
     * @return チャンネル
     */
    protected Channel getChannelByPlayer(Player player) {
        
        String name = player.getName();
        for ( String key : channels.keySet() ) {
            Channel channel = channels.get(key);
            if ( channel.members.contains(name) ) {
                return channel;
            }
        }
        return null;
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
    
    protected void removeChannel(String name) {
        if ( channels.containsKey(name) ) {
            channels.remove(name);
        }
        Channel channel = getChannel(name);
        if ( channel != null ) {
            // TODO チャンネルのメンバーを強制解散させる
            
        }
        save();
    }
}
