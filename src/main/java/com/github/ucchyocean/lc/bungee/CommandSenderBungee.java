/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import com.github.ucchyocean.lc.CommandSenderInterface;

import net.md_5.bungee.api.CommandSender;

/**
 * コマンド実行者のBungee実装クラス
 * @author ucchy
 */
public class CommandSenderBungee implements CommandSenderInterface {

    private CommandSender sender;

    public CommandSenderBungee(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean isOp() {
        // TODO 要検討
        // adminグループに所属しているかどうかを判定する
        return sender.getGroups().contains("admin");
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPermissionSet(String permission) {
        // TODO 要テスト
        return sender.getPermissions().contains(permission);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }
}
