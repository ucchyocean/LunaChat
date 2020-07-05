package com.github.ucchyocean.lc3.member;

import net.md_5.bungee.api.chat.BaseComponent;

public class ChannelMemberOther extends ChannelMember {

    private String name;
    private String displayName;

    public ChannelMemberOther(String name) {
        this.name = name;
        this.displayName = name;
    }

    public ChannelMemberOther(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
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
        return String.format("%s:{name=%s, displayName=%s}",
                this.getClass().toString(), name, displayName);
    }

    @Override
    public boolean isPermissionSet(String node) {
        return true;
    }

    @Override
    public void chat(String message) {
        // do nothing.
    }

}
