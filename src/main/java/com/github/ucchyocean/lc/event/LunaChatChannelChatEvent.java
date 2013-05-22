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
    private String japanizedMessage;
    private String postReplaceMessage;

    public LunaChatChannelChatEvent(String channelName,
            String originalMessage, String ngMaskedMessage,
            String japanizedMessage, String postReplaceMessage) {
        super(channelName);
        this.originalMessage = originalMessage;
        this.ngMaskedMessage = ngMaskedMessage;
        this.japanizedMessage = japanizedMessage;
        this.postReplaceMessage = postReplaceMessage;
    }

    public String getPreReplaceMessage() {
        return originalMessage;
    }

    public String getNgMaskedMessage() {
        return ngMaskedMessage;
    }


    public String getJapanizedMessage() {
        return japanizedMessage;
    }

    public String getPostReplaceMessage() {
        return postReplaceMessage;
    }

    public void setMessage(String message) {
        this.postReplaceMessage = message;
    }
}
