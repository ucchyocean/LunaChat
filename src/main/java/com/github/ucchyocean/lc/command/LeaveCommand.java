/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Channel;

/**
 * leaveコマンドの実行クラス
 * @author ucchy
 */
public class LeaveCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = 
            "leave";
    private static final String PERMISSION_NODE = 
            "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = 
            "usageLeave";
    
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
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から退出するチャンネルを取得する
        // 指定が無いならデフォルトの発言先にする
        Player player = (Player) sender;
        Channel def = api.getDefaultChannel(player.getName());
        String channelName = null;
        if ( def != null ) {
            channelName = def.getName();
        }
        if (args.length >= 2) {
            channelName = args[1];
        }

        // グローバルチャンネルなら退出できない
        if ( channelName == null || config.getGlobalChannel().equals(channelName) ) {
            sendResourceMessage(sender, PREERR, "errmsgCannotLeaveGlobal", channelName);
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        if ( !api.isExistChannel(channelName) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // チャンネルのメンバーかどうかを確認する
        Channel channel = api.getChannel(channelName);
        if (!channel.getMembers().contains(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNomember");
            return true;
        }

        // チャンネルから退出する
        channel.removeMember(player.getName());
        sendResourceMessage(sender, PREINFO, "cmdmsgLeave", channelName);
        return true;
    }
}
