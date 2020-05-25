/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.japanize.Japanizer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * BungeeJapanizeMessengerのtellコマンド実装クラス
 * @author ucchy
 */
public class TellCommand extends Command {

    private LunaChatBungee parent;

    /**
     * コンストラクタ
     * @param parent
     * @param name コマンド
     */
    public TellCommand(LunaChatBungee parent, String name) {
        super(name);
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

        // 引数が足らないので、Usageを表示して終了する。
        if ( args.length <= 1 ) {
            sendMessage(sender, ChatColor.RED +
                    "実行例： /" + this.getName() + " <player> <message>");
            return;
        }

        // 自分自身には送信できない。
        if ( args[0].equals(sender.getName()) ) {
            sendMessage(sender, ChatColor.RED +
                    "自分自身にはプライベートメッセージを送信することができません。");
            return;
        }

        // 送信先プレイヤーの取得。取得できないならエラーを表示して終了する。
        ProxiedPlayer reciever = parent.getProxy().getPlayer(args[0]);
        if ( reciever == null ) {
            sendMessage(sender, ChatColor.RED +
                    "メッセージ送信先 " + args[0] + " が見つかりません。");
            return;
        }

        // 送信メッセージの作成
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
                str.append(args[i] + " ");
        }
        String message = str.toString().trim();

        // 送信
        sendPrivateMessage(sender, reciever, message);
    }

    /**
     * プライベートメッセージを送信する
     * @param sender 送信者
     * @param receiver 受信者名
     * @param message メッセージ
     */
    protected void sendPrivateMessage(CommandSender sender, ProxiedPlayer reciever, String message) {

        LunaChatConfig config = parent.getConfig();

        // NGワードのマスク
        message = maskNGWord(message, config.getNgwordCompiled());

        // Japanizeの付加
        if ( message.startsWith(config.getNoneJapanizeMarker()) ) {

            message = message.substring(config.getNoneJapanizeMarker().length());

        } else {

            String japanize = Japanizer.japanize(message, config.getJapanizeType(),
                    parent.getDictionary().getDictionary());
            if ( japanize.length() > 0 ) {

                // NGワードのマスク
                japanize = maskNGWord(japanize, config.getNgwordCompiled());

                // フォーマット化してメッセージを上書きする
                String japanizeFormat = config.getJapanizeDisplayLine() == 1 ?
                        config.getJapanizeLine1Format() :
                        "%msg\n" + config.getJapanizeLine2Format();
                String preMessage = new String(message);
                message = japanizeFormat.replace("%msg", preMessage).replace("%japanize", japanize);
            }
        }

        // フォーマットの適用
        String senderServer = "";
        if ( sender instanceof ProxiedPlayer ) {
            senderServer = ((ProxiedPlayer)sender).getServer().getInfo().getName();
        }
        String result = new String(
                parent.getConfig().getDefaultFormatForPrivateMessage());
        result = result.replace("%senderserver", senderServer);
        result = result.replace("%sender", sender.getName());
        result = result.replace("%recieverserver",
                reciever.getServer().getInfo().getName());
        result = result.replace("%reciever", reciever.getName());
        result = result.replace("%msg", message);

        // カラーコードの置き換え
        result = Utility.replaceColorCode(result);

        // メッセージ送信
        sendMessage(sender, result);
        sendMessage(reciever, result);

        // 送信履歴を記録
        parent.putHistory(reciever.getName(), sender.getName());

        // コンソールに表示設定なら、コンソールに表示する
        if ( config.isDisplayChatOnConsole() ) {
            parent.getLogger().info(result);
        }
    }

    /**
     * NGワードをマスクする
     * @param message メッセージ
     * @param ngwords NGワード
     * @return マスクされたメッセージ
     */
    private String maskNGWord(String message, List<Pattern> ngwords) {
        for ( Pattern pattern : ngwords ) {
            Matcher matcher = pattern.matcher(message);
            if ( matcher.find() ) {
                message = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
            }
        }
        return message;
    }

    /**
     * 指定した対象にメッセージを送信する
     * @param reciever 送信先
     * @param message メッセージ
     */
    protected void sendMessage(CommandSender reciever, String message) {
        if ( message == null ) return;
        reciever.sendMessage(TextComponent.fromLegacyText(message));
    }
}
