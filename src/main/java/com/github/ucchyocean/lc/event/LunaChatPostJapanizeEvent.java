/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

/**
 * Japanize変換が行われた後に呼び出されるイベント
 * @author ucchy
 */
public class LunaChatPostJapanizeEvent extends LunaChatBaseEvent {

    private String original;
    private String japanized;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     * @param original 変換前の文字列
     * @param japanized 変換後の文字列
     */
    public LunaChatPostJapanizeEvent(String channelName,
            String original, String japanized) {
        super(channelName);
        this.original = original;
        this.japanized = japanized;
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
