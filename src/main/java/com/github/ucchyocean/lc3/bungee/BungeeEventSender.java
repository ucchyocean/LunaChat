/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee;

import java.util.List;
import java.util.Map;

import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelChatEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelCreateEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelMemberChangedEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelMessageEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelOptionChangedEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeeChannelRemoveEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeePostJapanizeEvent;
import com.github.ucchyocean.lc3.bungee.event.LunaChatBungeePreChatEvent;
import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.event.EventSenderInterface;
import com.github.ucchyocean.lc3.member.ChannelMember;

import net.md_5.bungee.api.ProxyServer;

/**
 * Bungeeのイベント実行クラス
 * @author ucchy
 */
public class BungeeEventSender implements EventSenderInterface {

    /**
     * チャンネルチャットのチャットイベント
     * @param channelName チャンネル名
     * @param player 発言者
     * @param originalMessage 発言内容
     * @param ngMaskedMessage 発言内容（NGマスク後）
     * @param messageFormat 発言に適用されるフォーマット
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelChatEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public EventResult sendLunaChatChannelChatEvent(String channelName, ChannelMember member, String originalMessage,
            String ngMaskedMessage, String messageFormat) {

        LunaChatBungeeChannelChatEvent event =
                new LunaChatBungeeChannelChatEvent(
                        channelName, member, originalMessage, ngMaskedMessage, messageFormat);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setNgMaskedMessage(event.getNgMaskedMessage());
        result.setMessageFormat(event.getMessageFormat());
        return result;
    }

    /**
     * チャンネル作成イベント
     * @param channelName チャンネル名
     * @param member 作成した人
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelCreateEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember)
     */
    @Override
    public EventResult sendLunaChatChannelCreateEvent(String channelName, ChannelMember member) {

        LunaChatBungeeChannelCreateEvent event =
                new LunaChatBungeeChannelCreateEvent(channelName, member);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setChannelName(event.getChannelName());
        return result;
    }

    /**
     * メンバー変更イベント
     * @param channelName チャンネル名
     * @param before 変更前のメンバー
     * @param after 変更後のメンバー
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelMemberChangedEvent(java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public EventResult sendLunaChatChannelMemberChangedEvent(String channelName, List<ChannelMember> before,
            List<ChannelMember> after) {

        LunaChatBungeeChannelMemberChangedEvent event =
                new LunaChatBungeeChannelMemberChangedEvent(channelName, before, after);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        return result;
    }

    /**
     * チャンネルチャットのメッセージイベント。このイベントはキャンセルできない。
     * @param channelName チャンネル名
     * @param member 発言者
     * @param message 発言内容（NGマスクやJapanizeされた後の内容）
     * @param recipients 受信者
     * @param displayName 発言者の表示名
     * @param originalMessage 発言内容（元々の内容）
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelMessageEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember, java.lang.String, java.util.ArrayList, java.lang.String, java.lang.String)
     */
    @Override
    public EventResult sendLunaChatChannelMessageEvent(String channelName, ChannelMember member, String message,
            List<ChannelMember> recipients, String displayName, String originalMessage) {

        LunaChatBungeeChannelMessageEvent event =
                new LunaChatBungeeChannelMessageEvent(
                        channelName, member, message, recipients, displayName, originalMessage);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setMessage(event.getMessage());
        result.setRecipients(event.getRecipients());
        return result;
     }

    /**
     * オプション変更イベント
     * @param channelName チャンネル名
     * @param member オプションを変更した人
     * @param options 変更後のオプション
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelOptionChangedEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember, java.util.HashMap)
     */
    @Override
    public EventResult sendLunaChatChannelOptionChangedEvent(String channelName, ChannelMember member,
            Map<String, String> options) {

        LunaChatBungeeChannelOptionChangedEvent event =
                new LunaChatBungeeChannelOptionChangedEvent(channelName, member, options);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setOptions(event.getOptions());
        return result;
    }

    /**
     * チャンネル削除イベント
     * @param channelName チャンネル名
     * @param member 削除を実行した人
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatChannelRemoveEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember)
     */
    @Override
    public EventResult sendLunaChatChannelRemoveEvent(String channelName, ChannelMember member) {

        LunaChatBungeeChannelRemoveEvent event = new LunaChatBungeeChannelRemoveEvent(channelName, member);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setChannelName(event.getChannelName());
        return result;
    }

    /**
     * Japanize変換が行われた後に呼び出されるイベント
     * @param channelName チャンネル名
     * @param member 発言したメンバー
     * @param original 変換前の文字列
     * @param japanized 変換後の文字列
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatPostJapanizeEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember, java.lang.String, java.lang.String)
     */
    @Override
    public EventResult sendLunaChatPostJapanizeEvent(String channelName, ChannelMember member, String original,
            String japanized) {

        LunaChatBungeePostJapanizeEvent event =
                new LunaChatBungeePostJapanizeEvent(channelName, member, original, japanized);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setJapanized(event.getJapanized());
        return result;
    }

    /**
     * チャンネルチャットへの発言前に発生するイベント
     * @param channelName チャンネル名
     * @param member 発言したメンバー
     * @param message 発言内容
     * @return イベント実行結果
     * @see com.github.ucchyocean.lc3.event.EventSenderInterface#sendLunaChatPreChatEvent(java.lang.String, com.github.ucchyocean.lc3.member.ChannelMember, java.lang.String)
     */
    @Override
    public EventResult sendLunaChatPreChatEvent(String channelName, ChannelMember member, String message) {

        LunaChatBungeePreChatEvent event =
                new LunaChatBungeePreChatEvent(channelName, member, message);
        event = ProxyServer.getInstance().getPluginManager().callEvent(event);

        EventResult result = new EventResult();
        result.setCancelled(event.isCancelled());
        result.setMessage(event.getMessage());
        return result;
    }
}
