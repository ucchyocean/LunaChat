/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.event;

import java.util.List;

/**
 * @author ucchy
 * メンバー変更イベント
 */
public class LunaChatChannelMemberChangedEvent extends LunaChatBaseEvent {

    private List<String> members;

    public LunaChatChannelMemberChangedEvent(String channelName, List<String> members) {
        super(channelName);
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }
}
