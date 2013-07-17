/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.event.LunaChatPostJapanizeEvent;
import com.github.ucchyocean.lc.japanize.IMEConverter;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.japanize.KanaConverter;

/**
 * Japanize変換を実行して、実行後に発言を行うタスク
 * @author ucchy
 */
public class DelayedJapanizeConvertTask extends BukkitRunnable {

    private String org;
    private JapanizeType type;
    private Channel channel;
    private Player player;
    private String format;
    private String lineFormat;
    private String result;

    /**
     * コンストラクタ
     * @param org 変換前の文字列
     * @param type 変換タイプ
     * @param channel 変換後に発言する、発言先チャンネル
     * @param player 発言したプレイヤー
     * @param japanizeFormat 変換後に発言するときの、発言フォーマット
     */
    public DelayedJapanizeConvertTask(String org, JapanizeType type, Channel channel,
            Player player, String japanizeFormat, String lineFormat) {
        this.org = org;
        this.type = type;
        this.channel = channel;
        this.player = player;
        this.format = japanizeFormat;
        this.lineFormat = lineFormat;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        if ( runSync() ) {

            // チャンネルへ送信
            if ( channel != null ) {
                channel.sendMessage(player, result, lineFormat);
            } else {
                Bukkit.broadcastMessage(result);
            }
        }
    }

    /**
     * 同期処理で変換を行います。結果は getResult() で取得してください。
     * @return 同期処理を実行したかどうか（イベントでキャンセルされた場合はfalseになります）
     */
    private boolean runSync() {

        // カナ変換
        String japanized = KanaConverter.conv(org);

        // IME変換
        if ( type == JapanizeType.GOOGLE_IME ) {
            japanized = IMEConverter.convByGoogleIME(japanized);
        } else if ( type == JapanizeType.SOCIAL_IME ) {
            japanized = IMEConverter.convBySocialIME(japanized);
        }

        // イベントコール
        String channelName = (channel == null) ? "" : channel.getName();
        LunaChatPostJapanizeEvent event =
                new LunaChatPostJapanizeEvent(channelName, org, japanized);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return false;
        }
        japanized = event.getJapanized();

        // フォーマットする
        result = format.replace("%msg", org);
        result = result.replace("%japanize", japanized);
        result = Utility.replaceColorCode(result);

        return true;
    }
}

