/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bridge;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.DynmapWebChatEvent;

import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;
import com.github.ucchyocean.lc.channel.Channel;

/**
 * dynmap連携クラス
 * @author ucchy
 */
public class DynmapBridge implements Listener {

    /** dynmap-apiクラス */
    private DynmapAPI dynmap;

    /** コンストラクタは使用不可 */
    private DynmapBridge() {
    }

    /**
     * dynmap-apiをロードする
     * @param plugin dynmap-apiのプラグインインスタンス
     * @param ロードしたかどうか
     */
    public static DynmapBridge load(Plugin plugin) {

        if ( plugin instanceof DynmapAPI ) {
            DynmapBridge bridge = new DynmapBridge();
            bridge.dynmap = (DynmapAPI)plugin;
            return bridge;
        } else {
            return null;
        }
    }

    /**
     * dynmapにプレイヤーのチャットを流す
     * @param player プレイヤー
     * @param message 発言内容
     */
    public void chat(Player player, String message) {

        dynmap.postPlayerMessageToWeb(player, message);
    }

    /**
     * dynmapにブロードキャストメッセージを流す
     * @param message メッセージ
     */
    public void broadcast(String message) {

        dynmap.sendBroadcastToWeb(null, message);
    }

    /**
     * DynmapのWebUIからチャット発言されたときのイベント
     * @param event
     */
    @EventHandler
    public void onDynmapWebChat(DynmapWebChatEvent event) {

        LunaChatAPI api = LunaChatBukkit.getInstance().getLunaChatAPI();
        LunaChatConfig config = LunaChatBukkit.getInstance().getLunaChatConfig();
        String dchannel = config.getDynmapChannel();
        Channel channel = null;

        if ( !dchannel.equals("") ) {
            // dynmapChannelが設定されている場合
            channel = api.getChannel(dchannel);

        } else {
            String gchannel = config.getGlobalChannel();
            if ( !gchannel.equals("") ) {
                // dynmapChannelが設定されていなくて、
                // globalChannelが設定されている場合
                channel = api.getChannel(gchannel);

            }
        }

        if ( channel != null ) {
            // チャンネルへ送信
            channel.chatFromOtherSource(
                    event.getName(), event.getSource(), event.getMessage());
            event.setProcessed();
            dynmap.sendBroadcastToWeb(null, event.getMessage());
        }
    }
}
