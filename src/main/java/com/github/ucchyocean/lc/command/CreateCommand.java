/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.channel.Channel;

/**
 * createコマンドの実行クラス
 * @author ucchy
 */
public class CreateCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "create";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageCreate";

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
        Channel other = api.getChannel(name);
        if ( other != null ) {
            sendResourceMessage(sender, PREERR, "errmsgExist");
            return true;
        }

        // 使用可能なチャンネル名かどうかをチェックする
        if ( !name.matches("[0-9a-zA-Z\\-_]+") ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgCannotUseForChannel", name);
            return true;
        }

        // 最低文字列長を上回っているかをチェックする
        if ( name.length() < config.getMinChannelNameLength() ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgCannotUseForChannelTooShort",
                    name, config.getMinChannelNameLength());
            return true;
        }

        // 最大文字列長を下回っているかをチェックする
        if ( name.length() > config.getMaxChannelNameLength() ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgCannotUseForChannelTooLong",
                    name, config.getMaxChannelNameLength());
            return true;
        }

        // チャンネル作成
        Channel channel = api.createChannel(name);
        if ( channel != null ) {
            channel.setDescription(desc);
            channel.save();
            sendResourceMessage(sender, PREINFO, "cmdmsgCreate", name);
        }
        return true;
    }
}
