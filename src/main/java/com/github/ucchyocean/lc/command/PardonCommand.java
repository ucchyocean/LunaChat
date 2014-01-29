/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Channel;

/**
 * pardonコマンドの実行クラス
 * @author ucchy
 */
public class PardonCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "pardon";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usagePardon";
    
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
            CommandSender sender, String label) {
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
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、BAN解除するユーザーを取得する
        String kickedName = "";
        if (args.length >= 2) {
            kickedName = args[1];
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // デフォルト参加チャンネルを取得、取得できない場合はエラー表示して終了する
        Player kicker = (Player) sender;
        Channel channel = api.getDefaultChannel(kicker.getName());
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNoJoin");
            return true;
        }

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // BAN解除されるプレイヤーがBANされているかどうかチェックする
        if (!channel.getBanned().contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgNotBanned");
            return true;
        }

        // BAN解除実行
        Player kicked = Bukkit.getPlayerExact(kickedName);
        channel.getBanned().remove(kickedName);
        channel.save();

        sendResourceMessage(sender, PREINFO,
                "cmdmsgPardon", kickedName, channel.getName());
        if (kicked != null) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgPardoned", channel.getName());
        }

        return true;
    }
}
