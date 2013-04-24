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
            event.setMessage( event.getMessage().substring(offset) );
            chatGlobal(event);
            return;
        }

        Player player = event.getPlayer();
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(player.getName());

        // デフォルトの発言先が無い場合
        if ( channel == null ) {
            if ( LunaChat.config.noJoinAsGlobal ) {
                // グローバル発言にする
                chatGlobal(event);
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

        // どのチャンネルにも所属していないプレイヤーを、既定のチャンネルへ参加させる
        if ( LunaChat.manager.getChannelByPlayer(player).size() == 0 &&
                !LunaChat.config.globalChannel.equals("") ) {
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
     * イベントをグローバルチャット発言として処理する
     * @param event 処理するイベント
     */
    private void chatGlobal(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        if ( !LunaChat.config.globalChannel.equals("") &&
                LunaChat.manager.getChannel(LunaChat.config.globalChannel) != null ) {
            // グローバルチャンネルがある場合

            Channel global = LunaChat.manager.getChannel(LunaChat.config.globalChannel);

            // もしグローバルのメンバーでなければ、まず参加させる
            if ( global.members.contains(player.getName()) ) {
                global.addMember(player.getName());
            }

            // チャンネルチャット発言
            global.chat(player, event.getMessage());

            // もとのイベントをキャンセル
            event.setCancelled(true);

        } else {
            // グローバルチャンネルが無い場合

            // Japanize変換
            if ( LunaChat.config.displayJapanize ) {
                event.setMessage( addJapanize(event.getMessage()) );
            }
        }
    }

    /**
     * 既定のチャンネルへの参加を試みる。
     * @param player プレイヤー
     * @return 参加できたかどうか
     */
    private boolean tryJoinToDefaultChannel(Player player) {

        String channelName = LunaChat.config.globalChannel;

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

    /**
     * メッセージに2バイトコードが含まれていない場合に、かな文字を付加する
     * @param message メッセージ
     * @return かな文字付きのメッセージ
     */
    private String addJapanize(String message) {
        // 2byteコードを含まない場合にのみ、処理を行う
        if ( message.getBytes().length == message.length() ) {
            String kana = KanaConverter.conv(message);
            message = message + "(" + kana + ")";
        }
        return message;
    }
}
