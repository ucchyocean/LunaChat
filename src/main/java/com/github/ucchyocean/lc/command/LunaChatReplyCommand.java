/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Resources;

/**
 * 1:1チャット受信コマンド
 * @author ucchy
 */
public class LunaChatReplyCommand extends LunaChatMessageCommand {

    private static final String PREERR = Resources.get("errorPrefix");

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }
        Player inviter = (Player)sender;

        // 引数が無ければ、usageを表示して終了する
        if (args.length == 0) {
            printUsage(sender, label);
            return true;
        }

        // メッセージを取得する
        StringBuilder message = new StringBuilder();
        for ( int i=0; i<args.length; i++ ) {
            message.append(args[i] + " ");
        }

        // 招待した人を履歴から取得する
        String invitedName = DataMaps.privateMessageMap.get(inviter.getName());
        if ( invitedName == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundPM");
            return true;
        }

        sendTellMessage(inviter, invitedName, message.toString().trim());

        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        sendResourceMessage(sender, "", "usageReply", label);
    }
}
