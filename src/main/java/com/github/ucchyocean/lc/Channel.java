/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

/**
 * @author ucchy
 * チャンネル
 */
public class Channel {

    private static final String DEFAULT_FORMAT = Resources.get("defaultFormat");
    private static final String MSG_JOIN = Resources.get("joinMessage");
    private static final String MSG_QUIT = Resources.get("quitMessage");
    
    /** 参加者 */
    protected List<String> members;
    
    /** BANされたプレイヤー */
    protected List<String> banned;
    
    /** チャンネルの名称 */
    protected String name;
    
    /** チャンネルの説明文 */
    protected String description;
    
    /** メッセージフォーマット<br>
     * 指定可能なキーワードは下記のとおり<br>
     * %ch - チャンネル名<br>
     * %username - ユーザー名<br>
     * %msg - メッセージ
     * */
    protected String format;
    
    /**
     * コンストラクタ
     * @param name チャンネルの名称
     * @param description チャンネルの説明文
     */
    protected Channel(String name, String description) {
        this.name = name;
        this.description = description;
        this.members = new ArrayList<String>();
        this.format = DEFAULT_FORMAT;
        this.banned = new ArrayList<String>();
    }
    
    /**
     * コンストラクタ
     * @param name チャンネルの名称
     * @param description チャンネルの説明文
     * @param members 参加者
     */
    protected Channel(String name, String description, List<String> members) {
        this.name = name;
        this.description = description;
        this.members = members;
        this.format = DEFAULT_FORMAT;
        this.banned = new ArrayList<String>();
    }
    
    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    protected void chat(Player player, String message) {
        
        String msg = format.replace("%ch", name);
        msg = msg.replace("%username", player.getDisplayName());
        msg = msg.replace("%msg", message);
        msg = Utility.replaceColorCode(msg);
        
        // オンラインのプレイヤーに送信する
        for ( String member : members ) {
            Player p = LunaChat.getPlayerExact(member);
            if ( p != null ) {
                p.sendMessage(msg);
            }
        }
        
        // ロギング
        if ( LunaChat.config.loggingChat ) {
            LunaChat.log(msg);
        }
    }

    /**
     * メンバーを追加する
     * @param name 追加するメンバー名
     */
    protected void addMember(String name) {
        
        if ( !members.contains(name) ) {
            members.add(name);
            sendJoinQuitMessage(true, name);
            LunaChat.manager.save();
        }
    }
    
    /**
     * メンバーを削除する
     * @param name 削除するメンバー名
     */
    protected void removeMember(String name) {
        
        if ( !members.contains(name) ) {
            members.remove(name);
            sendJoinQuitMessage(false, name);
            LunaChat.manager.save();
        }
    }
    
    /**
     * 入退室メッセージを流す
     * @param isJoin 入室かどうか（falseなら退室）
     * @param player 入退室したプレイヤー名
     */
    protected void sendJoinQuitMessage(boolean isJoin, String player) {
        
        String msg;
        if ( isJoin ) {
            msg = MSG_JOIN;
        } else {
            msg = MSG_QUIT;
        }
        msg = msg.replace("%ch", name);
        msg = msg.replace("%username", player);
        msg = Utility.replaceColorCode(msg);
        
        // オンラインのプレイヤーに送信する
        for ( String member : members ) {
            Player p = LunaChat.getPlayerExact(member);
            if ( p != null ) {
                p.sendMessage(msg);
            }
        }
        
        // ロギング
        if ( LunaChat.config.loggingChat ) {
            LunaChat.log(msg);
        }
    }
}
