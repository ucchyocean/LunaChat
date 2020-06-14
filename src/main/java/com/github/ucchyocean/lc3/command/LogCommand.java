/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import java.util.ArrayList;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatLogger;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * logコマンドの実行クラス
 * @author ucchy
 */
public class LogCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "log";
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
        sender.sendMessage(Messages.usageLog(label));
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

        // 引数チェック
        String cname = null;
        String argsPlayer = null;
        String argsFilter = null;
        String argsDate = null;
        boolean reverse = false;

        int index = 1;
        if ( args.length >= 2 && !args[1].contains("=")) {
            cname = args[1];
            index = 2;
        }

        for ( int i=index; i<args.length; i++ ) {
            String arg = args[i];
            if ( arg.startsWith("p=") ) {
                argsPlayer = arg.substring(2);
            } else if ( arg.startsWith("f=") ) {
                argsFilter = arg.substring(2);
            } else if ( arg.startsWith("d=") ) {
                argsDate = arg.substring(2);
            } else if ( arg.equals("r=") ) {
                reverse = true;
            }
        }

        if ( sender != null && cname == null ) {
            Channel def = api.getDefaultChannel(sender.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        }

        // 参照権限を確認する
        String node = PERMISSION_NODE + "." + cname;
        if (sender.isPermissionSet(node) && !sender.hasPermission(node)) {
            sender.sendMessage(Messages.errmsgPermission(node));
            return true;
        }

        // ログの取得
        ArrayList<String> logs;

        if ( config.getGlobalChannel().equals("") &&
                (cname == null || cname.equals(config.getGlobalMarker())) ) {

            // グローバルチャンネル設定が無くて、指定チャンネルがマーカーの場合、
            // 通常チャットのログを取得する
            LunaChatLogger logger = LunaChat.getNormalChatLogger();
            logs = logger.getLog(argsPlayer, argsFilter, argsDate, reverse);

            cname = "GlobalChat";

        } else {

            // チャンネルが存在するかどうか確認する
            Channel channel = api.getChannel(cname);
            if ( channel == null ) {
                sender.sendMessage(Messages.errmsgNotExist());
                return true;
            }

            // BANされていないかどうか確認する
            if ( sender != null && channel.getBanned().contains(sender) ) {
                sender.sendMessage(Messages.errmsgBanned());
                return true;
            }

            // チャンネルのメンバーかどうかを確認する
            if (!channel.getMembers().contains(sender)) {
                sender.sendMessage(Messages.errmsgNomember());
                return true;
            }

            logs = channel.getLog(argsPlayer, argsFilter, argsDate, reverse);
        }

        // 整形と表示
        sender.sendMessage(Messages.logDisplayFirstLine(cname));

        for ( String log : logs ) {

            String[] temp = log.split(",");
            String date = temp[0];
            String message = temp[1];
            String playerName = "";
            if ( temp.length >= 3 ) {
                playerName = temp[2];
            }
            sender.sendMessage(Messages.logDisplayFormat(date, playerName, message));
        }

        sender.sendMessage(Messages.logDisplayEndLine());

        return true;
    }

}
