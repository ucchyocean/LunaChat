/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;

/**
 * templateコマンドの実行クラス
 * @author ucchy
 */
public class TemplateCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "template";
    private static final String PERMISSION_NODE = "lunachat-admin." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageTemplate";

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
        return CommandType.ADMIN;
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

        // 引数チェック
        // このコマンドは、コンソールでも実行できる
        if ( args.length <= 1 ) {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        if ( !args[1].matches("[0-9]") ) {
            sendResourceMessage(sender, PREERR, "errmsgInvalidTemplateNumber");
            sendResourceMessage(sender, PREERR, "usageTemplate");
            return true;
        }

        String id = args[1];
        StringBuilder buf = new StringBuilder();
        if ( args.length >= 3 ) {
            for (int i = 2; i < args.length; i++) {
                buf.append(args[i] + " ");
            }
        }
        String format = buf.toString().trim();

        // 登録を実行
        if ( format.equals("") ) {
            api.removeTemplate(id);
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgTemplateRemove", id);
        } else {
            api.setTemplate(id, format);
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgTemplate", id, format);
        }

        return true;

    }
}
