/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bukkit.event;

import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc3.channel.Channel;

/**
 * チャンネル作成イベント
 * @author ucchy
 */
public class LunaChatBukkitChannelCreateEvent extends LunaChatBukkitBaseCancellableEvent {

    private CommandSender sender;

    public LunaChatBukkitChannelCreateEvent(String channelName, CommandSender sender) {
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

    /**
     * @deprecated チャンネル作成イベントは、チャンネルを作成する前に呼び出されるので、
     * このメソッドの戻り値は必ずnullになります。
     * @see com.github.ucchyocean.lc3.bukkit.event.LunaChatBukkitBaseEvent#getChannel()
     */
    @Override
    public Channel getChannel() {
        return super.getChannel();
    }
}
