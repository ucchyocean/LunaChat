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
    
    /** チャンネルモデレータ */
    protected String moderator;
    
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
        this.moderator = "";
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
        this.moderator = "";
    }
    
    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    protected void chat(Player player, String message) {
        
        // Japanize変換
        if ( LunaChat.config.displayJapanize ) {
            // 2byteコードを含まない場合にのみ、処理を行う
            if ( message.getBytes().length == message.length() ) {
                String kana = KanaConverter.conv(message);
                message = message + "(" + kana + ")";
            }
        }
        
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
        
        if ( members.size() == 0 ) {
            moderator = name;
        }
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
        
        if ( moderator.equals(name) ) {
            if ( members.size() > 0 ) {
                String last = moderator;
                moderator = members.get(0);
                sendInformation(String.format(
                        Utility.replaceColorCode(
                                Resources.get("moderatorChangedMessage")),
                        this.name, last, moderator));
            } else {
                moderator = "";
            }
        }
        if ( members.contains(name) ) {
            members.remove(name);
            sendJoinQuitMessage(false, name);
            if ( LunaChat.config.zeroMemberRemove && members.size() <= 0 ) {
                LunaChat.manager.removeChannel(this.name);
            } else {
                LunaChat.manager.save();
            }
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
        
        sendInformation(msg);
    }
    
    /**
     * 情報をチャンネルメンバーに流します。
     * @param message メッセージ
     */
    private void sendInformation(String message) {
        
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
}
