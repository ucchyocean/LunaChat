/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.ArrayList;

import org.bukkit.entity.Player;

/**
 * チャンネルチャットのメッセージイベント、
 * このイベントはキャンセルできない。
 * @author ucchy
 */
public class LunaChatChannelMessageEvent extends LunaChatBaseEvent {

    private Player player;
    private String message;
    private ArrayList<Player> recipients;
    
    public LunaChatChannelMessageEvent(String channelName,
            Player player, String message, ArrayList<Player> recipients) {
        super(channelName);
        this.player = player;
        this.message = message;
        this.recipients = recipients;
    }

    /**
     * 発言したプレイヤー、システムメッセージの場合はnullになることに注意
     * @return player 発言プレイヤー
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 置き換えされたメッセージ
     * @return message メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * メッセージを受信するプレイヤーリスト
     * @return recipients プレイヤーリスト
     */
    public ArrayList<Player> getRecipients() {
        return recipients;
    }

    /**
     * メッセージを上書き設定する
     * @param message メッセージ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * メッセージ受信者を上書き設定する
     * @param recipients メッセージ受信者
     */
    public void setRecipients(ArrayList<Player> recipients) {
        this.recipients = recipients;
    }
}
