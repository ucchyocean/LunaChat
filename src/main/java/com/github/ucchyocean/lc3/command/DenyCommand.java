/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * denyコマンドの実行クラス
 * @author ucchy
 */
public class DenyCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "deny";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;

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
        sender.sendMessage(Messages.usageDeny(label));
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
    public boolean runCommand(ChannelMember sender, String label, String[] args) {

        // 招待を受けていないプレイヤーなら、エラーを表示して終了する
        if (!DataMaps.inviteMap.containsKey(sender.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNotInvited");
            return true;
        }

        // 招待者を取得して、招待記録を消去する
        String inviterName = DataMaps.inviterMap.get(sender.getName());
        DataMaps.inviteMap.remove(sender.getName());
        DataMaps.inviterMap.remove(sender.getName());

        // メッセージ送信
        sendResourceMessage(sender, PREINFO, "cmdmsgDeny");
        ChannelMember inviter = ChannelMember.getChannelMember(inviterName);
        if (inviter != null) {
            sendResourceMessage(inviter, PREINFO, "cmdmsgDenyed");
        }
        return true;
    }

}
