/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * Japanize変換設定コマンド
 * @author ucchy
 */
public class LunaChatJapanizeCommand {

    private static final String PERM_JAPANIZE_OTHER = "lunachat-admin.japanize-other";

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 実行したかどうか（falseを返した場合、サーバーがUsageを表示する）
     */
    public boolean execute(ChannelMember sender, String label, String[] args) {

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
            LunaChatAPI api = LunaChat.getAPI();
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
            LunaChatAPI api = LunaChat.getAPI();
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
    private void printUsage(ChannelMember sender, String label) {
        sender.sendMessage(Messages.usageJapanize(label));
        if ( sender.hasPermission(PERM_JAPANIZE_OTHER) ) {
            sender.sendMessage(Messages.usageJapanizeOther(label));
        }
    }
}
