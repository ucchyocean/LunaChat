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
 * banコマンドの実行クラス
 * @author ucchy
 */
public class BanCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "ban";
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
        return CommandType.MODERATOR;
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
        sender.sendMessage(Messages.usageBan(label));
        sender.sendMessage(Messages.usageBan2(label));
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

        // 実行引数から、BANするユーザーを取得する
        String kickedName = "";
        if (args.length >= 2) {
            kickedName = args[1];
        } else {
            sender.sendMessage(Messages.errmsgCommand());
            return true;
        }

        // 対象チャンネルを取得、取得できない場合はエラー表示して終了する
        Channel channel = null;
        boolean isSpecifiedChannel = false;
        if (args.length >= 3) {
            channel = api.getChannel(args[2]);
            isSpecifiedChannel = true;
        } else {
            channel = api.getDefaultChannel(sender.getName());
        }
        if (channel == null) {
            sender.sendMessage(Messages.errmsgNoJoin());
            return true;
        }

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sender.sendMessage(Messages.errmsgNotModerator());
            return true;
        }

        // グローバルチャンネルならBANできない
        if ( channel.isGlobalChannel() ) {
            sender.sendMessage(Messages.errmsgCannotBANGlobal(channel.getName()));
            return true;
        }

        // BANされるプレイヤーがメンバーかどうかチェックする
        ChannelMember kicked = ChannelMember.getChannelMember(kickedName);
        if (!channel.getMembers().contains(kicked)) {
            sender.sendMessage(Messages.errmsgNomemberOther());
            return true;
        }

        // 既にBANされているかどうかチェックする
        if (channel.getBanned().contains(kicked)) {
            sender.sendMessage(Messages.errmsgAlreadyBanned());
            return true;
        }

        // 期限付きBANの場合、期限の指定が正しいかどうかをチェックする
        int expireMinutes = -1;
        if (args.length >= 3 && !isSpecifiedChannel) {
            if ( !args[2].matches("[0-9]+") ) {
                sender.sendMessage(Messages.errmsgInvalidBanExpireParameter());
                return true;
            }
            expireMinutes = Integer.parseInt(args[2]);
            if ( expireMinutes < 1 || 43200 < expireMinutes ) {
                sender.sendMessage(Messages.errmsgInvalidBanExpireParameter());
                return true;
            }
        }

        // BAN実行
        channel.getBanned().add(kicked);
        if ( expireMinutes != -1 ) {
            long expire = System.currentTimeMillis() + expireMinutes * 60 * 1000;
            channel.getBanExpires().put(kicked, expire);
        }
        channel.removeMember(kicked);

        // senderに通知メッセージを出す
        if ( expireMinutes != -1 ) {
            sender.sendMessage(Messages.cmdmsgBanWithExpire(
                    kickedName, channel.getName(), expireMinutes));
        } else {
            sender.sendMessage(Messages.cmdmsgBan(
                    kickedName, channel.getName()));
        }

        // チャンネルに通知メッセージを出す
        if ( expireMinutes != -1 ) {
            channel.sendSystemMessage(Messages.banWithExpireMessage(
                    channel.getColorCode(), channel.getName(), kicked.getName(), expireMinutes),
                    true, "system");
        } else {
            channel.sendSystemMessage(Messages.banMessage(
                    channel.getColorCode(), channel.getName(), kicked.getName()),
                    true, "system");
        }

        // BANされた人に通知メッセージを出す
        if ( kicked != null && kicked.isOnline() ) {
            kicked.sendMessage(Messages.cmdmsgBanned(channel.getName()));
        }

        return true;
    }
}
