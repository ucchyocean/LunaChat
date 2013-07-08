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
