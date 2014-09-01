/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.channel;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.event.LunaChatPostJapanizeEvent;
import com.github.ucchyocean.lc.japanize.IMEConverter;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.japanize.KanaConverter;

/**
 * Japanize変換を実行して、実行後に発言を行うタスク
 * @author ucchy
 */
public class DelayedJapanizeConvertTask extends BukkitRunnable {

    private static final String REGEX_URL = "https?://[\\w/:%#\\$&\\?\\(\\)~\\.=\\+\\-]+";

    private String org;
    private JapanizeType type;
    private Channel channel;
    private ChannelPlayer player;
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
            ChannelPlayer player, String japanizeFormat, String lineFormat) {
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
                channel.sendMessage(player, result, lineFormat, true);
            } else {
                Bukkit.broadcastMessage(result);

                // 設定に応じてdynmapへ送信する
                if ( LunaChat.getInstance().getLunaChatConfig().
                        isSendBroadcastChannelChatToDynmap() &&
                        LunaChat.getInstance().getDynmap() != null ) {
                    if ( player != null && player.getPlayer() != null )
                        LunaChat.getInstance().getDynmap().chat(player.getPlayer(), result);
                    else
                        LunaChat.getInstance().getDynmap().broadcast(result);
                }
            }
        }
    }

    /**
     * 同期処理で変換を行います。結果は getResult() で取得してください。
     * @return 同期処理を実行したかどうか（イベントでキャンセルされた場合はfalseになります）
     */
    public boolean runSync() {

        // 変換対象外のキーワード
        HashMap<String, String> keywordMap = new HashMap<String, String>();
        ArrayList<String> keywords = new ArrayList<String>();
        if ( LunaChat.getInstance().getLunaChatConfig().isJapanizeIgnorePlayerName() ) {
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                keywords.add(player.getName());
            }
        }

        // カラーコード削除、URL削除
        String deletedURL = Utility.stripColor(org.replaceAll(REGEX_URL, " "));

        // キーワードをロック
        int index = 0;
        String keywordLocked = deletedURL;
        for ( String keyword : keywords ) {
            if ( keywordLocked.contains(keyword) ) {
                index++;
                String key = "＜" + index + "＞";
                keywordLocked = keywordLocked.replace(keyword, key);
                keywordMap.put(key, keyword);
            }
        }

        // カナ変換
        String japanized = KanaConverter.conv(keywordLocked);

        // キーワードのアンロック
        for ( String key : keywordMap.keySet() ) {
            japanized = japanized.replace(key, keywordMap.get(key));
        }

        // IME変換
        if ( type == JapanizeType.GOOGLE_IME ) {
            japanized = IMEConverter.convByGoogleIME(japanized);
        } else if ( type == JapanizeType.SOCIAL_IME ) {
            japanized = IMEConverter.convBySocialIME(japanized);
        }

        // NGワードが含まれている場合は、マスクする
        for ( String word : LunaChat.getInstance().getLunaChatConfig().getNgword() ) {
            if ( japanized.contains(word) ) {
                japanized = japanized.replace(
                        word, Utility.getAstariskString(word.length()));
            }
        }

        // イベントコール
        String channelName = (channel == null) ? "" : channel.getName();
        LunaChatPostJapanizeEvent event =
                new LunaChatPostJapanizeEvent(channelName, player, org, japanized);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if ( event.isCancelled() ) {
            return false;
        }
        japanized = event.getJapanized();

        // フォーマットする
        result = format.replace("%msg", org);
        result = result.replace("%japanize", japanized);

        return true;
    }

    /**
     * Japanize変換の結果を返します。
     * @return 変換結果
     */
    public String getResult() {
        return result;
    }
}

