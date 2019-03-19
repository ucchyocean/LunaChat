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
 * muteコマンドの実行クラス
 * @author ucchy
 */
public class MuteCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "mute";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY1 = "usageMute";
    private static final String USAGE_KEY2 = "usageMute2";

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
        sendResourceMessage(sender, "", USAGE_KEY1, label);
        sendResourceMessage(sender, "", USAGE_KEY2, label);
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
        if ( !channel.hasModeratorPermission(sender) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // Muteされるプレイヤーがメンバーかどうかチェックする
        ChannelPlayer kicked = ChannelPlayer.getChannelPlayer(kickedName);
        if (!channel.getMembers().contains(kicked)) {
            sendResourceMessage(sender, PREERR, "errmsgNomemberOther");
            return true;
        }

        // 既にMuteされているかどうかチェックする
        if (channel.getMuted().contains(kicked)) {
            sendResourceMessage(sender, PREERR, "errmsgAlreadyMuted");
            return true;
        }

        // 期限付きMuteの場合、期限の指定が正しいかどうかをチェックする
        int expireMinutes = -1;
        if (args.length >= 3) {
            if ( !args[2].matches("[0-9]+") ) {
                sendResourceMessage(sender, PREERR, "errmsgInvalidMuteExpireParameter");
                return true;
            }
            expireMinutes = Integer.parseInt(args[2]);
            if ( expireMinutes < 1 || 43200 < expireMinutes ) {
                sendResourceMessage(sender, PREERR, "errmsgInvalidMuteExpireParameter");
                return true;
            }
        }

        // Mute実行
        channel.getMuted().add(kicked);
        if ( expireMinutes != -1 ) {
            long expire = System.currentTimeMillis() + expireMinutes * 60 * 1000;
            channel.getMuteExpires().put(kicked, expire);
        }
        channel.save();

        // senderに通知メッセージを出す
        if ( expireMinutes != -1 ) {
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgMuteWithExpire", kickedName, channel.getName(), expireMinutes);
        } else {
            sendResourceMessage(sender, PREINFO,
                    "cmdmsgMute", kickedName, channel.getName());
        }

        // チャンネルに通知メッセージを出す
        if ( expireMinutes != -1 ) {
            sendResourceMessageWithKeyword(channel,
                    "muteWithExpireMessage", kicked, expireMinutes);
        } else {
            sendResourceMessageWithKeyword(channel, "muteMessage", kicked);
        }

        // BANされた人に通知メッセージを出す
        if ( kicked != null ) {
            sendResourceMessage(kicked, PREINFO,
                    "cmdmsgMuted", channel.getName());
        }

        return true;
    }
}
