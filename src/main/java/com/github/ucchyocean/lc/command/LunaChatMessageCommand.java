/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * 1:1チャット送信コマンド
 * @author ucchy
 */
public class LunaChatMessageCommand implements CommandExecutor {

    private static final String PREERR = Resources.get("errorPrefix");

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        // senderからChannelPlayerを作成する
        ChannelPlayer inviter = ChannelPlayer.getChannelPlayer(sender);

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
                    message.append(args[i] + " ");
                }
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            printUsage(sender, label);
            return true;
        }

        // メッセージを送信する
        sendTellMessage(inviter, invitedName, message.toString().trim());
        return true;
    }

    /**
     * Tellコマンドの実行処理を行う
     * @param inviter
     * @param invitedName
     * @param message
     */
    protected void sendTellMessage(ChannelPlayer inviter, String invitedName, String message) {

        // 招待相手が存在するかどうかを確認する
        ChannelPlayer invited = ChannelPlayer.getChannelPlayer(invitedName);
        if ( invited == null || !invited.isOnline() ) {
            sendResourceMessage(inviter, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return;
        }

        // 招待相手が自分自身でないか確認する
        if (inviter.getName().equals(invited.getName())) {
            sendResourceMessage(inviter, PREERR,
                    "errmsgCannotSendPMSelf");
            return;
        }

        // チャンネルが存在するかどうかをチェックする
        LunaChatAPI api = LunaChat.getAPI();
        String cname = inviter.getName() + ">" + invited.getName();
        Channel channel = api.getChannel(cname);
        if ( channel == null ) {
            // チャンネルを作成して、送信者、受信者をメンバーにする
            channel = api.createChannel(cname);
            channel.setVisible(false);
            channel.addMember(inviter);
            channel.addMember(invited);
            channel.setPrivateMessageTo(invited.getName());
        }

        // メッセージがあるなら送信する
        if ( message.trim().length() > 0 ) {
            channel.chat(inviter, message);
        }

        // 送信履歴を残す
        DataMaps.privateMessageMap.put(
                invited.getName(), inviter.getName());
        DataMaps.privateMessageMap.put(
                inviter.getName(), invited.getName());

        return;
    }

    /**
     * コマンドの使い方を senderに送る
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        sendResourceMessage(sender, "", "usageMessage", label);
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    protected void sendResourceMessage(CommandSender sender, String pre,
            String key, Object... args) {

        String org = Resources.get(key);
        if ( org == null || org.equals("") ) {
            return;
        }
        String msg = String.format(pre + org, args);
        sender.sendMessage(msg);
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    protected void sendResourceMessage(ChannelPlayer cp, String pre,
            String key, Object... args) {

        String org = Resources.get(key);
        if ( org == null || org.equals("") ) {
            return;
        }
        String msg = String.format(pre + org, args);
        cp.sendMessage(msg);
    }
}
