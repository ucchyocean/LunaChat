/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * サブコマンドの抽象クラス
 * @author ucchy
 */
public abstract class LunaChatSubCommand {

    /**
     * コマンドの種別
     * @author ucchy
     */
    protected enum CommandType {

        /** 一般ユーザー向けコマンド */
        USER,

        /** チャンネルモデレーター向けコマンド */
        MODERATOR,

        /** サーバー管理者向けコマンド */
        ADMIN
    }

    protected LunaChatAPI api;
    protected LunaChatConfig config;

    /**
     * コンストラクタ
     */
    public LunaChatSubCommand() {
        api = LunaChat.getPlugin().getLunaChatAPI();
        config = LunaChat.getPlugin().getLunaChatConfig();
    }

//    /**
//     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、Channelに送信する
//     * @param channel メッセージの送り先
//     * @param pre プレフィックス
//     * @param key リソースキー
//     * @param player キーワード置き換えに使用するプレイヤー
//     */
//    protected void sendResourceMessageWithKeyword(
//            Channel channel, String key, ChannelMember player) {
//
//        String msg = Messages.get(key);
//        if ( msg == null || msg.equals("") ) {
//            return;
//        }
//        msg = msg.replace("%ch", channel.getName());
//        msg = msg.replace("%color", channel.getColorCode());
//        if ( player != null ) {
//            msg = msg.replace("%username", player.getDisplayName());
//            msg = msg.replace("%player", player.getName());
//        } else {
//            msg = msg.replace("%username", "");
//            msg = msg.replace("%player", "");
//        }
//        msg = Utility.replaceColorCode(msg);
//        channel.sendMessage(null, msg, null, true, "system");
//    }
//
//    /**
//     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、Channelに送信する
//     * @param channel メッセージの送り先
//     * @param pre プレフィックス
//     * @param key リソースキー
//     * @param player キーワード置き換えに使用するプレイヤー
//     * @param minutes キーワード置き換えに使用する数値
//     */
//    protected void sendResourceMessageWithKeyword(
//            Channel channel, String key, ChannelMember player, int minutes) {
//
//        String msg = Messages.get(key);
//        if ( msg == null || msg.equals("") ) {
//            return;
//        }
//        msg = msg.replace("%ch", channel.getName());
//        msg = msg.replace("%color", channel.getColorCode());
//        msg = msg.replace("%d", String.valueOf(minutes));
//        if ( player != null ) {
//            msg = msg.replace("%username", player.getDisplayName());
//            msg = msg.replace("%player", player.getName());
//        } else {
//            msg = msg.replace("%username", "");
//            msg = msg.replace("%player", "");
//        }
//        msg = Utility.replaceColorCode(msg);
//        channel.sendMessage(null, msg, null, true, "system");
//    }
//
//    /**
//     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、ChannelMemberに送信する
//     * @param sender メッセージの送り先
//     * @param pre プレフィックス
//     * @param key リソースキー
//     * @param args リソース内の置き換え対象キーワード
//     */
//    protected void sendResourceMessage(
//            ChannelMember cp, String pre, String key, Object... args) {
//
//        String org = Messages.get(key);
//        if ( org == null || org.equals("") ) {
//            return;
//        }
//        String msg = String.format(pre + org, args);
//        cp.sendMessage(msg);
//    }

    /**
     * コマンドを取得します。
     * @return コマンド
     */
    public abstract String getCommandName();

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     */
    public abstract String getPermissionNode();

    /**
     * コマンドの種別を取得します。
     * @return コマンド種別
     */
    public abstract CommandType getCommandType();

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     */
    public abstract void sendUsageMessage(
            ChannelMember sender, String label);

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     */
    public abstract boolean runCommand(
            ChannelMember sender, String label, String[] args);
}
