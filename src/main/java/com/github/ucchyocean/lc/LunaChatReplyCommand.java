/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



/**
 * @author ucchy
 * 1:1チャット受信コマンド
 */
public class LunaChatReplyCommand implements CommandExecutor {

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
        Player invited = (Player)sender;

        // メッセージを取得する
        StringBuilder message = new StringBuilder();
        if ( args.length >= 1 ) {
            for ( int i=0; i<args.length; i++ ) {
                message.append(args[i]);
            }
        }

        // 招待した人を履歴から取得する
        String inviterName = LunaChat.privateMessageMap.get(invited.getName());
        if ( inviterName == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundPM");
            return true;
        }
        LunaChat.privateMessageMap.remove(invited.getName());

        // デフォルトの発言先が異なる場合は、デフォルトの発言先にする
        Channel def = LunaChat.manager.getDefaultChannel(invited.getName());
        String cname = inviterName + ">" + invited.getName();
        if ( def == null || !cname.equals(def.getName()) ) {
            LunaChat.manager.setDefaultChannel(invited.getName(), cname);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", cname);
        }

        // メッセージがあるなら送信する
        if ( message.toString().trim().length() > 0 ) {
            Channel channel = LunaChat.manager.getChannel(cname);
            channel.chat(invited, message.toString());
        }

        return true;
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
