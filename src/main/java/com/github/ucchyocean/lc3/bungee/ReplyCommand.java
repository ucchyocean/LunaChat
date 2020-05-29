/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * LunaChatのreplyコマンド実装クラス
 * @author ucchy
 */
public class ReplyCommand extends TellCommand {

    private LunaChatBungee parent;

    /**
     * コンストラクタ
     * @param BungeeJapanizeMessenger
     * @param name コマンド
     * @param permission パーミッション
     * @param aliases エイリアス
     */
    public ReplyCommand(LunaChatBungee parent, String name, String permission, String... aliases) {
        super(parent, name, permission, aliases);
        this.parent = parent;
    }

    /**
     * コマンドが実行された時に呼び出されるメソッド
     * @param sender コマンド実行者
     * @param args コマンド引数
     * @see net.md_5.bungee.api.plugin.Command#execute(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    @Override
    public void execute(CommandSender sender, String[] args) {

        String recieverName = parent.getHistory(sender.getName());

        // 引数が無いときは、現在の会話相手を表示して終了する。
        if ( args.length == 0 ) {
            if ( recieverName != null ) {
                sendMessage(sender, ChatColor.LIGHT_PURPLE +
                        "現在の会話相手： " + recieverName);
            } else {
                sendMessage(sender, ChatColor.LIGHT_PURPLE +
                        "現在の会話相手は設定されていません。");
            }
            return;
        }

        // 送信先プレイヤーの取得。取得できないならエラーを表示して終了する。
        if ( recieverName == null ) {
            sendMessage(sender, ChatColor.RED +
                    "メッセージ送信先が見つかりません。");
            return;
        }
        ProxiedPlayer reciever = parent.getProxy().getPlayer(
                parent.getHistory(sender.getName()));
        if ( reciever == null ) {
            sendMessage(sender, ChatColor.RED +
                    "メッセージ送信先" + recieverName + "が見つかりません。");
            return;
        }

        // 送信メッセージの作成
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
                str.append(args[i] + " ");
        }
        String message = str.toString().trim();

        // 送信
        sendPrivateMessage(sender, reciever, message);
    }
}
