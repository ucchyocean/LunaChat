/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.channel;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * Japanize2行表示のときに、変換結果を遅延して指定した受信者に表示するためのタスク。
 * 他プラグインのチャンネル（mcMMOのパーティチャットや、TownyChatなど）に、Japanize変換結果を表示するために使用する。
 * @author ucchy
 */
public class DelayedJapanizeRecipientChatTask extends DelayedJapanizeConvertTask {

    private List<Player> recipients;

    /**
     * コンストラクタ
     * @param org 変換前の文字列
     * @param type 変換タイプ
     * @param player 発言したプレイヤー
     * @param japanizeFormat 変換後に発言するときの、発言フォーマット
     * @param recipients メッセージ受信者
     */
    public DelayedJapanizeRecipientChatTask(String org, JapanizeType type, ChannelPlayer player,
            String japanizeFormat, List<Player> recipients) {
        super(org, type, null, player, japanizeFormat);
        this.recipients = recipients;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        if ( runSync() ) {

            String result = getResult();

            // 送信
            for ( Player p : recipients ) {
                p.sendMessage(result);
            }
            Bukkit.getConsoleSender().sendMessage(result);
        }
    }
}
