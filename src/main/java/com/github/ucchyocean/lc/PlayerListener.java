/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ucchyocean.lc.japanize.ConvertTask;
import com.github.ucchyocean.lc.japanize.JapanizeType;

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
        Channel channel = LunaChat.manager.getDefaultChannel(player.getName());

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
     * プレイヤーのサーバー退出ごとに呼び出されるメソッド
     * @param event プレイヤー退出イベント
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        // お互いがオフラインになるPMチャンネルがある場合は
        // チャンネルをクリアする
        Collection<Channel> channels = LunaChat.manager.getChannels();
        ArrayList<Channel> deleteList = new ArrayList<Channel>();

        for ( Channel channel : channels ) {
            String cname = channel.getName();
            if ( cname.contains(">") && cname.contains(player.getName()) ) {
                boolean isAllOffline = true;
                for ( String pname : channel.getMembers() ) {
                    if ( !pname.equals(player.getName()) ) {
                        Player p = LunaChat.getPlayerExact(pname);
                        if ( p != null && p.isOnline() ) {
                            isAllOffline = false;
                        }
                    }
                }
                if ( isAllOffline ) {
                    deleteList.add(channel);
                }
            }
        }

        for ( Channel channel : deleteList ) {
            LunaChat.manager.removeChannel(channel.getName());
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
                global = LunaChat.manager.createChannel(LunaChat.config.globalChannel);
            }

            // デフォルト発言先が無いなら、グローバルチャンネルに設定する
            Channel dchannel = LunaChat.manager.getDefaultChannel(player.getName());
            if ( dchannel == null ) {
                LunaChat.manager.setDefaultChannel(player.getName(), global.getName());
            }

            // チャンネルチャット発言
            global.chat(player, event.getMessage());

            // もとのイベントをキャンセル
            event.setCancelled(true);

            return;

        } else {
            // グローバルチャンネル設定が無い場合

            String message = event.getMessage();
            // NGワード発言をマスク
            for ( String word : LunaChat.config.ngword ) {
                if ( message.contains(word) ) {
                    message = message.replace(
                            word, Utility.getAstariskString(word.length()));
                }
            }

            // カラーコード置き換え
            message = Utility.replaceColorCode(message);

            // Japanize変換と、発言処理
            if ( LunaChat.config.getJapanizeType() != JapanizeType.NONE ) {
                // 2byteコードを含まない場合にのみ、処理を行う
                if ( message.getBytes().length == message.length() ) {

                    int lineType = LunaChat.config.japanizeDisplayLine;
                    String taskFormat;
                    if ( lineType == 1 )
                        taskFormat = LunaChat.config.japanizeLine1Format;
                    else
                        taskFormat = LunaChat.config.japanizeLine2Format;

                    // メッセージを差し替えする
                    ConvertTask task = new ConvertTask(message,
                            LunaChat.config.getJapanizeType(), null, taskFormat);

                    if ( lineType == 1 ) {
                        if ( task.runSync() ) {
                            message = task.getResult();
                        }
                    } else {
                        Bukkit.getScheduler().runTask(LunaChat.instance, task);
                    }
                }
            }

            event.setMessage(message);

            return;
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
            global = LunaChat.manager.createChannel(gcName);
        }

        // デフォルト発言先が無いなら、グローバルチャンネルに設定する
        Channel dchannel = LunaChat.manager.getDefaultChannel(player.getName());
        if ( dchannel == null ) {
            LunaChat.manager.setDefaultChannel(player.getName(), gcName);
        }

        return true;
    }
}
