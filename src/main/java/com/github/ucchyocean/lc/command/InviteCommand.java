/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.member.ChannelMember;

/**
 * inviteコマンドの実行クラス
 * @author ucchy
 */
public class InviteCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "invite";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageInvite";

    private static final String PERMISSION_NODE_FORCE_INVITE
            = "lunachat-admin.force-invite";

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
        return CommandType.MODERATOR;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#sendUsageMessage()
     */
    @Override
    public void sendUsageMessage(
            ChannelMember sender, String label) {
        sendResourceMessage(sender, "", USAGE_KEY, label);
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#runCommand(java.lang.String[])
     */
    @Override
    public boolean runCommand(ChannelMember sender, String label, String[] args) {

        if ( args.length >= 3 && args[2].equalsIgnoreCase("force") ) {
            return runForceInviteCommand(sender, label, args);
        }

        return runNormalInviteCommand(sender, label, args);
    }

    /**
     * 通常のinviteコマンドを処理する
     * @param sender
     * @param label
     * @param args
     * @return
     */
    private boolean runNormalInviteCommand(ChannelMember sender, String label, String[] args) {

        // デフォルトの発言先を取得する
        Channel channel = api.getDefaultChannel(sender.getName());
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // 実行引数から招待する人を取得する
        String invitedName = "";
        if (args.length >= 2) {
            invitedName = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // 招待相手が存在するかどうかを確認する
        ChannelMember invited = ChannelMember.getChannelMember(invitedName);
        if ( invited == null || !invited.isOnline() ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return true;
        }

        // 招待相手が既にチャンネルに参加しているかどうかを確認する
        if (channel.getMembers().contains(invited)) {
            sendResourceMessage(sender, PREERR,
                    "errmsgInvitedAlreadyExist", invitedName);
            return true;
        }

        // 招待を送信する
        DataMaps.inviteMap.put(invitedName, channel.getName());
        DataMaps.inviterMap.put(invitedName, sender.getName());

        sendResourceMessage(sender, PREINFO,
                "cmdmsgInvite", invitedName, channel.getName());
        sendResourceMessage(invited, PREINFO,
                "cmdmsgInvited1", sender.getName(), channel.getName());
        sendResourceMessage(invited, PREINFO, "cmdmsgInvited2");
        return true;
    }

    /**
     * 強制入室コマンドを処理する
     * @param sender
     * @param label
     * @param args
     * @return
     */
    private boolean runForceInviteCommand(ChannelMember sender, String label, String[] args) {

        // パーミッションチェック
        if ( !sender.hasPermission(PERMISSION_NODE_FORCE_INVITE) ) {
            sendResourceMessage(sender, PREERR, "errmsgPermission",
                    PERMISSION_NODE_FORCE_INVITE);
            return true;
        }

        // 引数チェック
        // このコマンドは、デフォルトチャンネルでない人も実行できるが、その場合はチャンネル名を指定する必要がある
        String cname = null;
        if ( args.length <= 3 ) {
            Channel def = api.getDefaultChannel(sender.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        } else if ( args.length >= 4 ) {
            cname = args[3];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルが存在するかどうか確認する
        Channel channel = api.getChannel(cname);
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        // 招待相手が存在するかどうかを確認する
        String invitedName = args[1];
        ChannelMember invited = ChannelMember.getChannelMember(invitedName);
        if ( invited == null || !invited.isOnline() ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgNotfoundPlayer", invitedName);
            return true;
        }

        // 招待相手が既にチャンネルに参加しているかどうかを確認する
        if (channel.getMembers().contains(invited)) {
            sendResourceMessage(sender, PREERR,
                    "errmsgInvitedAlreadyExist", invitedName);
            return true;
        }

        // 参加する
        channel.addMember(invited);
        api.setDefaultChannel(invitedName, cname);
        sendResourceMessage(sender, PREINFO,
                "cmdmsgInvite", invitedName, channel.getName());
        sendResourceMessage(invited, PREINFO,
                "cmdmsgJoin", channel.getName());

        return true;
    }
}
