/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.event;

import com.github.ucchyocean.lc.member.ChannelMember;

/**
 * チャンネルチャットのチャットイベント
 * @author ucchy
 */
public class LunaChatChannelChatEvent extends LunaChatBaseCancellableEvent {

    private ChannelMember player;
    private String originalMessage;
    private String ngMaskedMessage;
    private String messageFormat;

    public LunaChatChannelChatEvent(String channelName, ChannelMember player,
            String originalMessage, String ngMaskedMessage,
            String messageFormat) {
        super(channelName);
        this.player = player;
        this.originalMessage = originalMessage;
        this.ngMaskedMessage = ngMaskedMessage;
        this.messageFormat = messageFormat;
    }

    /**
     * 発言を行ったプレイヤーを取得します。
     * @return 発言したプレイヤー
     */
    public ChannelMember getPlayer() {
        return player;
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
