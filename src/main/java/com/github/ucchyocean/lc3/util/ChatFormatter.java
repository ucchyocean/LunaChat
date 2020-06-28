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

import org.jetbrains.annotations.NotNull;
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
public class ChatFormatter {

    private static ChatFormatter instance;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    private static final String JOIN_COMMAND_TEMPLATE = "/lunachat join %s";
    private static final String TELL_COMMAND_TEMPLATE = "/tell %s ";

    private ChatFormatter() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param member 発言者
     * @param channel チャンネル
     * @return 置き換え結果
     */
    public static String replaceKeywordsForChannel(String format, @Nullable ChannelMember member, @NotNull Channel channel) {

        if ( instance == null ) {
            instance = new ChatFormatter();
        }

        LunaChatAPI api = LunaChat.getAPI();

        KeywordReplacer msg = new KeywordReplacer(format);

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

        msg.replace("%ch", String.format(
                "＜type=RUN_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                channel.getName(),
                Messages.hoverChannelName(channel.getName()),
                String.format(JOIN_COMMAND_TEMPLATE, channel.getName())));
        //msg.replace("%msg", message);
        msg.replace("%color", channel.getColorCode());
        if ( channel.getPrivateMessageTo() != null ) {
//            msg.replace("%to", channel.getPrivateMessageTo().getDisplayName());
            msg.replace("%to", String.format(
                    "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                    channel.getPrivateMessageTo().getDisplayName(),
                    Messages.hoverPlayerName(channel.getPrivateMessageTo().getName()),
                    String.format(TELL_COMMAND_TEMPLATE, channel.getPrivateMessageTo().getName())));
            msg.replace("%recieverserver", channel.getPrivateMessageTo().getServerName());
        } else {
            msg.replace("%to", "");
            msg.replace("%recieverserver", "");
        }

        if ( msg.contains("%date") ) {
            msg.replace("%date", instance.dateFormat.format(new Date()));
        }
        if ( msg.contains("%time") ) {
            msg.replace("%time", instance.timeFormat.format(new Date()));
        }

        if ( member != null ) {

            String playerPMPlaceHolder = String.format(
                    "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                    member.getDisplayName(),
                    Messages.hoverPlayerName(member.getName()),
                    String.format(TELL_COMMAND_TEMPLATE, member.getName()));
            msg.replace("%displayname", playerPMPlaceHolder);
            msg.replace("%username", playerPMPlaceHolder);
            msg.replace("%player", String.format(
                    "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                    member.getName(),
                    Messages.hoverPlayerName(member.getName()),
                    String.format(TELL_COMMAND_TEMPLATE, member.getName())));

            if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
                msg.replace("%prefix", member.getPrefix());
                msg.replace("%suffix", member.getSuffix());
            }

            msg.replace("%world", member.getWorldName());
            msg.replace("%server", member.getServerName());

        } else {
            msg.replace("%displayname", "");
            msg.replace("%username", "");
            msg.replace("%player", "");
            msg.replace("%prefix", "");
            msg.replace("%suffix", "");
            msg.replace("%world", "");
            msg.replace("%server", "");
        }

        return Utility.replaceColorCode(msg.toString());
    }

    /**
     * 通常チャットのフォーマット設定のキーワードを置き換えして返す
     * @param format チャットフォーマット
     * @param member 発言者
     * @return キーワード置き換え済みの文字列
     */
    public static String replaceKeywordsForEvent(String format, @NotNull ChannelMember member) {

        if ( instance == null ) {
            instance = new ChatFormatter();
        }

        KeywordReplacer msg = new KeywordReplacer(format);

        String playerPMPlaceHolder = String.format(
                "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                member.getDisplayName(),
                Messages.hoverPlayerName(member.getName()),
                String.format(TELL_COMMAND_TEMPLATE, member.getName()));
        msg.replace("%displayname", playerPMPlaceHolder);
        msg.replace("%username", playerPMPlaceHolder);
        msg.replace("%player", String.format(
                "＜type=SUGGEST_COMMAND text=\"%s\" hover=\"%s\" command=\"%s\"＞",
                member.getName(),
                Messages.hoverPlayerName(member.getName()),
                String.format(TELL_COMMAND_TEMPLATE, member.getName())));

//        msg.replace("%msg", "%2$s");

        if ( msg.contains("%date") ) {
            msg.replace("%date", instance.dateFormat.format(new Date()));
        }
        if ( msg.contains("%time") ) {
            msg.replace("%time", instance.timeFormat.format(new Date()));
        }

        if ( msg.contains("%prefix") || msg.contains("%suffix") ) {
            msg.replace("%prefix", member.getPrefix());
            msg.replace("%suffix", member.getSuffix());
        }

        msg.replace("%world", member.getWorldName());
        msg.replace("%server", member.getServerName());

        return Utility.replaceColorCode(msg.toString());
    }


    public static BaseComponent[] replaceTextComponent(String src) {

        List<BaseComponent> components = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "＜type=(SUGGEST_COMMAND|RUN_COMMAND) text=\"([^\"]*)\" hover=\"([^\"]*)\" command=\"([^\"]*)\"＞");
        Matcher matcher = pattern.matcher(src);
        int lastIndex = 0;

        while ( matcher.find() ) {

            // マッチする箇所までの文字列を取得する
            if ( lastIndex < matcher.start() ) {
                for ( BaseComponent c : TextComponent.fromLegacyText(src.substring(lastIndex, matcher.start())) ) {
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
            } else {
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
            }
            components.add(tc);

            lastIndex = matcher.end() + 1;
        }

        if ( lastIndex < src.length() - 1 ) {
            // 残りの部分の文字列を取得する
            for ( BaseComponent c : TextComponent.fromLegacyText(src.substring(lastIndex)) ) {
                components.add(c);
            }
        }

        BaseComponent[] result = new BaseComponent[components.size()];
        components.toArray(result);
        return result;
    }

}
