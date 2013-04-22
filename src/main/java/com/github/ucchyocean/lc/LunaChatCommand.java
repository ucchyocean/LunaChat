/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author ucchy
 * Lunachatコマンドの処理クラス
 */
public class LunaChatCommand implements CommandExecutor {

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

    private static final String[] USAGE_KEYS = {
        "usageJoin", "usageLeave", "usageList", "usageInvite", "usageAccept", 
        "usageDeny", "usageKick", "usageBan", "usagePardon",
    };

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        if (args.length == 0) {
            printUsage(sender, label);
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            return doJoin(sender, args);
        } else if (args[0].equalsIgnoreCase("leave")) {
            return doLeave(sender, args);
        } else if (args[0].equalsIgnoreCase("list")) {
            return doList(sender, args);
        } else if (args[0].equalsIgnoreCase("invite")) {
            return doInvite(sender, args);
        } else if (args[0].equalsIgnoreCase("accept")) {
            return doAccept(sender, args);
        } else if (args[0].equalsIgnoreCase("deny")) {
            return doDeny(sender, args);
        } else if (args[0].equalsIgnoreCase("kick")) {
            return doKick(sender, args);
        } else if (args[0].equalsIgnoreCase("ban")) {
            return doBan(sender, args);
        } else if (args[0].equalsIgnoreCase("pardon")) {
            return doPardon(sender, args);
        } else {
            return doJoin(sender, args);
        }
    }

    /**
     * チャンネルへの参加をする
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doJoin(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、参加するチャンネルを取得する
        String channelName = "";
        StringBuilder message = new StringBuilder();
        if (!args[0].equalsIgnoreCase("join")) {
            channelName = args[0];
            if (args.length >= 2) {
                for (int i = 1; i < args.length; i++) {
                    message.append(args[i] + " ");
                }
            }
        } else if (args.length >= 2) {
            channelName = args[1];
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    message.append(args[i] + " ");
                }
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(channelName)) {
            if (LunaChat.config.createChannelOnJoinCommand) {
                // 存在しないチャットには、チャンネルを作って入る設定の場合

                // チャンネル作成
                Channel c = LunaChat.manager.createChannel(channelName, "");
                c.addMember(((Player) sender).getName());
                sendResourceMessage(sender, PREINFO, "cmdmsgCreate", channelName);
                return true;

            } else {
                // 存在しないチャットには入れない設定の場合

                sendResourceMessage(sender, PREERR, "errmsgNotExist");
                return true;
            }
        }

        // デフォルト発言先をチェックする
        Player player = (Player) sender;
        Channel channel = LunaChat.manager.getChannel(channelName);

        // BANされていないか確認する
        if (channel.banned.contains(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgBanned");
            return true;
        }

        if (channel.members.contains(player.getName())) {

            // 何かメッセージがあるなら、そのままチャット送信する
            if (message.length() > 0) {
                channel.chat(player, message.toString());
                return true;
            }

            // デフォルトの発言先に設定する
            LunaChat.manager.setDefaultChannel(player.getName(), channelName);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", channelName);

        } else {
            // チャンネルに参加し、デフォルトの発言先に設定する
            channel.addMember(player.getName());
            sendResourceMessage(sender, PREINFO, "cmdmsgJoin", channelName);
            LunaChat.manager.setDefaultChannel(player.getName(), channelName);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", channelName);
        }

        return true;
    }

    /**
     * チャンネルから退出する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doLeave(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、参加するチャンネルを取得する
        String channel = "";
        if (args.length >= 2) {
            channel = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(channel)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // チャンネルのメンバーかどうかを確認する
        Player player = (Player) sender;
        Channel c = LunaChat.manager.getChannel(channel);
        if (!c.members.contains(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNomember");
            return true;
        }

        // チャンネルから退出する
        c.removeMember(player.getName());
        sendResourceMessage(sender, PREINFO, "cmdmsgLeave", channel);
        return true;
    }

    /**
     * チャンネルをリスト表示する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doList(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // リストを取得して表示する
        ArrayList<String> list = LunaChat.manager.getList(player);
        for (String msg : list) {
            sender.sendMessage(msg);
        }
        return true;
    }

    /**
     * 招待を送信する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doInvite(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、参加するチャンネル、招待する人を取得する
        String channelName = "";
        String invitedName = "";
        if (args.length >= 3) {
            channelName = args[1];
            invitedName = args[2];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(channelName)) {
            sender.sendMessage(Utility.replaceColorCode(PREERR
                    + Resources.get("errmsgNotExist")));
            return true;
        }

        // チャンネルのメンバーかどうかを確認する
        Player inviter = (Player) sender;
        Channel channel = LunaChat.manager.getChannel(channelName);
        if (!channel.members.contains(inviter.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNomember");
            return true;
        }

        // 招待相手が存在するかどうかを確認する
        Player invited = LunaChat.getPlayerExact(invitedName);
        if (invited == null) {
            sendResourceMessage(sender, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return true;
        }

        // 招待相手が既にチャンネルに参加しているかどうかを確認する
        if (channel.members.contains(invitedName)) {
            sendResourceMessage(sender, PREERR,
                    "errmsgInvitedAlreadyExist", invitedName);
            return true;
        }

        // 招待を送信する
        LunaChat.inviteMap.put(invitedName, channelName);
        LunaChat.inviterMap.put(invitedName, inviter.getName());

        sendResourceMessage(sender, PREINFO,
                "cmdmsgInvite", invitedName, channelName);
        sendResourceMessage(invited, PREINFO,
                "cmdmsgInvited1", inviter.getName(), channelName);
        sendResourceMessage(invited, PREINFO, "cmdmsgInvited2");
        return true;
    }

    /**
     * 招待を受けて、チャンネルチャットへ入室する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doAccept(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 招待を受けていないプレイヤーなら、エラーを表示して終了する
        Player player = (Player) sender;
        if (!LunaChat.inviteMap.containsKey(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNotInvited");
            return true;
        }

        // チャンネルを取得して、招待記録を消去する
        String channelName = LunaChat.inviteMap.get(player.getName());
        Channel channel = LunaChat.manager.getChannel(channelName);
        LunaChat.inviteMap.remove(player.getName());
        LunaChat.inviterMap.remove(player.getName());

        // 取得できなかったらエラー終了する
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundChannel");
            return true;
        }

        // 既に参加しているなら、エラーを表示して終了する
        if (channel.members.contains(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgInvitedAlreadyJoin");
            return true;
        }

        // 参加する
        channel.addMember(player.getName());
        sendResourceMessage(sender, PREINFO, "cmdmsgJoin", channel.name);

        return true;
    }

    /**
     * 招待を拒否する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doDeny(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 招待を受けていないプレイヤーなら、エラーを表示して終了する
        Player player = (Player) sender;
        if (!LunaChat.inviteMap.containsKey(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNotInvited");
            return true;
        }

        // 招待者を取得して、招待記録を消去する
        String inviterName = LunaChat.inviterMap.get(player.getName());
        LunaChat.inviteMap.remove(player.getName());
        LunaChat.inviterMap.remove(player.getName());

        // メッセージ送信
        sendResourceMessage(sender, PREINFO, "cmdmsgDeny");
        Player inviter = LunaChat.getPlayerExact(inviterName);
        if (inviter != null) {
            sendResourceMessage(inviter, PREINFO, "cmdmsgDenyed");
        }
        return true;
    }

    /**
     * チャンネルから指定したユーザーをキックする
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doKick(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、キックするユーザーを取得する
        String kickedName = "";
        if (args.length >= 2) {
            kickedName = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // デフォルト参加チャンネルを取得、取得できない場合はエラー表示して終了する
        Player kicker = (Player) sender;
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(kicker.getName());
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // モデレーターかどうか確認する
        if (!kicker.getName().equals(channel.moderator)) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // キックされるプレイヤーがメンバーかどうかチェックする
        if (!channel.members.contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }

        // キック実行
        channel.removeMember(kickedName);
        sendResourceMessage(sender, PREINFO,
                "cmdmsgKick", kickedName, channel.name);

        Player kicked = LunaChat.getPlayerExact(kickedName);
        if (kicked != null) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgKicked", channel.name);
        }

        return true;
    }

    /**
     * チャンネルから指定したユーザーをBANする
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doBan(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、BANするユーザーを取得する
        String kickedName = "";
        if (args.length >= 2) {
            kickedName = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // デフォルト参加チャンネルを取得、取得できない場合はエラー表示して終了する
        Player kicker = (Player) sender;
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(kicker.getName());
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // モデレーターかどうか確認する
        if (!kicker.getName().equals(channel.moderator)) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // BANされるプレイヤーがメンバーかどうかチェックする
        if (!channel.members.contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }

        // BAN実行
        Player kicked = LunaChat.getPlayerExact(kickedName);
        channel.banned.add(kickedName);
        channel.removeMember(kickedName);

        sendResourceMessage(sender, PREINFO,
                "cmdmsgBan", kickedName, channel.name);
        if (kicked != null) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgBanned", channel.name);
        }

        return true;
    }

    /**
     * 指定したプレイヤーのBANを解除する
     * 
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doPardon(CommandSender sender, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、BAN解除するユーザーを取得する
        String kickedName = "";
        if (args.length >= 2) {
            kickedName = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // デフォルト参加チャンネルを取得、取得できない場合はエラー表示して終了する
        Player kicker = (Player) sender;
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(kicker.getName());
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // モデレーターかどうか確認する
        if (!kicker.getName().equals(channel.moderator)) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // BAN解除されるプレイヤーがBANされているかどうかチェックする
        if (!channel.banned.contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgNotBanned");
            return true;
        }

        // BAN解除実行
        Player kicked = LunaChat.getPlayerExact(kickedName);
        channel.banned.remove(kickedName);

        sendResourceMessage(sender, PREINFO,
                "cmdmsgPardon", kickedName, channel.name);
        if (kicked != null) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgPardoned", channel.name);
        }

        return true;
    }
    
    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        for (String key : USAGE_KEYS) {
            sendResourceMessage(sender, "", key, label);
        }
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
