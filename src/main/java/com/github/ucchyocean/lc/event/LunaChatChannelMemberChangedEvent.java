/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.event;

import java.util.List;

import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * メンバー変更イベント
 * @author ucchy
 */
public class LunaChatChannelMemberChangedEvent extends LunaChatBaseCancellableEvent {

    private List<ChannelPlayer> before;
    private List<ChannelPlayer> after;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     * @param before 変更前のメンバー
     * @param after 変更後のメンバー
     */
    public LunaChatChannelMemberChangedEvent(
            String channelName, List<ChannelPlayer> before, List<ChannelPlayer> after) {
        super(channelName);
        this.before = before;
        this.after = after;
    }

    /**
     * 変更前のメンバーリストをかえす
     * @return
     */
    public List<ChannelPlayer> getMembersBefore() {
        return before;
    }

    /**
     * 変更後のメンバーリストをかえす
     * @return
     */
    public List<ChannelPlayer> getMembersAfter() {
        return after;
    }
}
