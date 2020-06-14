/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * acceptコマンドの実行クラス
 * @author ucchy
 */
public class AcceptCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "accept";
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
        sender.sendMessage(Messages.usageAccept(label));
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
            sender.sendMessage(Messages.errmsgNotInvited());
            return true;
        }

        // チャンネルを取得して、招待記録を消去する
        String channelName = DataMaps.inviteMap.get(sender.getName());

        // 取得できなかったらエラー終了する
        if ( channelName == null ) {
            sender.sendMessage(Messages.errmsgNotfoundChannel());
            return true;
        }

        Channel channel = api.getChannel(channelName);

        // 取得できなかったらエラー終了する
        if (channel == null) {
            sender.sendMessage(Messages.errmsgNotfoundChannel());
            return true;
        }

        DataMaps.inviteMap.remove(sender.getName());
        DataMaps.inviterMap.remove(sender.getName());

        // 既に参加しているなら、エラーを表示して終了する
        if (channel.getMembers().contains(sender)) {
            sender.sendMessage(Messages.errmsgInvitedAlreadyJoin());
            return true;
        }

        // BANされていないか確認する
        if (channel.getBanned().contains(sender)) {
            sender.sendMessage(Messages.errmsgBanned());
            return true;
        }

        // 参加する
        channel.addMember(sender);
        sender.sendMessage(Messages.cmdmsgJoin(channel.getName()));

        // デフォルトの発言先に設定する
        api.setDefaultChannel(sender.getName(), channelName);
        sender.sendMessage(Messages.cmdmsgSet(channel.getName()));

        return true;
    }
}
