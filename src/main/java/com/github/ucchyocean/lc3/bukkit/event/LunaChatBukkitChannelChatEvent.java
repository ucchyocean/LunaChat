/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bukkit.event;

import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * チャンネルチャットのチャットイベント
 * @author ucchy
 */
public class LunaChatBukkitChannelChatEvent extends LunaChatBukkitBaseCancellableEvent {

    private ChannelMember member;
    private String originalMessage;
    private String ngMaskedMessage;
    private String messageFormat;

    public LunaChatBukkitChannelChatEvent(String channelName, ChannelMember member,
            String originalMessage, String ngMaskedMessage,
            String messageFormat) {
        super(channelName);
        this.member = member;
        this.originalMessage = originalMessage;
        this.ngMaskedMessage = ngMaskedMessage;
        this.messageFormat = messageFormat;
    }

    /**
     * 発言を行ったプレイヤーを取得します。
     * @return 発言したプレイヤー
     */
    public ChannelMember getMember() {
        return member;
    }

    /**
     * 置き換え前の、発言されたままのテキストをかえす
     * @return 発言内容
     */
    public String getPreReplaceMessage() {
        return originalMessage;
    }

    /**
     * NGワードがマスクされた後のテキストをかえす
     * @return NGワードマスク済みの発言内容
     */
    public String getNgMaskedMessage() {
        return ngMaskedMessage;
    }

    /**
     * メッセージに適用されるフォーマットをかえす
     * @return フォーマット
     */
    public String getMessageFormat() {
        return messageFormat;
    }

    /**
     * NGワードマスク後のテキストを上書き設定する
     * @param ngMaskedMessage 上書きする発言内容
     */
    public void setNgMaskedMessage(String ngMaskedMessage) {
        this.ngMaskedMessage = ngMaskedMessage;
    }

    /**
     * メッセージフォーマットを上書き設定する
     * @param messageFormat フォーマット
     */
    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
