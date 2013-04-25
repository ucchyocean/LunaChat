/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;

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

        // グローバルチャンネル設定がある場合
        if ( !LunaChat.config.globalChannel.equals("") ) {
            tryJoinToGlobalChannel(player);
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

        if ( !LunaChat.config.globalChannel.equals("") ) {
            // グローバルチャンネル設定がある場合

            // グローバルチャンネルの取得、無ければ作成
            Channel global = LunaChat.manager.getChannel(LunaChat.config.globalChannel);
            if ( global == null ) {
                global = LunaChat.manager.createChannel(LunaChat.config.globalChannel, "");
            }

            // チャンネルチャット発言
            global.chat(player, event.getMessage());

            // もとのイベントをキャンセル
            event.setCancelled(true);

        } else {
            // グローバルチャンネル設定が無い場合

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
    private boolean tryJoinToGlobalChannel(Player player) {

        String gcName = LunaChat.config.globalChannel;

        // チャンネルが存在しない場合は作成する
        Channel global = LunaChat.manager.getChannel(gcName);
        if ( global == null ) {
            global = LunaChat.manager.createChannel(gcName, "");
        }

        // デフォルト発言先が無いなら、グローバルチャンネルに設定する
        Channel dchannel = LunaChat.manager.getDefaultChannelByPlayer(player.getName());
        if ( dchannel == null ) {
            LunaChat.manager.setDefaultChannel(player.getName(), gcName);
        }

        return true;
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
