/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author ucchy
 * チャンネル
 */
public class Channel {

    private static final String INFO_FIRSTLINE = Resources.get("channelInfoFirstLine");
    private static final String INFO_PREFIX = Resources.get("channelInfoPrefix");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");
    
    private static final String DEFAULT_FORMAT = Resources.get("defaultFormat");
    private static final String MSG_JOIN = Resources.get("joinMessage");
    private static final String MSG_QUIT = Resources.get("quitMessage");
    
    /** 参加者 */
    protected List<String> members;
    
    /** チャンネルモデレータ */
    protected List<String> moderator;
    
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
        this(name, description, new ArrayList<String>());
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
        this.moderator = new ArrayList<String>();
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
            moderator.add(name);
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
        
        // デフォルト発言先が退出するチャンネルと一致する場合、
        // デフォルト発言先を削除する
        String def = LunaChat.manager.getDefault(name);
        if ( def != null && def.equals(this.name) ) {
            LunaChat.manager.removeDefault(name);
        }
        
        // 実際にメンバーから削除する
        if ( members.contains(name) ) {
            members.remove(name);
            sendJoinQuitMessage(false, name);
            if ( LunaChat.config.zeroMemberRemove && members.size() <= 0 ) {
                LunaChat.manager.removeChannel(this.name);
                return;
            } else {
                LunaChat.manager.save();
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
    
    /**
     * チャンネル情報を返す
     * @return チャンネル情報
     */
    protected ArrayList<String> getInfo() {
        
        ArrayList<String> info = new ArrayList<String>();
        info.add(Utility.replaceColorCode(INFO_FIRSTLINE));
        
        // メンバーの人数を数える
        int onlineNum = 0;
        for ( String pname : members ) {
            if ( isOnline(pname) ) {
                onlineNum++;
            }
        }
        int memberNum = members.size();
        
        info.add( String.format(
                Utility.replaceColorCode(LIST_FORMAT), 
                name, onlineNum, memberNum, description) );
        
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
            if ( isOnline(members.get(i)) ) {
                disp = ChatColor.WHITE + name;
            } else {
                disp = ChatColor.GRAY + name;
            }
            buf.append(disp + ",");
        }
        
        info.add(buf.toString());
        info.add(Utility.replaceColorCode(LIST_ENDLINE));
        
        return info;
    }
    
    /**
     * 指定された名前のプレイヤーがオンラインかどうかを確認する
     * @param name プレイヤー名
     * @return オンラインかどうか
     */
    private boolean isOnline(String name) {
        Player p = LunaChat.getPlayerExact(name);
        return ( p != null && p.isOnline() );
    }
}
