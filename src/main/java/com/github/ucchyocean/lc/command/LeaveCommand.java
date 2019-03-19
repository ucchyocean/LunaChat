/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * leaveコマンドの実行クラス
 * @author ucchy
 */
public class LeaveCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "leave";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageLeave";

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
     * コマンドの種別を取得します。
     * @return コマンド種別
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandType()
     */
    @Override
    public CommandType getCommandType() {
        return CommandType.USER;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#sendUsageMessage(org.bukkit.command.CommandSender, java.lang.String)
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
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#runCommand(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から退出するチャンネルを取得する
        // 指定が無いならデフォルトの発言先にする
        ChannelPlayer player = ChannelPlayer.getChannelPlayer(sender);
        Channel def = api.getDefaultChannel(player.getName());
        String channelName = null;
        if ( def != null ) {
            channelName = def.getName();
        }
        if (args.length >= 2) {
            channelName = args[1];
        }

        Channel channel = api.getChannel(channelName);

        // チャンネルが存在するかどうかをチェックする
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        channelName = channel.getName();

        // 退室権限を確認する
        String node = PERMISSION_NODE + "." + channelName;
        if (sender.isPermissionSet(node) && !sender.hasPermission(node)) {
            sendResourceMessage(sender, PREERR, "errmsgPermission",
                    PERMISSION_NODE + "." + channelName);
            return true;
        }

        // グローバルチャンネルなら退出できない
        if ( channel.isGlobalChannel() ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotLeaveGlobal", channelName);
            return true;
        }

        // 強制参加チャンネルなら退出できない
        if ( channel.isForceJoinChannel() ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotLeaveForceJoin", channelName);
            return true;
        }

        // チャンネルのメンバーかどうかを確認する
        if (!channel.getMembers().contains(player)) {
            sendResourceMessage(sender, PREERR, "errmsgNomember");
            return true;
        }

        // チャンネルから退出する
        channel.removeMember(player);
        sendResourceMessage(sender, PREINFO, "cmdmsgLeave", channelName);
        return true;
    }
}
