/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatLogger;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * logコマンドの実行クラス
 * @author ucchy
 */
public class LogCommand extends SubCommandAbst {

    private static final String LOGDISPLAY_FIRSTLINE = Resources.get("logDisplayFirstLine");
    private static final String LOGDISPLAY_ENDLINE = Resources.get("logDisplayEndLine");
    private static final String LOGDISPLAY_FORMAT = Resources.get("logDisplayFormat");

    private static final String COMMAND_NAME = "log";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageLog";

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
        sendResourceMessage(sender, "", USAGE_KEY, label);
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
    public boolean runCommand(CommandSender sender, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

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

        if ( player != null && cname == null ) {
            Channel def = api.getDefaultChannel(player.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        }

        // 参照権限を確認する
        String node = PERMISSION_NODE + "." + cname;
        if (sender.isPermissionSet(node) && !sender.hasPermission(node)) {
            sendResourceMessage(sender, PREERR, "errmsgPermission",
                    PERMISSION_NODE + "." + cname);
            return true;
        }

        // ログの取得
        ArrayList<String> logs;

        if ( config.getGlobalChannel().equals("") &&
                (cname == null || cname.equals(config.getGlobalMarker())) ) {

            // グローバルチャンネル設定が無くて、指定チャンネルがマーカーの場合、
            // 通常チャットのログを取得する
            LunaChatLogger logger = LunaChat.getInstance().getNormalChatLogger();
            logs = logger.getLog(argsPlayer, argsFilter, argsDate, reverse);

            cname = "グローバルチャット";

        } else {

            // チャンネルが存在するかどうか確認する
            Channel channel = api.getChannel(cname);
            if ( channel == null ) {
                sendResourceMessage(sender, PREERR, "errmsgNotExist");
                return true;
            }

            // BANされていないかどうか確認する
            ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
            if ( player != null && channel.getBanned().contains(cp) ) {
                sendResourceMessage(sender, PREERR, "errmsgBanned");
                return true;
            }

            // チャンネルのメンバーかどうかを確認する
            if (!channel.getMembers().contains(cp)) {
                sendResourceMessage(sender, PREERR, "errmsgNomember");
                return true;
            }

            logs = channel.getLog(argsPlayer, argsFilter, argsDate, reverse);
        }

        // 整形と表示
        sender.sendMessage(String.format(LOGDISPLAY_FIRSTLINE, cname));

        for ( String log : logs ) {

            String[] temp = log.split(",");
            String date = temp[0];
            String message = temp[1];
            String playerName = "";
            if ( temp.length >= 3 ) {
                playerName = temp[2];
            }
            sender.sendMessage(String.format(
                    LOGDISPLAY_FORMAT, date, playerName, message));
        }

        sender.sendMessage(LOGDISPLAY_ENDLINE);

        return true;
    }

}
