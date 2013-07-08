/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Channel;

/**
 * inviteコマンドの実行クラス
 * @author ucchy
 */
public class InviteCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "invite";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageInvite";

    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getPermissionNode()
     */
    @Override
    public String getPermissionNode() {
        return PERMISSION_NODE;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#sendUsageMessage()
     */
    @Override
    public void sendUsageMessage(
            CommandSender sender, String label) {
        sendResourceMessage(sender, "", USAGE_KEY, label);
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#runCommand(java.lang.String[])
     */
    @Override
    public boolean runCommand(CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // デフォルトの発言先を取得する
        Player inviter = (Player)sender;
        Channel channel = api.getDefaultChannel(inviter.getName());
        if ( channel == null ) {
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

        // モデレーターかどうか確認する
        if ( !channel.getModerator().contains(inviter.getName()) && !inviter.isOp()) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // 招待相手が存在するかどうかを確認する
        Player invited = Bukkit.getPlayerExact(invitedName);
        if (invited == null) {
            sendResourceMessage(sender, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return true;
        }

        // 招待相手が既にチャンネルに参加しているかどうかを確認する
        if (channel.getMembers().contains(invitedName)) {
            sendResourceMessage(sender, PREERR,
                    "errmsgInvitedAlreadyExist", invitedName);
            return true;
        }

        // 招待を送信する
        DataMaps.inviteMap.put(invitedName, channel.getName());
        DataMaps.inviterMap.put(invitedName, inviter.getName());

        sendResourceMessage(sender, PREINFO,
                "cmdmsgInvite", invitedName, channel.getName());
        sendResourceMessage(invited, PREINFO,
                "cmdmsgInvited1", inviter.getName(), channel.getName());
        sendResourceMessage(invited, PREINFO, "cmdmsgInvited2");
        return true;
    }
}
