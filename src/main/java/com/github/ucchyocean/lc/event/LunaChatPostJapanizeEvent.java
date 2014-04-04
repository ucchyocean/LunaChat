/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import org.bukkit.entity.Player;

/**
 * Japanize変換が行われた後に呼び出されるイベント
 * @author ucchy
 */
public class LunaChatPostJapanizeEvent extends LunaChatBaseCancellableEvent {

    private Player player;
    private String original;
    private String japanized;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     * @param player 発言したプレイヤー
     * @param original 変換前の文字列
     * @param japanized 変換後の文字列
     */
    public LunaChatPostJapanizeEvent(String channelName, Player player,
            String original, String japanized) {
        super(channelName);
        this.player = player;
        this.original = original;
        this.japanized = japanized;
    }

    /**
     * 発言を行ったプレイヤーを取得します。
     * @return 発言したプレイヤー
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Japanize変換後の文字列を返す
     * @return 変換後の文字列
     */
    public String getJapanized() {
        return japanized;
    }

    /**
     * Japanize変換後の文字列を差し替える
     * @param japanized 変換後の文字列
     */
    public void setJapanized(String japanized) {
        this.japanized = japanized;
    }

    /**
     * Japanize変換前の文字列を返す
     * @return 変換前の文字列
     */
    public String getOriginal() {
        return original;
    }
}
