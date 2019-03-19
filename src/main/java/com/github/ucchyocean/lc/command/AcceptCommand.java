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
 * acceptコマンドの実行クラス
 * @author ucchy
 */
public class AcceptCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "accept";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageAccept";

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

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }

        // 招待を受けていないプレイヤーなら、エラーを表示して終了する
        Player player = (Player) sender;
        if (!DataMaps.inviteMap.containsKey(player.getName())) {
            sendResourceMessage(sender, PREERR, "errmsgNotInvited");
            return true;
        }

        // チャンネルを取得して、招待記録を消去する
        String channelName = DataMaps.inviteMap.get(player.getName());
        Channel channel = api.getChannel(channelName);
        DataMaps.inviteMap.remove(player.getName());
        DataMaps.inviterMap.remove(player.getName());

        // 取得できなかったらエラー終了する
        if (channel == null) {
            sendResourceMessage(sender, PREERR, "errmsgNotfoundChannel");
            return true;
        }

        // 既に参加しているなら、エラーを表示して終了する
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
        if (channel.getMembers().contains(cp)) {
            sendResourceMessage(sender, PREERR, "errmsgInvitedAlreadyJoin");
            return true;
        }

        // 参加する
        channel.addMember(cp);
        sendResourceMessage(sender, PREINFO, "cmdmsgJoin", channel.getName());

        // デフォルトの発言先に設定する
        api.setDefaultChannel(player.getName(), channelName);
        sendResourceMessage(sender, PREINFO, "cmdmsgSet", channelName);

        return true;
    }
}
