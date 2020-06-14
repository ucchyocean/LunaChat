/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bukkit.event;

import java.util.List;

import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * メンバー変更イベント
 * @author ucchy
 */
public class LunaChatBukkitChannelMemberChangedEvent extends LunaChatBukkitBaseCancellableEvent {

    private List<ChannelMember> before;
    private List<ChannelMember> after;

    /**
     * コンストラクタ
     * @param channelName チャンネル名
     * @param before 変更前のメンバー
     * @param after 変更後のメンバー
     */
    public LunaChatBukkitChannelMemberChangedEvent(
            String channelName, List<ChannelMember> before, List<ChannelMember> after) {
        super(channelName);
        this.before = before;
        this.after = after;
    }

    /**
     * 変更前のメンバーリストをかえす
     * @return
     */
    public List<ChannelMember> getMembersBefore() {
        return before;
    }

    /**
     * 変更後のメンバーリストをかえす
     * @return
     */
    public List<ChannelMember> getMembersAfter() {
        return after;
    }
}
