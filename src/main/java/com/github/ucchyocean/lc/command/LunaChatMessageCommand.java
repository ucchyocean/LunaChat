/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Channel;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.Utility;


/**
 * @author ucchy
 * 1:1チャット送信コマンド
 */
public class LunaChatMessageCommand implements CommandExecutor {

    private static final String PREINFO = Resources.get("infoPrefix");
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

        // 実行引数から1:1チャットの相手を取得する
        String invitedName = "";
        StringBuilder message = new StringBuilder();
        if (args.length >= 1) {
            invitedName = args[0];
            if ( args.length >= 2 ) {
                for ( int i=1; i<args.length; i++ ) {
                    message.append(args[i]);
                }
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            printUsage(sender, label);
            return true;
        }

        // 招待相手が自分自身でないか確認する
        if (inviter.getName().equals(invitedName)) {
            sendResourceMessage(sender, PREERR,
                    "errmsgCannotSendPMSelf");
            return true;
        }

        // 招待相手が存在するかどうかを確認する
        Player invited = LunaChat.getPlayerExact(invitedName);
        if (invited == null) {
            sendResourceMessage(sender, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        String cname = inviter.getName() + ">" + invitedName;
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( channel == null ) {
            // チャンネルを作成して、送信者、受信者をメンバーにする
            channel = LunaChat.manager.createChannel(cname);
            channel.setVisible(false);
            channel.addMember(inviter.getName());
            channel.addMember(invitedName);
        }

        // デフォルトの発言先が異なる場合は、デフォルトの発言先にする
        Channel def = LunaChat.manager.getDefaultChannel(inviter.getName());
        if ( def == null || !cname.equals(def.getName()) ) {
            LunaChat.manager.setDefaultChannel(inviter.getName(), cname);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", cname);
        }

        // メッセージがあるなら送信する
        if ( message.toString().trim().length() > 0 ) {
            channel.chat(inviter, message.toString());
        }

        // 送信履歴を残す
        LunaChat.privateMessageMap.put(invitedName, inviter.getName());

        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        sendResourceMessage(sender, "", "usageMessage", label);
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     *
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    private void sendResourceMessage(CommandSender sender, String pre,
            String key, Object... args) {
        String msg = String.format(
                Utility.replaceColorCode(pre + Resources.get(key)), args);
        sender.sendMessage(msg);
    }
}
