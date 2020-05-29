/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * Japanize2行表示のときに、変換結果を遅延してチャンネルに表示するためのタスク
 * @author ucchy
 */
public class JapanizeChannelChatTask implements Runnable {

    private Channel channel;
    private ChannelMember player;
    private String lineFormat;

    private JapanizeConvertTask task;

    /**
     * コンストラクタ
     * @param org 変換前の文字列
     * @param type 変換タイプ
     * @param channel 変換後に発言する、発言先チャンネル
     * @param player 発言したプレイヤー
     * @param japanizeFormat 変換後に発言するときの、発言フォーマット
     * @param lineFormat
     */
    public JapanizeChannelChatTask(String org, JapanizeType type, Channel channel,
            ChannelMember player, String japanizeFormat, String lineFormat) {

        task = new JapanizeConvertTask(org, type, japanizeFormat);
        this.channel = channel;
        this.player = player;
        this.lineFormat = lineFormat;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if ( task.runSync() ) {
            // チャンネルへ送信
            String name = (player != null) ? player.getDisplayName() : "";
            channel.sendMessage(player, task.getResult(), lineFormat, true, name);
        }
    }
}
