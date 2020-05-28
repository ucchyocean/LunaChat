/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bridge;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;
import com.github.ucchyocean.lc.channel.DelayedJapanizeRecipientChatTask;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.member.ChannelMember;
import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;

/**
 * mcMMO連携クラス
 * @author ucchy
 */
public class McMMOBridge implements Listener {

    /**
     * mcMMOのパーティチャットが発生したときのイベント
     * @param event
     */
    @EventHandler
    public void onMcMMOPartyChatEvent(McMMOPartyChatEvent event) {

        // mcMMOから、パーティのメンバーを取得する
        List<Player> recipients = PartyAPI.getOnlineMembers(event.getParty());

        String message = event.getMessage();
        ChannelMember player = ChannelMember.getChannelMember(event.getSender());
        LunaChatConfig config = LunaChat.getConfig();
        LunaChatAPI api = LunaChat.getAPI();

        // NGワード発言をマスク
        for ( Pattern pattern : config.getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(message);
            if ( matcher.find() ) {
                message = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
            }
        }

        // カラーコード置き換え
        // 置き換え設定になっていて、発言者がパーミッションを持っているなら、置き換えする
        if ( config.isEnableNormalChatColorCode() &&
                player.hasPermission("lunachat.allowcc") ) {
            message = Utility.replaceColorCode(message);
        }

        // 一時的にJapanizeスキップ設定かどうかを確認する
        boolean skipJapanize = false;
        String marker = config.getNoneJapanizeMarker();
        if ( !marker.equals("") && message.startsWith(marker) ) {
            skipJapanize = true;
            message = message.substring(marker.length());
        }

        // 2byteコードを含むなら、Japanize変換は行わない
        String kanaTemp = Utility.stripColorCode(message);
        if ( !skipJapanize &&
                ( kanaTemp.getBytes().length > kanaTemp.length() ||
                        kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
            skipJapanize = true;
        }

        // Japanize変換と、発言処理
        if ( !skipJapanize &&
                LunaChat.getAPI().isPlayerJapanize(player.getName()) &&
                config.getJapanizeType() != JapanizeType.NONE ) {

            int lineType = config.getJapanizeDisplayLine();

            if ( lineType == 1 ) {

                String taskFormat = Utility.replaceColorCode(config.getJapanizeLine1Format());

                String japanized = api.japanize(
                        kanaTemp, config.getJapanizeType());
                if ( japanized != null ) {
                    String temp = taskFormat.replace("%msg", message);
                    message = temp.replace("%japanize", japanized);
                }

            } else {

                String taskFormat = Utility.replaceColorCode(config.getJapanizeLine2Format());

                DelayedJapanizeRecipientChatTask task = new DelayedJapanizeRecipientChatTask(
                        message, config.getJapanizeType(), player, taskFormat, recipients);

                // 発言処理を必ず先に実施させるため、遅延を入れてタスクを実行する。
                int wait = config.getJapanizeWait();
                task.runTaskLater(LunaChatBukkit.getInstance(), wait);
            }
        }

        // 発言内容の設定
        event.setMessage(message);
    }
}
