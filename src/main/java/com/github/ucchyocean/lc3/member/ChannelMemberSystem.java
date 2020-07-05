package com.github.ucchyocean.lc3.member;

import net.md_5.bungee.api.chat.BaseComponent;

public class ChannelMemberSystem extends ChannelMember {

    private static final String NAME = "system";
    private static final ChannelMember instance = new ChannelMemberSystem();

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public void sendMessage(String message) {
        // do nothing.
    }

    @Override
    public void sendMessage(BaseComponent[] message) {
        // do nothing.
    }

    @Override
    public String getWorldName() {
        return "";
    }

    @Override
    public String getServerName() {
        return "";
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public boolean isPermissionSet(String node) {
        return true;
    }

    @Override
    public void chat(String message) {
        // do nothing.
    }

    public static ChannelMember getInstance() {
        return instance;
    }
}
