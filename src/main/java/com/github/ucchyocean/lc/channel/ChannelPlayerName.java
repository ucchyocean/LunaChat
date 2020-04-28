/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.channel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.bridge.VaultChatBridge;

/**
 * 名前管理のプレイヤー
 * @author ucchy
 */
public class ChannelPlayerName extends ChannelPlayer {

    private String name;

    /**
     * コンストラクタ
     * @param name プレイヤー名
     */
    public ChannelPlayerName(String name) {
        this.name = name;
    }

    /**
     * オンラインかどうか
     * @return オンラインかどうか
     */
    @Override
    public boolean isOnline() {
        Player player = getPlayer();
        return (player != null && player.isOnline());
    }

    /**
     * プレイヤー名を返す
     * @return プレイヤー名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * プレイヤー表示名を返す
     * @return プレイヤー表示名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        Player player = getPlayer();
        if ( player != null ) {
            return player.getDisplayName();
        }
        return name;
    }

    /**
     * プレフィックスを返す
     * @return プレフィックス
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getPrefix()
     */
    @Override
    public String getPrefix() {
        VaultChatBridge vault = LunaChat.getInstance().getVaultChat();
        if ( vault == null ) {
            return "";
        }
        Player player = getPlayer();
        if ( player != null ) {
            return vault.getPlayerPrefix(player);
        }
        return "";
    }

    /**
     * サフィックスを返す
     * @return サフィックス
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getSuffix()
     */
    @Override
    public String getSuffix() {
        VaultChatBridge vault = LunaChat.getInstance().getVaultChat();
        if ( vault == null ) {
            return "";
        }
        Player player = getPlayer();
        if ( player != null ) {
            return vault.getPlayerSuffix(player);
        }
        return "";
    }

    /**
     * メッセージを送る
     * @param message 送るメッセージ
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String message) {
        Player player = getPlayer();
        if ( player != null ) {
            player.sendMessage(message);
        }
    }

    /**
     * BukkitのPlayerを取得する
     * @return Player
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getPlayer()
     */
    @Override
    public Player getPlayer() {
        return Bukkit.getPlayerExact(name);
    }

    /**
     * 発言者が今いるワールドのワールド名を取得する
     * @return ワールド名
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getWorldName()
     */
    @Override
    public String getWorldName() {
        Player player = getPlayer();
        if ( player != null ) {
            return player.getWorld().getName();
        }
        return "-";
    }

    /**
     * 発言者が今いる位置を取得する
     * @return 発言者の位置
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getLocation()
     */
    @Override
    public Location getLocation() {
        Player player = getPlayer();
        if ( player != null ) {
            return player.getLocation();
        }
        return null;
    }

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#hasPermission(java.lang.String)
     */
    @Override
    public boolean hasPermission(String node) {
        Player player = getPlayer();
        if ( player == null ) {
            return false;
        } else {
            return player.hasPermission(node);
        }
    }

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#isPermissionSet(java.lang.String)
     */
    @Override
    public boolean isPermissionSet(String node) {
        Player player = getPlayer();
        if ( player == null ) {
            return false;
        } else {
            return player.isPermissionSet(node);
        }
    }

    /**
     * 指定されたCommandSenderと同一かどうかを返す
     * @param sender
     * @return 同一かどうか
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#equals(org.bukkit.entity.Player)
     */
    @Override
    public boolean equals(CommandSender sender) {
        if ( sender == null || !(sender instanceof Player) ) {
            return false;
        }
        Player player = (Player)sender;
        return name.equals(player.getName());
    }

    /**
     * IDを返す
     * @return 名前をそのまま返す
     * @see com.github.ucchyocean.lc.channel.ChannelPlayer#getID()
     */
    @Override
    public String toString() {
        return name;
    }
}
