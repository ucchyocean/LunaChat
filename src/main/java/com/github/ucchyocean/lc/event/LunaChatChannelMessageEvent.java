/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.ArrayList;

import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * チャンネルチャットのメッセージイベント、
 * このイベントはキャンセルできない。
 * @author ucchy
 */
public class LunaChatChannelMessageEvent extends LunaChatBaseEvent {

    private ChannelPlayer player;
    private String message;
    private ArrayList<ChannelPlayer> recipients;

    public LunaChatChannelMessageEvent(String channelName,
            ChannelPlayer player, String message, ArrayList<ChannelPlayer> recipients) {
        super(channelName);
        this.player = player;
        this.message = message;
        this.recipients = recipients;
    }

    /**
     * 発言したプレイヤー、システムメッセージの場合はnullになることに注意
     * @return player 発言プレイヤー
     */
    public ChannelPlayer getPlayer() {
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
    public ArrayList<ChannelPlayer> getRecipients() {
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
    public void setRecipients(ArrayList<ChannelPlayer> recipients) {
        this.recipients = recipients;
    }
}
