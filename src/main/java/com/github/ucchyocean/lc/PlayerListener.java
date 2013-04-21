/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
        if ( event.getMessage().startsWith(LunaChat.config.globalMarker) ) {
            int offset = LunaChat.config.globalMarker.length();
            event.setMessage(event.getMessage().substring(offset));
            return;
        }
        
        Player player = event.getPlayer();
        Channel channel = LunaChat.manager.getChannelByPlayer(player);
        
        // チャンネルに所属していない場合
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
}
