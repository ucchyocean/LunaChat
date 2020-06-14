/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee.event;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.channel.Channel;

import net.md_5.bungee.api.plugin.Event;

/**
 * 基底イベントクラス
 * @author ucchy
 */
public abstract class LunaChatBungeeBaseEvent extends Event {

    protected String channelName;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     */
    public LunaChatBungeeBaseEvent(String channelName) {
        this.channelName = channelName;
    }

    /**
     * チャンネル名を取得する
     * @return チャンネル名
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * チャンネルを取得する
     * @return チャンネル
     */
    public Channel getChannel() {
        return LunaChat.getAPI().getChannel(channelName);
    }
}
