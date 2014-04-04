/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ucchyocean.lc.bridge.VaultChatBridge;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * プレイヤーの行動を監視するリスナ
 * @author ucchy
 */
public class PlayerListener implements Listener {

    /**
     * プレイヤーのチャットごとに呼び出されるメソッド
     * @param event チャットイベント
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        // 頭にglobalMarkerが付いている場合は、グローバル発言にする
        if ( config.getGlobalMarker() != null &&
                !config.getGlobalMarker().equals("") &&
                event.getMessage().startsWith(config.getGlobalMarker()) &&
                event.getMessage().length() > config.getGlobalMarker().length() ) {

            int offset = config.getGlobalMarker().length();
            event.setMessage( event.getMessage().substring(offset) );
            chatGlobal(event);
            return;
        }

        Player player = event.getPlayer();
        Channel channel = api.getDefaultChannel(player.getName());

        // デフォルトの発言先が無い場合
        if ( channel == null ) {
            if ( config.isNoJoinAsGlobal() ) {
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

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        Player player = event.getPlayer();

        // 強制参加チャンネル設定を確認し、参加させる
        forceJoinToForceJoinChannels(player);

        // グローバルチャンネル設定がある場合
        if ( !config.getGlobalChannel().equals("") ) {
            tryJoinToGlobalChannel(player);
        }

        // チャンネルチャット情報を表示する
        if ( config.isShowListOnJoin() ) {
            ArrayList<String> list =
                    LunaChat.getInstance().getManager().getListForMotd(player);
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
        ArrayList<Channel> deleteList = new ArrayList<Channel>();

        for ( Channel channel : LunaChat.getInstance().getLunaChatAPI().getChannels() ) {
            String cname = channel.getName();
            if ( cname.contains(">") && cname.contains(player.getName()) ) {
                boolean isAllOffline = true;
                for ( String pname : channel.getMembers() ) {
                    if ( !pname.equals(player.getName()) ) {
                        Player p = Utility.getPlayerExact(pname);
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
            LunaChat.getInstance().getLunaChatAPI().removeChannel(channel.getName());
        }
    }

    /**
     * イベントをグローバルチャット発言として処理する
     * @param event 処理するイベント
     */
    private void chatGlobal(AsyncPlayerChatEvent event) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        Player player = event.getPlayer();

        if ( !config.getGlobalChannel().equals("") ) {
            // グローバルチャンネル設定がある場合

            // グローバルチャンネルの取得、無ければ作成
            Channel global = api.getChannel(config.getGlobalChannel());
            if ( global == null ) {
                global = api.createChannel(config.getGlobalChannel());
            }

            // デフォルト発言先が無いなら、グローバルチャンネルに設定する
            Channel dchannel = api.getDefaultChannel(player.getName());
            if ( dchannel == null ) {
                api.setDefaultChannel(player.getName(), global.getName());
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
            for ( String word : config.getNgword() ) {
                if ( message.contains(word) ) {
                    message = message.replace(
                            word, Utility.getAstariskString(word.length()));
                }
            }

            // チャットフォーマット装飾の適用
            if ( config.isEnableNormalChatMessageFormat() ) {
                String format = config.getNormalChatMessageFormat();
                format = replaceNormalChatFormatKeywords(format, event.getPlayer());
                event.setFormat(format);
            }

            // カラーコード置き換え
            message = Utility.replaceColorCode(message);

            // 一時的にJapanizeスキップ設定かどうかを確認する
            boolean skipJapanize = false;
            String marker = config.getNoneJapanizeMarker();
            if ( !marker.equals("") && message.startsWith(marker) ) {
                skipJapanize = true;
                message = message.substring(marker.length());
            }

            // 2byteコードを含むなら、Japanize変換は行わない
            if ( !skipJapanize &&
                    ( message.getBytes().length > message.length() ||
                      message.matches("[ \\uFF61-\\uFF9F]+") ) ) {
                skipJapanize = true;
            }

            // Japanize変換と、発言処理
            if ( !skipJapanize &&
                    LunaChat.getInstance().getManager().isPlayerJapanize(player.getName()) &&
                    config.getJapanizeType() != JapanizeType.NONE ) {

                int lineType = config.getJapanizeDisplayLine();
                String taskFormat;
                if ( lineType == 1 ) {

                    taskFormat = config.getJapanizeLine1Format();

                    String japanized = api.japanize(
                            message, config.getJapanizeType());
                    if ( japanized != null ) {
                        String temp = taskFormat.replace("%msg", message);
                        temp = temp.replace("%japanize", japanized);
                        message = Utility.replaceColorCode(temp);
                    }

                } else {

                    taskFormat = config.getJapanizeLine2Format();

                    DelayedJapanizeConvertTask task = new DelayedJapanizeConvertTask(message,
                            config.getJapanizeType(), null, player, taskFormat, null);

                    // 発言処理を必ず先に実施させるため、遅延を入れてタスクを実行する。
                    int wait = config.getJapanizeWait();
                    Bukkit.getScheduler().runTaskLater(LunaChat.getInstance(), task, wait);
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

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        String gcName = config.getGlobalChannel();

        // チャンネルが存在しない場合は作成する
        Channel global = api.getChannel(gcName);
        if ( global == null ) {
            global = api.createChannel(gcName);
        }

        // デフォルト発言先が無いなら、グローバルチャンネルに設定する
        Channel dchannel = api.getDefaultChannel(player.getName());
        if ( dchannel == null ) {
            api.setDefaultChannel(player.getName(), gcName);
        }

        return true;
    }

    /**
     * 強制参加チャンネルへ参加させる
     * @param player プレイヤー
     */
    private void forceJoinToForceJoinChannels(Player player) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        List<String> forceJoinChannels = config.getForceJoinChannels();
        String playerName = player.getName();

        for ( String cname : forceJoinChannels ) {

            // チャンネルが存在しない場合は作成する
            Channel channel = api.getChannel(cname);
            if ( channel == null ) {
                channel = api.createChannel(cname);
            }

            // チャンネルのメンバーでないなら、参加する
            if ( !channel.getMembers().contains(playerName) ) {
                channel.addMember(playerName);
            }

            // デフォルト発言先が無いなら、グローバルチャンネルに設定する
            Channel dchannel = api.getDefaultChannel(playerName);
            if ( dchannel == null ) {
                api.setDefaultChannel(playerName, cname);
            }
        }
    }

    /**
     * 通常チャットのフォーマット設定のキーワードを置き換えして返す
     * @param org フォーマット設定
     * @param player 発言プレイヤー
     * @return キーワード置き換え済みの文字列
     */
    private String replaceNormalChatFormatKeywords(String org, Player player) {

        String format = org;
        format = format.replace("%username", "%1$s");
        format = format.replace("%msg", "%2$s");

        if ( format.contains("%prefix") || format.contains("%suffix") ) {

            String prefix = "";
            String suffix = "";
            VaultChatBridge vaultchat = LunaChat.getInstance().getVaultChat();
            if ( vaultchat != null ) {
                prefix = vaultchat.getPlayerPrefix(player);
                suffix = vaultchat.getPlayerSuffix(player);
            }
            format = format.replace("%prefix", prefix);
            format = format.replace("%suffix", suffix);
        }

        return Utility.replaceColorCode(format);

    }
}
