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
        if ( !api.isExistChannel(cname) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // モデレーターかどうか確認する
        Channel channel = api.getChannel(cname);
        if ( player != null ) {
            if ( !channel.getModerator().contains(player.getName()) && !player.isOp()) {
                sendResourceMessage(sender, PREERR, "errmsgNotModerator");
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
