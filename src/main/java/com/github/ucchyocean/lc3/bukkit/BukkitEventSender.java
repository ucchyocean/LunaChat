/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bukkit;

import java.util.List;
import java.util.Map;

import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.event.EventSenderInterface;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * Bukkitのイベント実行クラス
 * @author ucchy
 */
public class BukkitEventSender implements EventSenderInterface {

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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
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
        // TODO 未実装
        return null;
    }
}
