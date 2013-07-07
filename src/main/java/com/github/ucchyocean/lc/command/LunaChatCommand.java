/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.Resources;



/**
 * @author ucchy
 * Lunachatコマンドの処理クラス
 */
public class LunaChatCommand implements CommandExecutor {

    private static final String PREERR = Resources.get("errorPrefix");
    
    private ArrayList<SubCommandAbst> commands;
    private JoinCommand joinCommand;
    
    /**
     * コンストラクタ
     */
    public LunaChatCommand() {
        
        commands = new ArrayList<SubCommandAbst>();
        joinCommand = new JoinCommand();
        commands.add(joinCommand);
        commands.add(new LeaveCommand());
        commands.add(new ListCommand());
        commands.add(new InviteCommand());
        commands.add(new AcceptCommand());
        commands.add(new DenyCommand());
        commands.add(new KickCommand());
        commands.add(new BanCommand());
        commands.add(new PardonCommand());
        commands.add(new CreateCommand());
        commands.add(new RemoveCommand());
        commands.add(new FormatCommand());
        commands.add(new ModeratorCommand());
        commands.add(new OptionCommand());
        commands.add(new TemplateCommand());
        commands.add(new ReloadCommand());
    }

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        // 引数なしは、ヘルプを表示
        if (args.length == 0) {
            printUsage(sender, label);
            return true;
        }

        // 第1引数に指定されたコマンドを実行する
        for ( SubCommandAbst c : commands ) {
            if ( c.getCommandName().equalsIgnoreCase(args[0]) ) {
                
                // パーミッションの確認
                String node = c.getPermissionNode();
                if ( !sender.hasPermission(node) ) {
                    sendResourceMessage(sender, PREERR, "errmsgPermission", node);
                    return true;
                }
                
                // 実行
                return c.runCommand(sender, label, args);
            }
        }

        // 第1引数がコマンドでないなら、joinが指定されたとみなす
        String node = joinCommand.getPermissionNode();
        if ( !sender.hasPermission(node) ) {
            sendResourceMessage(sender, PREERR, "errmsgPermission", node);
            return true;
        }
        
        return joinCommand.runCommand(sender, label, args);
    }

    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        for ( SubCommandAbst c : commands ) {
            if ( sender.hasPermission(c.getPermissionNode()) ) {
                c.sendUsageMessage(sender, label);
            }
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
        String msg = String.format(pre + Resources.get(key), args);
        sender.sendMessage(msg);
    }
}
