/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * チャットのフォーマットを作成するユーティリティクラス
 * @author ucchy
 */
public class ClickableFormat {

    private static final String JOIN_COMMAND_TEMPLATE = "/lunachat join %s";
    private static final String TELL_COMMAND_TEMPLATE = "/tell %s";

    private static final String PLACEHOLDER_RUN_COMMAND =
            "＜type=RUN_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞";
    private static final String PLACEHOLDER_SUGGEST_COMMAND =
            "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞";
    private static final String PLACEHOLDER_PATTERN =
            "＜type=(SUGGEST_COMMAND|RUN_COMMAND) text=\"([^\"]*)\" hover=\"([^\"]*)\" command=\"([^\"]*)\"＞";

    private KeywordReplacer message;

    private ClickableFormat(KeywordReplacer message) {
        this.message = message;
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param member 発言者
     * @return 置き換え結果
     */
    public static ClickableFormat makeFormat(String format, @Nullable ChannelMember member) {
        return makeFormat(format, member, null, true);
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param member 発言者
     * @param channel チャンネル
     * @param withPlayerLink プレイヤー名の箇所にクリック可能なプレースホルダーを挿入するか
     * @return 置き換え結果
     */
    public static ClickableFormat makeFormat(String format,
            @Nullable ChannelMember member, @Nullable Channel channel, boolean withPlayerLink) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        LunaChatAPI api = LunaChat.getAPI();

        KeywordReplacer msg = new KeywordReplacer(format);

        //msg.replace("%msg", message);

        if ( channel != null ) {

            // テンプレートのキーワードを、まず最初に置き換える
            for ( int i=0; i<=9; i++ ) {
                String key = "%" + i;
                if ( msg.contains(key) ) {
                    if ( api.getTemplate("" + i) != null ) {
                        msg.replace(key, api.getTemplate("" + i));
                        break;
                    }
                }
            }

            // チャンネル関連のキーワード置き換え
            msg.replace("%ch", String.format(
                    PLACEHOLDER_RUN_COMMAND,
                    channel.getName(),
                    Messages.hoverChannelName(channel.getName()),
                    String.format(JOIN_COMMAND_TEMPLATE, channel.getName())));
            msg.replace("%color", channel.getColorCode());
            if ( channel.getPrivateMessageTo() != null ) {
                msg.replace("%to", String.format(
                        PLACEHOLDER_SUGGEST_COMMAND,
                        channel.getPrivateMessageTo().getDisplayName(),
                        Messages.hoverPlayerName(channel.getPrivateMessageTo().getName()),
                        String.format(TELL_COMMAND_TEMPLATE, channel.getPrivateMessageTo().getName())));
                msg.replace("%recieverserver", channel.getPrivateMessageTo().getServerName());
            }
        }

        if ( msg.contains("%date") ) {
            msg.replace("%date", dateFormat.format(new Date()));
        }
        if ( msg.contains("%time") ) {
            msg.replace("%time", timeFormat.format(new Date()));
        }

        if ( member != null ) {

            // ChannelMember関連のキーワード置き換え
            if ( withPlayerLink ) {
                String playerPMPlaceHolder = String.format(
                        PLACEHOLDER_SUGGEST_COMMAND,
                        member.getDisplayName(),
                        Messages.hoverPlayerName(member.getName()),
                        String.format(TELL_COMMAND_TEMPLATE, member.getName()));
                msg.replace("%displayname", playerPMPlaceHolder);
                msg.replace("%username", playerPMPlaceHolder);
                msg.replace("%player", String.format(
                        PLACEHOLDER_SUGGEST_COMMAND,
                        member.getName(),
                        Messages.hoverPlayerName(member.getName()),
                        String.format(TELL_COMMAND_TEMPLATE, member.getName())));
            } else {
                msg.replace("%displayname", member.getDisplayName());
                msg.replace("%username", member.getDisplayName());
                msg.replace("%player", member.getName());
            }

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                msg.replace("%prefix", member.getPrefix());
                msg.replace("%suffix", member.getSuffix());
            }

            msg.replace("%world", member.getWorldName());
            msg.replace("%server", member.getServerName());
        }

        return new ClickableFormat(msg);
    }


    public BaseComponent[] makeTextComponent() {

        message.translateColorCode();

        List<BaseComponent> components = new ArrayList<>();
        Matcher matcher = Pattern.compile(PLACEHOLDER_PATTERN).matcher(message.getStringBuilder());
        int lastIndex = 0;

        while ( matcher.find() ) {

            // マッチする箇所までの文字列を取得する
            if ( lastIndex < matcher.start() ) {
                for ( BaseComponent c : TextComponent.fromLegacyText(message.substring(lastIndex, matcher.start())) ) {
                    components.add(c);
                }
            }

            // マッチした箇所の文字列を解析して追加する
            String type = matcher.group(1);
            String text = matcher.group(2);
            String hover = matcher.group(3);
            String command = matcher.group(4);
            TextComponent tc = new TextComponent(text);
            if ( !hover.isEmpty() ) {
                tc.setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
            }
            if ( type.equals("RUN_COMMAND") ) {
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            } else { // type.equals("SUGGEST_COMMAND")
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
            }
            components.add(tc);

            lastIndex = matcher.end();
        }

        if ( lastIndex < message.length() - 1 ) {
            // 残りの部分の文字列を取得する
            for ( BaseComponent c : TextComponent.fromLegacyText(message.substring(lastIndex)) ) {
                components.add(c);
            }
        }

        BaseComponent[] result = new BaseComponent[components.size()];
        components.toArray(result);
        return result;
    }

    public String toLegacyText() {

        StringBuilder msg = new StringBuilder(message.toString());
        Matcher matcher = Pattern.compile(PLACEHOLDER_PATTERN).matcher(msg);

        while ( matcher.find(0) ) {
            String text = matcher.group(2);
            msg.replace(matcher.start(), matcher.end(), text);
        }

        return msg.toString();
    }

    @Override
    public String toString() {
        return message.toString();
    }

    public void replace(String keyword, String value) {
        message.replace(keyword, value);
    }
}
