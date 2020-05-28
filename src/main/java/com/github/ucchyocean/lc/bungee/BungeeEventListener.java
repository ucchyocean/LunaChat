/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.japanize.Japanizer;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * BungeeCordのイベントを監視するリスナークラス
 * @author ucchy
 */
public class BungeeEventListener implements Listener {

    private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd";
    private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private LunaChatBungee parent;
    private LunaChatConfig config;

    /**
     * コンストラクタ
     * @param parent LunaChatBungeeのインスタンス
     */
    protected BungeeEventListener(LunaChatBungee parent) {
        this.parent = parent;
        config = parent.getConfig();
        dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        timeFormat = new SimpleDateFormat(TIME_FORMAT_PATTERN);
    }

    /**
     * プレイヤーがチャット発言した時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onChat(ChatEvent event) {

        // 設定が無効なら、そのまま無視する
        if ( !config.isBroadcastChat() ) {
            return;
        }

        // コマンド実行の場合は、そのまま無視する
        if ( event.isCommand() ) {
            return;
        }

        // プレイヤーの発言ではない場合は、そのまま無視する
        if ( !(event.getSender() instanceof ProxiedPlayer) ) {
            return;
        }

        // 発言者と発言サーバーと発言内容の取得
        final ProxiedPlayer sender = (ProxiedPlayer)event.getSender();
        String senderServer = sender.getServer().getInfo().getName();
        String message = event.getMessage();

        // NGワードのマスク
        message = maskNGWord(message, config.getNgwordCompiled());

        // Japanizeの付加
        if ( message.startsWith(config.getNoneJapanizeMarker()) ) {

            message = message.substring(config.getNoneJapanizeMarker().length());

        } else {

            String japanize = Japanizer.japanize(message, config.getJapanizeType(),
                    LunaChat.getAPI().getAllDictionary());
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

        // フォーマットの置き換え処理
        String result = config.getBroadcastChatFormat();
        result = result.replace("%senderserver", senderServer);
        result = result.replace("%sender", sender.getName());
        if ( result.contains("%date") ) {
            result = result.replace("%date", dateFormat.format(new Date()));
        }
        if ( result.contains("%time") ) {
            result = result.replace("%time", timeFormat.format(new Date()));
        }
        result = result.replace("%msg", message);
        result = Utility.replaceColorCode(result);

        // 発言したプレイヤーがいるサーバー"以外"のサーバーに、
        // 発言内容を送信する。
        for ( String server : parent.getProxy().getServers().keySet() ) {

            if ( server.equals(senderServer) ) {
                continue;
            }

            ServerInfo info = parent.getProxy().getServerInfo(server);
            for ( ProxiedPlayer player : info.getPlayers() ) {
                sendMessage(player, result);
            }
        }

        // ローカルも置き換える処理なら、置換えを行う
        if ( config.isBroadcastChatLocalJapanize() ) {

            // NOTE: 改行がサポートされないので、改行を含む場合は、
            // \nで分割して前半をセットし、後半は150ミリ秒後に送信する。
            if ( !message.contains("\n") ) {
                event.setMessage(Utility.stripColorCode(message));
            } else {
                int index = message.indexOf("\n");
                String pre = message.substring(0, index);
                final String post = Utility.replaceColorCode(
                        message.substring(index + "\n".length()));
                event.setMessage(Utility.stripColorCode(pre));
                parent.getProxy().getScheduler().schedule(parent, new Runnable() {
                    @Override
                    public void run() {
                        for ( ProxiedPlayer p : sender.getServer().getInfo().getPlayers() ) {
                            sendMessage(p, post);
                        }
                    }
                }, 150, TimeUnit.MILLISECONDS);
            }
        }

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
    private void sendMessage(CommandSender reciever, String message) {
        if ( message == null ) return;
        reciever.sendMessage(TextComponent.fromLegacyText(message));
    }
}
