/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.bukkit.LunaChatBukkit;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * Japanize変換設定コマンド
 * @author ucchy
 */
public class LunaChatJapanizeCommand implements CommandExecutor {

    private static final String PERM_JAPANIZE_OTHER = "lunachat-admin.japanize-other";

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        if ( args.length == 1 &&
                (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) ) {
            // japanize on/off の実行

            // プレイヤーでなければ終了する
//            if (!(sender instanceof Player)) {
//                sender.sendMessage(Messages.errmsgIngame());
//                return true;
//            }
//            Player player = (Player)sender;

            // Japanize設定をon/offにする
            boolean value = args[0].equalsIgnoreCase("on");
            LunaChatAPI api = LunaChatBukkit.getInstance().getLunaChatAPI();
            api.setPlayersJapanize(sender.getName(), value);

            sender.sendMessage(Messages.cmdmsgPlayerJapanize(args[0]));
            return true;

        } else if ( args.length == 2 &&
                (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) ) {
            // japanize (player) on/off の実行

            // 権限チェック
            if ( !sender.hasPermission(PERM_JAPANIZE_OTHER) ) {
                sender.sendMessage(Messages.errmsgNotPermission(PERM_JAPANIZE_OTHER));
                return true;
            }

            // 指定されたプレイヤーが存在するかチェック
            ChannelMember target = ChannelMember.getChannelMember(args[0]);
            if ( target == null || !target.isOnline() ) {
                sender.sendMessage(Messages.errmsgNotfoundPlayer(args[0]));
                return true;
            }

            // Japanize設定をon/offにする
            boolean value = args[1].equalsIgnoreCase("on");
            LunaChatAPI api = LunaChatBukkit.getInstance().getLunaChatAPI();
            api.setPlayersJapanize(target.getName(), value);

            target.sendMessage(Messages.cmdmsgPlayerJapanize(args[1]));
            sender.sendMessage(Messages.cmdmsgPlayerJapanizeOther(args[0], args[1]));
            return true;
        }

        // usageを表示して終了する
        printUsage(sender, label);
        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        sender.sendMessage(Messages.usageJapanize(label));
        if ( sender.hasPermission(PERM_JAPANIZE_OTHER) ) {
            sender.sendMessage(Messages.usageJapanizeOther(label));
        }
    }
}
