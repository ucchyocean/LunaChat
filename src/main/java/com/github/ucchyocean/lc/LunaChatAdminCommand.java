/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author ucchy
 * LunachatAdminコマンドの処理クラス
 */
public class LunaChatAdminCommand implements CommandExecutor {

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");
    
    private static final String[] USAGE_KEYS = {
        "usageReload", "usageCreate", "usageRemove", "usageFormat"
    };

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {

        if ( args.length == 0 ) {
            printUsage(sender, label);
            return true;
        }
        
        if ( args[0].equalsIgnoreCase("reload") ) {
            return doReload(sender, args);
        } else if ( args[0].equalsIgnoreCase("create") ) {
            return doCreate(sender, args);
        } else if ( args[0].equalsIgnoreCase("remove") ) {
            return doRemove(sender, args);
        } else if ( args[0].equalsIgnoreCase("format") ) {
            return doFormat(sender, args);
        } else if ( args[0].equalsIgnoreCase("moderator") ) {
            return doModerator(sender, args);
        } else {
            printUsage(sender, label);
            return true;
        }
    }

    /**
     * config.yml の再読み込みを行う
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doReload(CommandSender sender, String[] args) {

        LunaChat.config.reloadConfig();
        sender.sendMessage(Utility.replaceColorCode(
                Resources.get("cmdmsgReload")));
        return true;
    }

    /**
     * チャンネルを新規作成する
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doCreate(CommandSender sender, String[] args) {

        // 実行引数から、作成するチャンネルを取得する
        String name = "";
        String desc = "";
        if ( args.length >= 2 ) {
            name = args[1];
            if ( args.length >= 3 ) {
                desc = args[2];
            }
        } else {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgCommand")));
            return true;
        }
        
        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if ( channels.contains(name) ) {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgExist")));
            return true;
        }
        
        // チャンネル作成
        LunaChat.manager.createChannel(name, desc);
        sender.sendMessage(String.format(
                Utility.replaceColorCode(
                        PREINFO + Resources.get("cmdmsgCreate")),
                name));
        return true;
    }

    /**
     * チャンネルの削除をする
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doRemove(CommandSender sender, String[] args) {

        // 実行引数から、削除するチャンネルを取得する
        String name = "";
        if ( args.length >= 2 ) {
            name = args[1];
        } else {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgCommand")));
            return true;
        }
        
        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if ( !channels.contains(name) ) {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgNotExist")));
            return true;
        }
        
        // チャンネル削除
        LunaChat.manager.removeChannel(name);
        sender.sendMessage(String.format(
                Utility.replaceColorCode(
                        PREINFO + Resources.get("cmdmsgRemove")),
                name));
        return true;
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doFormat(CommandSender sender, String[] args) {

        // 実行引数から、設定するチャンネルを取得する
        String name = "";
        String format = "";
        if ( args.length >= 3 ) {
            name = args[1];
            for ( int i=2; i<args.length; i++ ) {
                format = format + args[i] + " ";
            }
        } else {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgCommand")));
            return true;
        }
        
        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if ( !channels.contains(name) ) {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgNotExist")));
            return true;
        }
        
        Channel channel = LunaChat.manager.getChannel(name);
        channel.format = format;
        sender.sendMessage(String.format(
                Utility.replaceColorCode(
                        PREINFO + Resources.get("cmdmsgFormat")),
                format));
        
        return true;
    }

    /**
     * モデレーターを設定する
     * @param sender 
     * @param args 
     * @return
     */
    private boolean doModerator(CommandSender sender, String[] args) {

        // 実行引数から、設定するチャンネルを取得する
        String name = "";
        String moderator = "";
        if ( args.length >= 3 ) {
            name = args[1];
            moderator = args[2];
        } else {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgCommand")));
            return true;
        }
        
        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if ( !channels.contains(name) ) {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgNotExist")));
            return true;
        }
        
        // チャンネルのメンバーかどうかをチェックする
        Channel channel = LunaChat.manager.getChannel(name);
        if ( !channel.members.contains(moderator) ) {
            sender.sendMessage(Utility.replaceColorCode(
                    PREERR + Resources.get("errmsgNomemberOther")));
            return true;
        }
        
        // 設定する
        channel.moderator = moderator;
        
        sender.sendMessage(String.format(
                Utility.replaceColorCode(
                        PREINFO + Resources.get("cmdmsgModerator")),
                name, moderator));
        
        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     * @param sender 
     * @param label 
     */
    private void printUsage(CommandSender sender, String label) {
        
        for ( String key : USAGE_KEYS ) {
            String msg = String.format(
                    Utility.replaceColorCode(Resources.get(key)),
                    label);
            sender.sendMessage(msg);
        }
    }
}
