/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * kickコマンドの実行クラス
 * @author ucchy
 */
public class KickCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "kick";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageKick";

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
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 実行引数から、キックするユーザーを取得する
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

        // グローバルチャンネルならキックできない
        if ( channel.isGlobalChannel() ) {
            sendResourceMessage(sender, PREERR,
                    "errmsgCannotKickGlobal", channel.getName());
            return true;
        }

        // キックされるプレイヤーがメンバーかどうかチェックする
        ChannelPlayer kicked = ChannelPlayer.getChannelPlayer(kickedName);
        if (!channel.getMembers().contains(kicked)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }

        // キック実行
        channel.removeMember(kicked);
        channel.save();

        // senderに通知メッセージを出す
        sendResourceMessage(sender, PREINFO,
                "cmdmsgKick", kickedName, channel.getName());

        // チャンネルに通知メッセージを出す
        sendResourceMessageWithKeyword(channel, "kickMessage", kicked);

        // キックされた人に通知メッセージを出す
        if ( kicked != null && kicked.isOnline() ) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgKicked", channel.getName());
        }

        return true;
    }

}
