/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * hideコマンドの実行クラス
 * @author ucchy
 */
public class HideCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "hide";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY1 = "usageHide";
    private static final String USAGE_KEY2 = "usageHidePlayer";

    private static final String HIDE_CHANNEL_FIRSTLINE =
            Resources.get("hideChannelFirstLine");
    private static final String HIDE_PLAYER_FIRSTLINE =
            Resources.get("hidePlayerFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_PREFIX = Resources.get("listPlainPrefix");

    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getPermissionNode()
     */
    @Override
    public String getPermissionNode() {
        return PERMISSION_NODE;
    }

    /**
     * コマンドの種別を取得します。
     * @return コマンド種別
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandType()
     */
    @Override
    public CommandType getCommandType() {
        return CommandType.USER;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#sendUsageMessage(org.bukkit.command.CommandSender, java.lang.String)
     */
    @Override
    public void sendUsageMessage(
            CommandSender sender, String label) {
        sendResourceMessage(sender, "", USAGE_KEY1, label);
        sendResourceMessage(sender, "", USAGE_KEY2, label);
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#runCommand(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }
        ChannelPlayer player = ChannelPlayer.getChannelPlayer(sender);

        // 引数チェック
        String cname = null;
        if ( args.length <= 1 ) {
            Channel def = api.getDefaultChannel(player.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        } else {
            cname = args[1];
        }

        // 指定されたコマンドが「/ch hide list」なら、リストを表示して終了
        if ( cname != null && cname.equals("list") ) {
            for ( String item : getHideInfoList(player) ) {
                player.sendMessage(item);
            }
            return true;
        }

        // チャンネルかプレイヤーが存在するかどうかをチェックする
        boolean isChannelCommand = false;
        Channel channel = api.getChannel(cname);
        if ( channel != null ) {
            isChannelCommand = true;
        } else if ( Utility.getOfflinePlayer(cname) == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExistChannelAndPlayer");
            return true;
        }

        if ( isChannelCommand ) {
            // チャンネルが対象の場合の処理

            // 既に非表示になっていないかどうかをチェックする
            if ( channel.getHided().contains(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyHided");
                return true;
            }

            // メンバーかどうかをチェックする
            if ( !channel.getMembers().contains(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgNomember");
                return true;
            }

            // 設定する
            channel.getHided().add(player);
            channel.save();
            sendResourceMessage(sender, PREINFO, "cmdmsgHided", channel.getName());

            return true;

        } else {
            // プレイヤーが対象の場合の処理

            // 既に非表示になっていないかどうかをチェックする
            ChannelPlayer hided = ChannelPlayer.getChannelPlayer(cname);
            if ( api.getHidelist(hided).contains(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyHidedPlayer");
                return true;
            }

            // 自分自身を指定していないかどうかチェックする
            if ( hided.equals(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgCannotHideSelf");
                return true;
            }

            // 設定する
            api.addHidelist(player, hided);
            sendResourceMessage(sender, PREINFO, "cmdmsgHidedPlayer", hided.getDisplayName());

            return true;
        }
    }

    /**
     * hide情報のメッセージを取得する
     * @param player 対象となるプレイヤー
     * @return メッセージ
     */
    private ArrayList<String> getHideInfoList(ChannelPlayer player) {

        ArrayList<String> items = new ArrayList<String>();
        items.add(HIDE_CHANNEL_FIRSTLINE);
        for ( String channel : getHideChannelNameList(player) ) {
            items.add(LIST_PREFIX + channel);
        }
        items.add(HIDE_PLAYER_FIRSTLINE);
        for ( ChannelPlayer p : api.getHideinfo(player) ) {
            items.add(LIST_PREFIX + p.getDisplayName());
        }
        items.add(LIST_ENDLINE);

        return items;
    }

    /**
     * 指定したプレイヤーが非表示にしているチャンネル名のリストを返す
     * @param player プレイヤー
     * @return 指定したプレイヤーが非表示にしているチャンネルのリスト
     */
    private ArrayList<String> getHideChannelNameList(ChannelPlayer player) {

        ArrayList<String> names = new ArrayList<String>();
        for ( Channel channel : api.getChannels() ) {
            if ( channel.getHided().contains(player) ) {
                names.add(channel.getName());
            }
        }
        return names;
    }
}
