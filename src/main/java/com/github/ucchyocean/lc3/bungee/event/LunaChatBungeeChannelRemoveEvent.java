/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee.event;

import net.md_5.bungee.api.CommandSender;

/**
 * チャンネル削除イベント
 * @author ucchy
 */
public class LunaChatBungeeChannelRemoveEvent extends LunaChatBungeeBaseCancellableEvent {

    private CommandSender sender;

    public LunaChatBungeeChannelRemoveEvent(String channelName, CommandSender sender) {
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
