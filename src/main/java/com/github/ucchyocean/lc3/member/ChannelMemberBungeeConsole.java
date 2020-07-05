/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.member;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * ChannelMemberのBungee-ConsoleCommandSender実装
 * @author ucchy
 */
public class ChannelMemberBungeeConsole extends ChannelMemberBungee {

    private CommandSender sender;

    /**
     * コンストラクタ
     * @param sender ConsoleのCommandSender
     */
    public ChannelMemberBungeeConsole(CommandSender sender) {
        this.sender = sender;
    }

    /**
     * BungeeのProxiedPlayerを取得する
     * @return 常にnullが返される
     * @see com.github.ucchyocean.lc3.member.ChannelMemberBungee#getPlayer()
     */
    @Override
    public ProxiedPlayer getPlayer() {
        return null;
    }

    /**
     * 発言者が今いるサーバーを取得する
     * @return 常にnullが返される
     * @see com.github.ucchyocean.lc3.member.ChannelMemberBungee#getServer()
     */
    @Override
    public Server getServer() {
        return null;
    }

    /**
     * 発言者がオンラインかどうかを取得する
     * @return 常にtrueが返される
     * @see com.github.ucchyocean.lc3.member.ChannelMember#isOnline()
     */
    @Override
    public boolean isOnline() {
        return true;
    }

    /**
     * 発言者名を取得する
     * @return 発言者名
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getName()
     */
    @Override
    public String getName() {
        return sender.getName();
    }

    /**
     * 発言者の表示名を取得する
     * @return 発言者の表示名
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return sender.getName();
    }

    /**
     * プレフィックスを返す
     * @return 常に空文字列
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getPrefix()
     */
    @Override
    public String getPrefix() {
        return null;
    }

    /**
     * サフィックスを返す
     * @return 常に空文字列
     * @see com.github.ucchyocean.lc3.member.ChannelMember#getSuffix()
     */
    @Override
    public String getSuffix() {
        return null;
    }

    /**
     * 発言者にメッセージを送信する
     * @param message メッセージ
     * @see com.github.ucchyocean.lc3.member.ChannelMember#sendMessage(java.lang.String)
     */
    @Override
    public void sendMessage(String message) {
        if ( message == null || message.isEmpty() ) return;
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * メッセージを送る
     * @param message 送るメッセージ
     * @see com.github.ucchyocean.lc3.member.ChannelMember#sendMessage(net.md_5.bungee.api.chat.BaseComponent[])
     */
    public void sendMessage(BaseComponent[] message) {
        if ( message == null || message.length == 0 ) return;
        sender.sendMessage(message);
    }

    /**
     * 指定されたパーミッションノードの権限を持っているかどうかを取得する
     * @param node パーミッションノード
     * @return 権限を持っているかどうか
     * @see com.github.ucchyocean.lc3.member.ChannelMember#hasPermission(java.lang.String)
     */
    @Override
    public boolean hasPermission(String node) {
        return sender.hasPermission(node);
    }

    /**
     * 指定されたパーミッションノードが定義されているかどうかを取得する
     * @param node パーミッションノード
     * @return 定義を持っているかどうか
     * @see com.github.ucchyocean.lc3.member.ChannelMember#isPermissionSet(java.lang.String)
     */
    @Override
    public boolean isPermissionSet(String node) {
        return sender.getPermissions().contains(node);
    }

    /**
     * 指定されたメッセージの内容を発言する
     * @param message メッセージ
     * @see com.github.ucchyocean.lc3.member.ChannelMember#chat(java.lang.String)
     */
    public void chat(String message) {
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText("<" + getName() + ">" + message));
    }

    /**
     * IDを返す
     * @return 名前をそのまま返す
     * @see com.github.ucchyocean.lc3.member.ChannelMember#toString()
     */
    @Override
    public String toString() {
        return getName();
    }
}
