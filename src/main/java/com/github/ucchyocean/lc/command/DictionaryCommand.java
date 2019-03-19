/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.command;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * dictionaryコマンドの実行クラス
 * @author ucchy
 */
public class DictionaryCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "dictionary";
    private static final String PERMISSION_NODE = "lunachat-admin." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageDictionary";

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
    public boolean runCommand(CommandSender sender, String label, String[] args) {

        // 引数チェック
        // このコマンドは、コンソールでも実行できる
        if ( args.length <= 1 ) {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            sendUsageMessage(sender, label);
            return true;
        }

        if ( !args[1].equalsIgnoreCase("add") &&
                !args[1].equalsIgnoreCase("remove") &&
                !args[1].equalsIgnoreCase("view") ) {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            sendUsageMessage(sender, label);
            return true;
        }

        if ( args[1].equalsIgnoreCase("add") ) {

            // addの場合は、さらに2つ引数が必要
            if ( args.length <= 3 ) {
                sendResourceMessage(sender, PREERR, "errmsgCommand");
                sendUsageMessage(sender, label);
                return true;
            }

            String key = args[2];
            String value = args[3];
            api.setDictionary(key, value);

            sendResourceMessage(sender, PREINFO, "cmdmsgDictionaryAdd", key, value);
            return true;

        } else if ( args[1].equalsIgnoreCase("remove") ) {

            // removeの場合は、さらに1つ引数が必要
            if ( args.length <= 2 ) {
                sendResourceMessage(sender, PREERR, "errmsgCommand");
                sendUsageMessage(sender, label);
                return true;
            }

            String key = args[2];
            api.removeDictionary(key);

            sendResourceMessage(sender, PREINFO, "cmdmsgDictionaryRemove", key);
            return true;

        } else if ( args[1].equalsIgnoreCase("view") ) {

            HashMap<String, String> dic = api.getAllDictionary();
            for ( String key : dic.keySet() ) {
                String value = dic.get(key);
                sender.sendMessage(key + ChatColor.GRAY + " -> " + ChatColor.WHITE + value);
            }
            return true;

        }

        return true;
    }

}
