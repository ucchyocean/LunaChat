/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.event;

import java.util.List;
import java.util.Map;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * イベントの実行結果を格納するクラス
 * @author ucchy
 */
public class EventResult {

    private boolean isCancelled = false;
    private String message;
    private String messageFormat;
    private String ngMaskedMessage;
    private String channelName;
    private List<ChannelMember> recipients;
    private String japanized;
    private Map<String, String> options;

    /**
     * @return
     */
    public Channel getChannel() {
        return LunaChat.getAPI().getChannel(channelName);
    }

    /**
     * @param cancelled
     */
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    /**
     * @return
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return messageFormat
     */
    public String getMessageFormat() {
        return messageFormat;
    }

    /**
     * @param messageFormat messageFormat
     */
    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    /**
     * @return ngMaskedMessage
     */
    public String getNgMaskedMessage() {
        return ngMaskedMessage;
    }

    /**
     * @param ngMaskedMessage ngMaskedMessage
     */
    public void setNgMaskedMessage(String ngMaskedMessage) {
        this.ngMaskedMessage = ngMaskedMessage;
    }

    /**
     * @return channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * @param channelName channelName
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * @return recipients
     */
    public List<ChannelMember> getRecipients() {
        return recipients;
    }

    /**
     * @param recipients recipients
     */
    public void setRecipients(List<ChannelMember> recipients) {
        this.recipients = recipients;
    }

    /**
     * @return japanized
     */
    public String getJapanized() {
        return japanized;
    }

    /**
     * @param japanized japanized
     */
    public void setJapanized(String japanized) {
        this.japanized = japanized;
    }

    /**
     * @return options
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * @param options options
     */
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}