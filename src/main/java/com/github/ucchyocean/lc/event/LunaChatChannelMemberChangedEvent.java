/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.List;

/**
 * メンバー変更イベント
 * @author ucchy
 */
public class LunaChatChannelMemberChangedEvent extends LunaChatBaseCancellableEvent {

    private List<String> before;
    private List<String> after;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     * @param before 変更前のメンバー
     * @param after 変更後のメンバー
     */
    public LunaChatChannelMemberChangedEvent(
            String channelName, List<String> before, List<String> after) {
        super(channelName);
        this.before = before;
        this.after = after;
    }

    /**
     * 変更前のメンバーリストをかえす
     * @return
     */
    public List<String> getMembersBefore() {
        return before;
    }

    /**
     * 変更後のメンバーリストをかえす
     * @return
     */
    public List<String> getMembersAfter() {
        return after;
    }
}
