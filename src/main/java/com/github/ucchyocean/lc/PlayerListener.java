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
            event.setMessage(event.getMessage().substring(offset));
            return;
        }
        
        Player player = event.getPlayer();
        Channel channel = LunaChat.manager.getDefaultChannelByPlayer(player);
        
        // デフォルトの発言先が無い場合
        if ( channel == null ) {
            if ( LunaChat.config.noJoinAsGlobal ) {
                // グローバル発言にする（＝つまり、何もしないで終了する）
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
    
        if ( LunaChat.config.showListOnJoin ) {
            // チャンネルチャット情報を表示する
            Player player = event.getPlayer();
            ArrayList<String> list = LunaChat.manager.getList(player);
            for ( String msg : list ) {
                player.sendMessage(msg);
            }
        }
    }
}
