/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bukkit.event;

import org.bukkit.command.CommandSender;

/**
 * チャンネル削除イベント
 * @author ucchy
 */
public class LunaChatChannelRemoveEvent extends LunaChatBaseCancellableEvent {

    private CommandSender sender;

    public LunaChatChannelRemoveEvent(String channelName, CommandSender sender) {
        super(channelName);
        this.sender = sender;
    }

    /**
     * チャンネルを削除した人を取得する。
     * @return チャンネルを削除したCommandSender
     */
    public CommandSender getSender() {
        return sender;
    }
}
