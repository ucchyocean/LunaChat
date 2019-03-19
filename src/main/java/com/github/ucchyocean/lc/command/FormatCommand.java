/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.channel.Channel;

/**
 * formatコマンドの実行クラス
 * @author ucchy
 */
public class FormatCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "format";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageFormat";

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
        return CommandType.MODERATOR;
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
        String format = "";
        String cname = null;
        if ( player != null && args.length >= 2 ) {
            Channel def = api.getDefaultChannel(player.getName());
            if ( def != null ) {
                cname = def.getName();
            }
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
        Channel channel = api.getChannel(cname);
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // 制約キーワードを確認する
        List<String> constraints = config.getFormatConstraint();
        String tempFormat = new String(format);
        for ( int i=0; i<=9; i++ ) {
            String key = "%" + i;
            if ( tempFormat.contains(key) && api.getTemplate(i + "") != null ) {
                tempFormat = tempFormat.replace(key, api.getTemplate("" + i));
                break;
            }
        }
        for ( String constraint : constraints ) {
            if ( !tempFormat.contains(constraint) ) {
                sendResourceMessage(sender, PREERR, "errmsgFormatConstraint", constraint);
                return true;
            }
        }

        // フォーマットの設定
        channel.setFormat(format);
        sendResourceMessage(sender, PREINFO, "cmdmsgFormat", format);
        channel.save();
        return true;
    }
}
