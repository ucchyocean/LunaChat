/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.member;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * テスト用のダミーメンバー
 * @author ucchy
 */
public class ChannelMemberDummy extends ChannelMember {

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#isOnline()
     */
    @Override
    public boolean isOnline() {
        return true;
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getName()
     */
    @Override
    public String getName() {
        return "ucchy";
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return "うっちぃ";
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getPrefix()
     */
    @Override
    public String getPrefix() {
        return "[P]";
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getSuffix()
     */
    @Override
    public String getSuffix() {
        return "[S]";
    }

    /**
     * @param message
     * @see com.github.ucchyocean.lc3.member.ChannelMember#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    /**
     * @param message
     * @see com.github.ucchyocean.lc3.member.ChannelMember#sendMessage(net.md_5.bungee.api.chat.BaseComponent[])
     */
    @Override
    public void sendMessage(BaseComponent[] message) {
        System.out.println(message);
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getWorldName()
     */
    @Override
    public String getWorldName() {
        return "w";
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getServerName()
     */
    @Override
    public String getServerName() {
        return "s";
    }

    /**
     * @param node
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#hasPermission(java.lang.String)
     */
    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    /**
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#toString()
     */
    @Override
    public String toString() {
        return "ChannelMemberDummy{name=ucchy}";
    }

    /**
     * @param node
     * @return
     * @see com.github.ucchyocean.lc3.member.ChannelMember#isPermissionSet(java.lang.String)
     */
    @Override
    public boolean isPermissionSet(String node) {
        return true;
    }

    /**
     * @param message
     * @see com.github.ucchyocean.lc3.member.ChannelMember#chat(java.lang.String)
     */
    @Override
    public void chat(String message) {
        // do nothing.
    }

}
