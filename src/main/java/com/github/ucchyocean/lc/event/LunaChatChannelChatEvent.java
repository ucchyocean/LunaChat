/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

/**
 * @author ucchy
 * チャンネルチャットのチャットイベント
 */
public class LunaChatChannelChatEvent extends LunaChatBaseEvent {

    private String originalMessage;
    private String ngMaskedMessage;
    private String messageFormat;

    public LunaChatChannelChatEvent(String channelName,
            String originalMessage, String ngMaskedMessage,
            String messageFormat) {
        super(channelName);
        this.originalMessage = originalMessage;
        this.ngMaskedMessage = ngMaskedMessage;
        this.messageFormat = messageFormat;
    }

    public String getPreReplaceMessage() {
        return originalMessage;
    }

    public String getNgMaskedMessage() {
        return ngMaskedMessage;
    }


    public String getMessageFormat() {
        return messageFormat;
    }
    
    public void setNgMaskedMessage(String ngMaskedMessage) {
        this.ngMaskedMessage = ngMaskedMessage;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
