/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.HashMap;

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

    private static final String[] COMMANDS = {
        "join", "leave", "list", "invite", "accept",
        "deny", "kick", "ban", "pardon", "create",
        "remove", "format", "moderator", "option", "reload",
    };

    private static final String[] USAGE_KEYS = {
        "usageJoin", "usageLeave", "usageList", "usageInvite", "usageAccept",
        "usageDeny", "usageKick", "usageBan", "usagePardon", "usageCreate",
        "usageRemove", "usageFormat", "usageModerator", "usageOption",
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

        if (!hasPermission(sender, args)) {
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
        } else if (args[0].equalsIgnoreCase("info")) {
            return doInfo(sender, args);
        } else if (args[0].equalsIgnoreCase("create")) {
            return doCreate(sender, args);
        } else if (args[0].equalsIgnoreCase("remove")) {
            return doRemove(sender, args);
        } else if (args[0].equalsIgnoreCase("format")) {
            return doFormat(sender, args);
        } else if (args[0].equalsIgnoreCase("moderator")) {
            return doModerator(sender, args);
        } else if (args[0].equalsIgnoreCase("option")) {
            return doOption(sender, args);
        } else if (args[0].equalsIgnoreCase("reload")) {
            return doReload(sender, args);
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

        // チャンネルを取得する
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

            // グローバルチャンネルで、何かメッセージがあるなら、そのままチャット送信する
            if (channel.name.equals(LunaChat.config.globalChannel) && message.length() > 0) {
                channel.chat(player, message.toString());
                return true;
            }

            // パスワードが設定されている場合は、パスワードを確認する
            if ( !channel.password.equals("") ) {
                if ( message.toString().trim().equals("") ) {
                    // パスワード空欄
                    sendResourceMessage(sender, PREERR, "errmsgPassword1");
                    sendResourceMessage(sender, PREERR, "errmsgPassword2");
                    sendResourceMessage(sender, PREERR, "errmsgPassword3");
                    return true;
                } else if ( !channel.password.equals(message.toString().trim()) ) {
                    // パスワード不一致
                    sendResourceMessage(sender, PREERR, "errmsgPasswordNotmatch");
                    sendResourceMessage(sender, PREERR, "errmsgPassword2");
                    sendResourceMessage(sender, PREERR, "errmsgPassword3");
                    return true;
                }
            }

            // チャンネルに参加し、デフォルトの発言先に設定する
            if ( !channel.name.equals(LunaChat.config.globalChannel) ) {
                channel.addMember(player.getName());
                sendResourceMessage(sender, PREINFO, "cmdmsgJoin", channelName);
            }
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

        // 実行引数から退出するチャンネルを取得する
        // 指定が無いならデフォルトの発言先にする
        Player player = (Player) sender;
        String channelName = LunaChat.manager.getDefault(player.getName());
        if (args.length >= 2) {
            channelName = args[1];
        }

        // グローバルチャンネルなら退出できない
        if ( LunaChat.config.globalChannel.equals(channelName) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotLeaveGlobal", channelName);
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(channelName)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // チャンネルのメンバーかどうかを確認する
        Channel channel = LunaChat.manager.getChannel(channelName);
        if (!channel.members.contains(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNomember");
            return true;
        }

        // チャンネルから退出する
        channel.removeMember(player.getName());
        sendResourceMessage(sender, PREINFO, "cmdmsgLeave", channelName);
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

        // デフォルトの発言先を取得する
        Player inviter = (Player) sender;
        String channelName = LunaChat.manager.getDefault(inviter.getName());
        if ( channelName == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // 実行引数から招待する人を取得する
        String invitedName = "";
        if (args.length >= 2) {
            invitedName = args[1];
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

        // モデレーターかどうか確認する
        Channel channel = LunaChat.manager.getChannel(channelName);
        if ( !channel.moderator.contains(inviter.getName()) && !inviter.isOp()) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
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
        if (!channel.moderator.contains(kicker.getName()) && !kicker.isOp()) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // グローバルチャンネルならキックできない
        if ( LunaChat.config.globalChannel.equals(channel.name) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotKickGlobal", channel.name);
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
        if (!channel.moderator.contains(kicker.getName()) && !kicker.isOp()) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // グローバルチャンネルならBANできない
        if ( LunaChat.config.globalChannel.equals(channel.name) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotBANGlobal", channel.name);
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
        if (!channel.moderator.contains(kicker.getName()) && !kicker.isOp()) {
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
     * チャンネル情報を表示する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doInfo(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        String cname;
        if ( player != null && args.length <= 1 ) {
            cname = LunaChat.manager.getDefault(player.getName());
        } else if ( args.length >= 2 ) {
            cname = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうか確認する
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // BANされていないかどうか確認する
        if ( channel.banned.contains(player.getName()) ) {
            sendResourceMessage(sender, PREERR, "errmsgBanned");
            return true;
        }

        // 情報を取得して表示する
        ArrayList<String> list = channel.getInfo();
        for (String msg : list) {
            sender.sendMessage(msg);
        }
        return true;
    }

    /**
     * チャンネルを新規作成する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doCreate(CommandSender sender, String[] args) {

        // 実行引数から、作成するチャンネルを取得する
        String name = "";
        String desc = "";
        if (args.length >= 2) {
            name = args[1];
            if (args.length >= 3) {
                desc = args[2];
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (channels.contains(name)) {
            sendResourceMessage(sender, PREERR, "errmsgExist");
            return true;
        }

        // チャンネル作成
        LunaChat.manager.createChannel(name, desc);
        sendResourceMessage(sender, PREINFO, "cmdmsgCreate", name);
        return true;
    }

    /**
     * チャンネルの削除をする
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doRemove(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        String cname;
        if ( player != null && args.length <= 1 ) {
            cname = LunaChat.manager.getDefault(player.getName());
        } else if ( args.length >= 2 ) {
            cname = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうか確認する
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        if ( player != null ) {
            if ( !channel.moderator.contains(player.getName()) && !player.isOp()) {
                sendResourceMessage(sender, PREERR, "errmsgNotModerator");
                return true;
            }
        }

        // グローバルチャンネルなら削除できない
        if ( LunaChat.config.globalChannel.equals(channel.name) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotRemoveGlobal", channel.name);
            return true;
        }

        // チャンネル削除
        LunaChat.manager.removeChannel(cname);
        sendResourceMessage(sender, PREINFO, "cmdmsgRemove", cname);
        return true;
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doFormat(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        String format = "";
        String cname;
        if ( player != null && args.length >= 2 ) {
            cname = LunaChat.manager.getDefault(player.getName());
            for (int i = 1; i < args.length; i++) {
                format = format + args[i] + " ";
            }
        } else if ( args.length >= 3 ) {
            cname = args[1];
            for (int i = 2; i < args.length; i++) {
                format = format + args[i] + " ";
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }
        format = format.trim();

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(cname)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( player != null ) {
            if ( !channel.moderator.contains(player.getName()) && !player.isOp()) {
                sendResourceMessage(sender, PREERR, "errmsgNotModerator");
                return true;
            }
        }

        // フォーマットの設定
        channel.format = format;
        sendResourceMessage(sender, PREINFO, "cmdmsgFormat", format);
        LunaChat.manager.save();
        return true;
    }

    /**
     * モデレーターを設定する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doModerator(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        String cname = "";
        ArrayList<String> moderator = new ArrayList<String>();
        if ( player != null && args.length >= 2 ) {
            cname = LunaChat.manager.getDefault(player.getName());
            for (int i = 1; i < args.length; i++) {
                moderator.add(args[i]);
            }
        } else if ( args.length >= 3 ) {
            cname = args[1];
            for (int i = 2; i < args.length; i++) {
                moderator.add(args[i]);
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(cname)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( player != null ) {
            if ( !channel.moderator.contains(player.getName()) && !player.isOp()) {
                sendResourceMessage(sender, PREERR, "errmsgNotModerator");
                return true;
            }
        }

        // グローバルチャンネルなら設定できない
        if ( LunaChat.config.globalChannel.equals(channel.name) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotModeratorGlobal", channel.name);
            return true;
        }

        // 設定する
        for ( String mod : moderator ) {
            if ( mod.startsWith("-") ) {
                String name = mod.substring(1);
                channel.moderator.remove(name);
                sendResourceMessage(sender, PREINFO,
                        "cmdmsgModeratorMinus", name, cname);
            } else {
                channel.moderator.add(mod);
                sendResourceMessage(sender, PREINFO,
                        "cmdmsgModerator", mod, cname);
            }
        }

        LunaChat.manager.save();
        return true;
    }

    /**
     * チャンネルのオプションを指定する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doOption(CommandSender sender, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        ArrayList<String> optionsTemp = new ArrayList<String>();
        String cname;
        if ( player != null && args.length >= 2 ) {
            cname = LunaChat.manager.getDefault(player.getName());
            for (int i = 1; i < args.length; i++) {
                optionsTemp.add(args[i]);
            }
        } else if ( args.length >= 3 ) {
            cname = args[1];
            for (int i = 2; i < args.length; i++) {
                optionsTemp.add(args[i]);
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(cname)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        Channel channel = LunaChat.manager.getChannel(cname);
        if ( player != null ) {
            if ( !channel.moderator.contains(player.getName()) && !player.isOp()) {
                sendResourceMessage(sender, PREERR, "errmsgNotModerator");
                return true;
            }
        }

        // 指定内容を解析する
        HashMap<String, String> options = new HashMap<String, String>();
        for ( String t : optionsTemp ) {
            int index = t.indexOf("=");
            if ( index == -1 ) {
                continue;
            }
            options.put(t.substring(0, index), t.substring(index + 1));
        }

        // 設定する
        boolean setOption = false;
        if ( options.containsKey("description") ) {
            channel.description = options.get("description");
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgOption", "description", options.get("description"));
            setOption = true;
        }
        if ( !LunaChat.config.globalChannel.equals(channel.name) ) {
            if ( options.containsKey("password") ) {
                channel.password = options.get("password");
                sendResourceMessage(sender, PREINFO,
                        "cmdmsgOption", "password", options.get("password"));
                setOption = true;
            }
            if ( options.containsKey("visible") ) {
                String temp = options.get("visible");
                if ( temp.equalsIgnoreCase("false") ) {
                    channel.visible = false;
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "visible", "false");
                    setOption = true;
                } else if ( temp.equalsIgnoreCase("true") ) {
                    channel.visible = true;
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "visible", "true");
                    setOption = true;
                }
            }
        }

        if ( !setOption ) {
            sendResourceMessage(sender, PREERR, "errmsgInvalidOptions");
        } else {
            LunaChat.manager.save();
        }

        return true;
    }

    /**
     * sender がパーミッションを持っているかどうかを確認する。
     * 持っていなければ、エラーメッセージを表示する。
     *
     * @param sender 実行した人
     * @param args 指定したコマンド
     * @return パーミッションを持っているかどうか
     */
    private boolean hasPermission(CommandSender sender, String[] args) {

        // 第1引数がコマンドに一致するか確認し、一致したらそのパーミッションを確認する
        for ( String c : COMMANDS ) {
            if ( c.equalsIgnoreCase(args[0]) ) {
                boolean permission = sender.hasPermission("lunachat." + c);
                if ( !permission ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgPermission", "lunachat." + c);
                }
                return permission;
            }
        }

        // 第1引数がコマンドでないなら、joinが実行されたとみなして、
        // lunachat.join のパーミッションを確認する
        boolean permission = sender.hasPermission("lunachat.join");
        if ( !permission ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgPermission", "lunachat.join");
        }
        return permission;
    }

    /**
     * config.yml の再読み込みを行う
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doReload(CommandSender sender, String[] args) {

        LunaChat.config.reloadConfig();
        sendResourceMessage(sender, PREINFO, "cmdmsgReload");
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
