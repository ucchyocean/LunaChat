/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee.event;

import java.util.ArrayList;
import java.util.List;

import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * チャンネルチャットのメッセージイベント、
 * このイベントはキャンセルできない。
 * @author ucchy
 */
public class LunaChatBungeeChannelMessageEvent extends LunaChatBungeeBaseEvent {

    private ChannelMember member;
    private String message;
    private List<ChannelMember> recipients;
    private String displayName;
    private String originalMessage;

    public LunaChatBungeeChannelMessageEvent(String channelName,
            ChannelMember member, String message, List<ChannelMember> recipients,
            String displayName, String originalMessage) {
        super(channelName);
        this.member = member;
        this.message = message;
        this.recipients = recipients;
        this.displayName = displayName;
        this.originalMessage = originalMessage;
    }

    /**
     * 発言したプレイヤー、システムメッセージの場合はnullになることに注意
     * @return player 発言プレイヤー
     */
    public ChannelMember getMember() {
        return member;
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
    public List<ChannelMember> getRecipients() {
        return recipients;
    }

    /**
     * 発言者の表示名を取得する
     * @return 発言者の表示名
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * オリジナルメッセージ（チャットフォーマットを適用していない状態のメッセージ）を取得する
     * @return オリジナルメッセージ
     */
    public String getOriginalMessage() {
        return originalMessage;
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
    public void setRecipients(ArrayList<ChannelMember> recipients) {
        this.recipients = recipients;
    }
}
