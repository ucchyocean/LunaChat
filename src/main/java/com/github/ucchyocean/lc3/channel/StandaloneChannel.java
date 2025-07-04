/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.util.ClickableFormat;

/**
 *
 * @author ucchy
 */
public class StandaloneChannel extends Channel {

    public StandaloneChannel(String name) {
        super(name);
    }

    @Override
    protected void sendMessage(ChannelMember member, String message,
            @Nullable ClickableFormat format, boolean sendDynmap) {

        // デバッグ表示メッセージ
        System.out.printf("room=%s, member=%s, format=%s, message=%s%n",
                getName(), member.toString(), format, message);

        // TODO チャンネルのメンバーに送信

    }

    @Override
    protected void log(String message, String name) {

        // デバッグ表示メッセージ
        System.out.printf("room=%s, name=%s, message=%s%n",
                getName(), name, message);

        // TODO ログファイルを出力
    }
}
