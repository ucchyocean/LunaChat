/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * infoコマンドの実行クラス
 * @author ucchy
 */
public class InfoCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "info";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageInfo";

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

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        String cname = null;
        if ( player != null && args.length <= 1 ) {
            Channel def = api.getDefaultChannel(player.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        } else if ( args.length >= 2 ) {
            cname = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうか確認する
        Channel channel = api.getChannel(cname);
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // BANされていないかどうか確認する
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
        if ( player != null && channel.getBanned().contains(cp) ) {
            sendResourceMessage(sender, PREERR, "errmsgBanned");
            return true;
        }

        // チャンネルモデレーターかどうか確認する
        boolean isModerator = channel.hasModeratorPermission(sender);

        // 情報を取得して表示する
        ArrayList<String> list = channel.getInfo(isModerator);
        for (String msg : list) {
            sender.sendMessage(msg);
        }
        return true;
    }
}
