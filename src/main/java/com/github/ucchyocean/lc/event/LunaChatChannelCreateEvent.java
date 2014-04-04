/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import org.bukkit.command.CommandSender;

/**
 * チャンネル作成イベント
 * @author ucchy
 */
public class LunaChatChannelCreateEvent extends LunaChatBaseCancellableEvent {

    private CommandSender sender;

    public LunaChatChannelCreateEvent(String channelName, CommandSender sender) {
        super(channelName);
        this.sender = sender;
    }

    /**
     * 作成するチャンネルのチャンネル名を上書き設定する
     * @param channelName 設定するチャンネル名
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * チャンネルを作成した人を取得する。
     * @return チャンネルを作成したCommandSender
     */
    public CommandSender getSender() {
        return sender;
    }
}
