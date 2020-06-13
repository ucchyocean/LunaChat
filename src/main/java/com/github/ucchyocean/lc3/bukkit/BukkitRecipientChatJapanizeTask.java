/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bukkit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc3.channel.JapanizeConvertTask;
import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * Japanize2行表示のときに、変換結果を遅延して指定した受信者に表示するためのタスク。
 * 他プラグインのチャンネル（mcMMOのパーティチャットや、TownyChatなど）に、Japanize変換結果を表示するために使用する。
 * @author ucchy
 */
public class BukkitRecipientChatJapanizeTask extends BukkitRunnable {

    private List<Player> recipients;

    private JapanizeConvertTask task;

    /**
     * コンストラクタ
     * @param org 変換前の文字列
     * @param type 変換タイプ
     * @param player 発言したプレイヤー
     * @param japanizeFormat 変換後に発言するときの、発言フォーマット
     * @param recipients メッセージ受信者
     */
    public BukkitRecipientChatJapanizeTask(String org, JapanizeType type, ChannelMember player,
            String japanizeFormat, List<Player> recipients) {
        task = new JapanizeConvertTask(org, type, japanizeFormat, null, player);
        this.recipients = recipients;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        if ( task.runSync() ) {

            String result = task.getResult();

            // 送信
            for ( Player p : recipients ) {
                p.sendMessage(result);
            }
            Bukkit.getConsoleSender().sendMessage(result);
        }
    }
}
