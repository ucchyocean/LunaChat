/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 *
 * @author ucchy
 */
public class DummyChannel extends Channel {

    public DummyChannel(String name) {
        super(name);
    }

    /**
     * @param player
     * @param message
     * @param format
     * @param sendDynmap
     * @param displayName
     * @see com.github.ucchyocean.lc3.channel.Channel#sendMessage(com.github.ucchyocean.lc3.member.ChannelMember, java.lang.String, java.lang.String, boolean, java.lang.String)
     */
    @Override
    protected void sendMessage(ChannelMember player, String message, String format, boolean sendDynmap,
            String displayName) {
        System.out.println("room " + getName() + " message : " + message);
    }

}
