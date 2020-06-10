/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * LunaChatのイベント実行インターフェイス
 * @author ucchy
 */
public interface EventSenderInterface {

    /**
     * チャンネルチャットのチャットイベント
     * @param channelName チャンネル名
     * @param player 発言者
     * @param originalMessage 発言内容
     * @param ngMaskedMessage 発言内容（NGマスク後）
     * @param messageFormat 発言に適用されるフォーマット
     * @return イベント実行結果
     */
    public EventResult sendLunaChatChannelChatEvent(
            String channelName, ChannelMember member,
            String originalMessage, String ngMaskedMessage,
            String messageFormat);

    /**
     * チャンネル作成イベント
     * @param channelName チャンネル名
     * @param member 作成した人
     * @return イベント実行結果
     */
    public EventResult sendLunaChatChannelCreateEvent(
            String channelName, ChannelMember member);

    /**
     * メンバー変更イベント
     * @param channelName チャンネル名
     * @param before 変更前のメンバー
     * @param after 変更後のメンバー
     * @return イベント実行結果
     */
    public EventResult sendLunaChatChannelMemberChangedEvent(
            String channelName, List<ChannelMember> before,
            List<ChannelMember> after);

    /**
     * チャンネルチャットのメッセージイベント。このイベントはキャンセルできない。
     * @param channelName チャンネル名
     * @param member 発言者
     * @param message 発言内容（NGマスクやJapanizeされた後の内容）
     * @param recipients 受信者
     * @param displayName 発言者の表示名
     * @param originalMessage 発言内容（元々の内容）
     */
    public void sendLunaChatChannelMessageEvent(
            String channelName, ChannelMember member, String message,
            ArrayList<ChannelMember> recipients, String displayName,
            String originalMessage);

    /**
     * オプション変更イベント
     * @param channelName チャンネル名
     * @param member オプションを変更した人
     * @param options 変更後のオプション
     * @return イベント実行結果
     */
    public EventResult sendLunaChatChannelOptionChangedEvent(
            String channelName, ChannelMember member,
            HashMap<String, String> options);

    /**
     * チャンネル削除イベント
     * @param channelName チャンネル名
     * @param member 削除を実行した人
     * @return イベント実行結果
     */
    public EventResult sendLunaChatChannelRemoveEvent(
            String channelName, ChannelMember member);

    /**
     * Japanize変換が行われた後に呼び出されるイベント
     * @param channelName チャンネル名
     * @param member 発言したメンバー
     * @param original 変換前の文字列
     * @param japanized 変換後の文字列
     * @return イベント実行結果
     */
    public EventResult sendLunaChatPostJapanizeEvent(
            String channelName, ChannelMember member,
            String original, String japanized);

    /**
     * チャンネルチャットへの発言前に発生するイベント
     * @param channelName チャンネル名
     * @param member 発言したメンバー
     * @param message 発言内容
     * @return イベント実行結果
     */
    public EventResult sendLunaChatPreChatEvent(
            String channelName, ChannelMember member, String message);
}
