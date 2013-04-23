/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author ucchy
 * プレイヤーの行動を監視するリスナ
 */
public class PlayerListener implements Listener {

    private static final String PREINFO = Resources.get("infoPrefix");
    private static final String PREERR = Resources.get("errorPrefix");

    /**
     * プレイヤーのチャットごとに呼び出されるメソッド
     * @param event チャットイベント
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        // 頭にglobalMarkerが付いている場合は、グローバル発言にする
        if ( LunaChat.config.globalMarker != null &&
                !LunaChat.config.globalMarker.equals("") &&
                event.getMessage().startsWith(LunaChat.config.globalMarker) ) {

            int offset = LunaChat.config.globalMarker.length();
            String message = event.getMessage().substring(offset);

            // Japanize変換
            if ( LunaChat.config.displayJapanize ) {
                // 2byteコードを含まない場合にのみ、処理を行う
                if ( message.getBytes().length == message.length() ) {
                    String kana = KanaConverter.conv(message);
                    message = message + "(" + kana + ")";
                }
            }

            event.setMessage(message);
            return;
        }

        Player player = event.getPlayer();
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(player.getName());

        // デフォルトの発言先が無い場合
        if ( channel == null ) {
            if ( LunaChat.config.noJoinAsGlobal ) {
                // グローバル発言にする

                // Japanize変換
                if ( LunaChat.config.displayJapanize ) {
                    // 2byteコードを含まない場合にのみ、処理を行う
                    String message = event.getMessage();
                    if ( message.getBytes().length == message.length() ) {
                        String kana = KanaConverter.conv(message);
                        message = message + "(" + kana + ")";
                    }
                    event.setMessage(message);
                }

                return;
            } else {
                // 発言をキャンセルして終了する
                event.setCancelled(true);
                return;
            }
        }

        // チャンネルチャット発言
        channel.chat(player, event.getMessage());

        // もとのイベントをキャンセル
        event.setCancelled(true);
    }

    /**
     * プレイヤーのサーバー参加ごとに呼び出されるメソッド
     * @param event プレイヤー参加イベント
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // サーバー初参加のプレイヤーを、既定のチャンネルへ参加させる
        if ( player.hasPlayedBefore() &&
                !LunaChat.config.joinChannelOnFirstVisit.equals("") ) {
            tryJoinToDefaultChannel(player);
        }

        // チャンネルチャット情報を表示する
        if ( LunaChat.config.showListOnJoin ) {
            ArrayList<String> list = LunaChat.manager.getListForMotd(player);
            for ( String msg : list ) {
                player.sendMessage(msg);
            }
        }
    }

    /**
     * 既定のチャンネルへの参加を試みる。
     * @param player プレイヤー
     * @return 参加できたかどうか
     */
    private boolean tryJoinToDefaultChannel(Player player) {

        String channelName = LunaChat.config.joinChannelOnFirstVisit;

        // チャンネルが存在するかどうかをチェックする
        ArrayList<String> channels = LunaChat.manager.getNames();
        if (!channels.contains(channelName)) {
            if (LunaChat.config.createChannelOnJoinCommand) {
                // 存在しないチャットには、チャンネルを作って入る設定の場合

                // チャンネル作成
                Channel c = LunaChat.manager.createChannel(channelName, "");
                c.addMember(player.getName());
                sendResourceMessage(player, PREINFO, "cmdmsgCreate", channelName);
                return false;

            } else {
                // 存在しないチャットには入れない設定の場合

                sendResourceMessage(player, PREERR, "errmsgNotExist");
                return false;
            }
        }

        // デフォルト発言先をチェックする
        Channel channel = LunaChat.manager.getChannel(channelName);

        // BANされていないか確認する
        if (channel.banned.contains(player.getName())) {
            sendResourceMessage(player, PREERR, "errmsgBanned");
            return false;
        }

        // チャンネルに参加し、デフォルトの発言先に設定する
        channel.addMember(player.getName());
        sendResourceMessage(player, PREINFO, "cmdmsgJoin", channelName);
        LunaChat.manager.setDefaultChannel(player.getName(), channelName);
        sendResourceMessage(player, PREINFO, "cmdmsgSet", channelName);

        return true;
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
