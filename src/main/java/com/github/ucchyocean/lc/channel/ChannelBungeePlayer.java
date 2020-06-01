package com.github.ucchyocean.lc.channel;

import com.github.ucchyocean.lc.channel.ChannelPlayerName;

/**
 * 他のサーバーに居るプレイヤーの情報を格納しておくためのChannelPlayerクラス。
 */
public class ChannelBungeePlayer extends ChannelPlayerName {

    private final String prefix;
    private final String suffix;
    private final String worldName;
    private final String displayName;
    private boolean canUseColorCode;

    public ChannelBungeePlayer(String name, String prefix, String suffix, String worldName, String displayName, boolean canUseColorCode) {
        super(name);
        this.prefix = prefix;
        this.suffix = suffix;
        this.worldName = worldName;
        this.displayName = displayName;
        this.canUseColorCode = canUseColorCode;
    }

    /**
     * 他のサーバーにおけるプレフィックスを適応するためのメソッド。
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 他のサーバーにおけるサフィックスを適応するためのメソッド。
     */
    @Override
    public String getSuffix() {
        return suffix;
    }

    /**
     * 他のサーバーにおけるディスプレイネームを適応するためのメソッド。
     */
    @Override
    public String getDisplayName() {
        if (isOnline()) {
            return getPlayer().getDisplayName();
        } else {
            return displayName;
        }
    }

    /**
     * 他のサーバーの、発言者がいるワールド名を取得するメソッド。
     * 発言当時のワールド名を保存するものであり、このインスタンスを格納していて、発言者がワールド移動した場合
     * は情報に不整合が生じる。
     */
    @Override
    public String getWorldName() {
        if (isOnline()) {
            return super.getWorldName();
        } else {
            return worldName;
        }
    }

    /**
     * カラーコードが使えるかどうかを他のサーバーでも適応するための実装。
     */
    @Override
    public boolean hasPermission(String node) {
        if (node.equals("lunachat.allowcc")) {
            return canUseColorCode;
        }

        return super.hasPermission(node);
    }
}