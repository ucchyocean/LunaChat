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
 * @author ucchy LunachatAdminコマンドの処理クラス
 */
public class LunaChatAdminCommand implements CommandExecutor {

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

    private static final String[] USAGE_KEYS = {
        "usageReload", "usageCreate",
        "usageRemove", "usageFormat", "usageModerator"
    };

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        if (args.length == 0) {
            printUsage(sender, label);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return doReload(sender, args);
        } else if (args[0].equalsIgnoreCase("create")) {
            return doCreate(sender, args);
        } else if (args[0].equalsIgnoreCase("remove")) {
            return doRemove(sender, args);
        } else if (args[0].equalsIgnoreCase("format")) {
            return doFormat(sender, args);
        } else if (args[0].equalsIgnoreCase("moderator")) {
            return doModerator(sender, args);
        } else {
            printUsage(sender, label);
            return true;
        }
    }

    /**
     * config.yml の再読み込みを行う
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doReload(CommandSender sender, String[] args) {

        LunaChat.config.reloadConfig();
        sendResourceMessage(sender, PREINFO, "cmdmsgReload");
        return true;
    }

    /**
     * チャンネルを新規作成する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doCreate(CommandSender sender, String[] args) {

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
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (channels.contains(name)) {
            sendResourceMessage(sender, PREERR, "errmsgExist");
            return true;
        }

        // チャンネル作成
        LunaChat.manager.createChannel(name, desc);
        sendResourceMessage(sender, PREINFO, "cmdmsgCreate", name);
        return true;
    }

    /**
     * チャンネルの削除をする
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doRemove(CommandSender sender, String[] args) {

        // 実行引数から、削除するチャンネルを取得する
        String name = "";
        if (args.length >= 2) {
            name = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(name)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // チャンネル削除
        LunaChat.manager.removeChannel(name);
        sendResourceMessage(sender, PREINFO, "cmdmsgRemove", name);
        return true;
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doFormat(CommandSender sender, String[] args) {

        // 実行引数から、設定するチャンネルを取得する
        String name = "";
        String format = "";
        if (args.length >= 3) {
            name = args[1];
            for (int i = 2; i < args.length; i++) {
                format = format + args[i] + " ";
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(name)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        Channel channel = LunaChat.manager.getChannel(name);
        channel.format = format;
        sendResourceMessage(sender, PREINFO, "cmdmsgFormat", format);
        return true;
    }

    /**
     * モデレーターを設定する
     *
     * @param sender
     * @param args
     * @return
     */
    private boolean doModerator(CommandSender sender, String[] args) {

        // 実行引数から、設定するチャンネルを取得する
        String name = "";
        String moderator = "";
        if (args.length >= 3) {
            name = args[1];
            moderator = args[2];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(name)) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // チャンネルのメンバーかどうかをチェックする
        Channel channel = LunaChat.manager.getChannel(name);
        if (!channel.members.contains(moderator)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }

        // 設定する
        channel.moderator = moderator;
        sendResourceMessage(sender, PREINFO,
                "cmdmsgModerator", name, moderator);

        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        for (String key : USAGE_KEYS) {
            sendResourceMessage(sender, "", key, label);
        }
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     *
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    private void sendResourceMessage(CommandSender sender, String pre,
            String key, Object... args) {
        String msg = String.format(
                Utility.replaceColorCode(pre + Resources.get(key)), args);
        sender.sendMessage(msg);
    }
}
