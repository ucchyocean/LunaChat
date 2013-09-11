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
 * muteコマンドの実行クラス
 * @author ucchy
 */
public class MuteCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "mute";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageMute";
    
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

        // 実行引数から、Muteするユーザーを取得する
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
        if (!channel.getModerator().contains(kicker.getName()) && !kicker.isOp()) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // Muteされるプレイヤーがメンバーかどうかチェックする
        if (!channel.getMembers().contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }
        
        // 既にMuteされているかどうかチェックする
        if (channel.getMuted().contains(kickedName)) {
            sendResourceMessage(sender, PREERR, "errmsgAlreadyMuted");
            return true;
        }

        // Mute実行
        Player kicked = Bukkit.getPlayerExact(kickedName);
        channel.getMuted().add(kickedName);
        channel.save();

        sendResourceMessage(sender, PREINFO,
                "cmdmsgMute", kickedName, channel.getName());
        if (kicked != null) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgMuted", channel.getName());
        }

        return true;
    }
}
