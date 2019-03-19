/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * unhideコマンドの実行クラス
 * @author ucchy
 */
public class UnhideCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "unhide";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY1 = "usageUnhide";
    private static final String USAGE_KEY2 = "usageUnhidePlayer";

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
        } else if ( args.length >= 2 ) {
            cname = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
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

            // 非表示になっているかどうかをチェックする
            if ( !channel.getHided().contains(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyUnhided");
                return true;
            }

            // 設定する
            channel.getHided().remove(player);
            channel.save();
            sendResourceMessage(sender, PREINFO, "cmdmsgUnhided", channel.getName());

            return true;

        } else {
            // プレイヤーが対象の場合の処理

            // 既に表示になっていないかどうかをチェックする
            ChannelPlayer hided = ChannelPlayer.getChannelPlayer(cname);
            if ( !api.getHidelist(hided).contains(player) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyUnhidedPlayer");
                return true;
            }

            // 設定する
            api.removeHidelist(player, hided);
            sendResourceMessage(sender, PREINFO, "cmdmsgUnhidedPlayer", hided.getDisplayName());

            return true;
        }
    }
}
