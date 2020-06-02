/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.Utility;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * unhideコマンドの実行クラス
 * @author ucchy
 */
public class UnhideCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "unhide";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY1 = "usageUnhide";
    private static final String USAGE_KEY2 = "usageUnhidePlayer";

    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getPermissionNode()
     */
    @Override
    public String getPermissionNode() {
        return PERMISSION_NODE;
    }

    /**
     * コマンドの種別を取得します。
     * @return コマンド種別
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getCommandType()
     */
    @Override
    public CommandType getCommandType() {
        return CommandType.USER;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#sendUsageMessage()
     */
    @Override
    public void sendUsageMessage(
            ChannelMember sender, String label) {
        sendResourceMessage(sender, "", USAGE_KEY1, label);
        sendResourceMessage(sender, "", USAGE_KEY2, label);
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#runCommand(java.lang.String[])
     */
    @Override
    public boolean runCommand(
            ChannelMember sender, String label, String[] args) {

        // 引数チェック
        String cname = null;
        boolean isPlayerCommand = false;
        boolean isChannelCommand = false;
        if ( args.length <= 1 ) {
            Channel def = api.getDefaultChannel(sender.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        } else if ( args.length >= 2 ) {
            if ( args.length >= 3 && args[1].equalsIgnoreCase("player") ) {
                // 指定されたコマンドが「/ch unhide player (player名)」なら、対象をプレイヤーとする。
                isPlayerCommand = true;
                isChannelCommand = false;
                cname = args[2];

            } else if ( args.length >= 3 && args[1].equalsIgnoreCase("channel") ) {
                // 指定されたコマンドが「/ch unhide channel (channel名)」なら、対象をチャンネルとする。
                isPlayerCommand = false;
                isChannelCommand = true;
                cname = args[2];

            } else {
                // 「/ch hide (player名 または channel名)」
                cname = args[1];
            }
        }

        // チャンネルかプレイヤーが存在するかどうかをチェックする
        Channel channel = api.getChannel(cname);
        if ( !isPlayerCommand && channel != null ) {
            isChannelCommand = true;
        } else if ( Utility.existsOfflinePlayer(cname) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExistChannelAndPlayer");
            return true;
        }

        if ( isChannelCommand ) {
            // チャンネルが対象の場合の処理

            // 非表示になっているかどうかをチェックする
            if ( !channel.getHided().contains(sender) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyUnhided");
                return true;
            }

            // 設定する
            channel.getHided().remove(sender);
            channel.save();
            sendResourceMessage(sender, PREINFO, "cmdmsgUnhided", channel.getName());

            return true;

        } else {
            // プレイヤーが対象の場合の処理

            // 既に表示になっていないかどうかをチェックする
            ChannelMember hided = ChannelMember.getChannelMember(cname);
            if ( !api.getHidelist(hided).contains(sender) ) {
                sendResourceMessage(sender, PREERR, "errmsgAlreadyUnhidedPlayer");
                return true;
            }

            // 設定する
            api.removeHidelist(sender, hided);
            sendResourceMessage(sender, PREINFO, "cmdmsgUnhidedPlayer", hided.getDisplayName());

            return true;
        }
    }
}
