/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import java.util.ArrayList;

import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * moderatorコマンドの実行クラス
 * @author ucchy
 */
public class ModeratorCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "moderator";
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
        sender.sendMessage(Messages.usageModerator(label));
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
        // このコマンドは、デフォルトチャンネルでない人も実行できるが、その場合はチャンネル名を指定する必要がある
        String cname = null;
        ArrayList<String> moderator = new ArrayList<String>();
        if ( args.length >= 2 ) {
            Channel def = api.getDefaultChannel(sender.getName());
            if ( def != null ) {
                cname = def.getName();
            }
            for (int i = 1; i < args.length; i++) {
                moderator.add(args[i]);
            }
        } else if ( args.length >= 3 ) {
            cname = args[1];
            for (int i = 2; i < args.length; i++) {
                moderator.add(args[i]);
            }
        } else {
            sender.sendMessage(Messages.errmsgCommand());
            return true;
        }

        Channel channel = api.getChannel(cname);

        // チャンネルが存在するかどうかをチェックする
        if ( channel == null ) {
            sender.sendMessage(Messages.errmsgNotExist());
            return true;
        }

        cname = channel.getName();

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sender.sendMessage(Messages.errmsgNotModerator());
            return true;
        }

        // グローバルチャンネルなら設定できない
        if ( channel.isGlobalChannel() ) {
            sender.sendMessage(Messages.errmsgCannotModeratorGlobal(channel.getName()));
            return true;
        }

        // 設定する
        for ( String mod : moderator ) {
            if ( mod.startsWith("-") ) {
                String name = mod.substring(1);
                ChannelMember cp = ChannelMember.getChannelMember(name);
                channel.removeModerator(cp);
                sender.sendMessage(Messages.cmdmsgModeratorMinus(name, cname));
            } else {
                ChannelMember cp = ChannelMember.getChannelMember(mod);
                channel.addModerator(cp);
                sender.sendMessage(Messages.cmdmsgModerator(mod, cname));
            }
        }

        return true;
    }
}
