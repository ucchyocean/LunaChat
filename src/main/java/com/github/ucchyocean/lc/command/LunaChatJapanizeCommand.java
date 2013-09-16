/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.Resources;

/**
 * Japanize変換設定コマンド
 * @author ucchy
 */
public class LunaChatJapanizeCommand implements CommandExecutor {

    private static final String PERM_JAPANIZE_OTHER = "lunachat-admin.japanize-other";
    
    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

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
            if (!(sender instanceof Player)) {
                sendResourceMessage(sender, PREERR, "errmsgIngame");
                return true;
            }
            Player player = (Player)sender;

            // Japanize設定をon/offにする
            boolean value = args[0].equalsIgnoreCase("on");
            LunaChatAPI api = LunaChat.instance.getLunaChatAPI();
            api.setPlayersJapanize(player.getName(), value);

            sendResourceMessage(sender, PREINFO,
                    "cmdmsgPlayerJapanize", args[0]);
            return true;

        } else if ( args.length == 2 &&
                (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off")) ) {
            // japanize (player) on/off の実行

            // 権限チェック
            if ( !sender.hasPermission(PERM_JAPANIZE_OTHER) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", PERM_JAPANIZE_OTHER);
                return true;
            }

            // 指定されたプレイヤーが存在するかチェック
            Player target = Bukkit.getPlayerExact(args[0]);
            if ( target == null ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotfoundPlayer", args[0]);
                return true;
            }

            // Japanize設定をon/offにする
            boolean value = args[1].equalsIgnoreCase("on");
            LunaChatAPI api = LunaChat.instance.getLunaChatAPI();
            api.setPlayersJapanize(target.getName(), value);

            sendResourceMessage(target, PREINFO,
                    "cmdmsgPlayerJapanize", args[1]);
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgPlayerJapanizeOther", args[0], args[1]);
            return true;
        }

        // usageを表示して終了する
        printUsage(sender, label);
        return true;
    }

    /**
     * コマンドの使い方を senderに送る
     *
     * @param sender
     * @param label
     */
    private void printUsage(CommandSender sender, String label) {
        sendResourceMessage(sender, "", "usageJapanize", label);
        if ( sender.hasPermission(PERM_JAPANIZE_OTHER) ) {
            sendResourceMessage(sender, "", "usageJapanizeOther", label);
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
    protected void sendResourceMessage(CommandSender sender, String pre,
            String key, Object... args) {
        String msg = String.format(pre + Resources.get(key), args);
        sender.sendMessage(msg);
    }
}
