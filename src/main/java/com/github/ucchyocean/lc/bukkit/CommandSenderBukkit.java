/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bukkit;

import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.CommandSenderInterface;

/**
 * コマンド実行者のBukkit実装クラス
 * @author ucchy
 */
public class CommandSenderBukkit implements CommandSenderInterface {

    private CommandSender sender;

    public CommandSenderBukkit(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    @Override
    public boolean isOp() {
        return sender.isOp();
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return sender.isPermissionSet(permission);
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }
}
