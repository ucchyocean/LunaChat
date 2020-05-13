/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.member.ChannelMember;

/**
 * 1:1チャット受信コマンド
 * @author ucchy
 */
public class LunaChatReplyCommand extends LunaChatMessageCommand {

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        // senderからChannelMemberを作成する
        ChannelMember inviter = ChannelMember.getChannelMember(sender);

        // 会話相手を履歴から取得する
        String invitedName = DataMaps.privateMessageMap.get(inviter.getName());

        // 引数が無ければ、現在の会話相手を表示して終了する
        if (args.length == 0) {
            if ( invitedName == null ) {
                sendResourceMessage(sender, PREINFO,
                        "cmdmsgReplyInviterNone", inviter.getName());
            } else {
                sendResourceMessage(sender, PREINFO,
                        "cmdmsgReplyInviter", inviter.getName(), invitedName);
            }
            return true;
        }

        // 会話相手がからっぽなら、コマンドを終了する。
        if ( invitedName == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundPM");
            return true;
        }

        // メッセージを取得する
        StringBuilder message = new StringBuilder();
        for ( int i=0; i<args.length; i++ ) {
            message.append(args[i] + " ");
        }

        sendTellMessage(inviter, invitedName, message.toString().trim());

        return true;
    }
}
