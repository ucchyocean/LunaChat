/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Resources;

/**
 * @author ucchy
 * 1:1チャット受信コマンド
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

        // メッセージを取得する
        StringBuilder message = new StringBuilder();
        if ( args.length >= 1 ) {
            for ( int i=0; i<args.length; i++ ) {
                message.append(args[i]);
            }
        }

        // 招待した人を履歴から取得する
        String invitedName = DataMaps.privateMessageMap.get(inviter.getName());
        if ( invitedName == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundPM");
            return true;
        }

        sendTellMessage(inviter, invitedName, message.toString());

        return true;
    }
}
