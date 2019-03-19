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
 * joinコマンドの実行クラス
 * @author ucchy
 */
public class JoinCommand extends SubCommandAbst {

    private static final String COMMAND_NAME = "join";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";
    private static final String USAGE_KEY = "usageJoin";

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
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        // プレイヤーでなければ終了する
        if (!(sender instanceof Player)) {
            sendResourceMessage(sender, PREERR, "errmsgIngame");
            return true;
        }
        ChannelPlayer player = ChannelPlayer.getChannelPlayer(sender);

        // 実行引数から、参加するチャンネルを取得する
        String channelName = "";
        StringBuilder message = new StringBuilder();
        if (!args[0].equalsIgnoreCase("join")) {
            channelName = args[0];
            if (args.length >= 2) {
                for (int i = 1; i < args.length; i++) {
                    message.append(args[i] + " ");
                }
            }
        } else if (args.length >= 2) {
            channelName = args[1];
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    message.append(args[i] + " ");
                }
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        // チャンネルを取得する
        Channel channel = api.getChannel(channelName);

        // チャンネルが存在するかどうかをチェックする
        if ( channel == null ) {
            if ( config.getGlobalChannel().equals("") &&
                    channelName.equals(config.getGlobalMarker()) ) {
                // グローバルチャンネル設定が無くて、指定チャンネルがマーカーの場合、
                // 発言先を削除して、グローバルチャンネルにする

                api.removeDefaultChannel(player.getName());
                sendResourceMessage(sender, PREINFO, "cmdmsgSet", "グローバル");
                if ( message.length() > 0 && player.getPlayer() != null ) {
                    player.getPlayer().chat(config.getGlobalMarker() + message.toString());
                }
                return true;
            }
            if (config.isCreateChannelOnJoinCommand()) {
                // 存在しないチャットには、チャンネルを作って入る設定の場合

                // 使用可能なチャンネル名かどうかをチェックする
                if ( !channelName.matches("[0-9a-zA-Z\\-_]+") ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgCannotUseForChannel", channelName);
                    return true;
                }

                // 最低文字列長を上回っているかをチェックする
                if ( channelName.length() < config.getMinChannelNameLength() ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgCannotUseForChannelTooShort",
                            channelName, config.getMinChannelNameLength());
                    return true;
                }

                // 最大文字列長を下回っているかをチェックする
                if ( channelName.length() > config.getMaxChannelNameLength() ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgCannotUseForChannelTooLong",
                            channelName, config.getMaxChannelNameLength());
                    return true;
                }

                // チャンネル作成
                Channel c = api.createChannel(channelName);
                if ( c != null ) {
                    c.addMember(player);
                    sendResourceMessage(sender, PREINFO, "cmdmsgCreate", channelName);
                }
                return true;

            } else {
                // 存在しないチャットには入れない設定の場合

                sendResourceMessage(sender, PREERR, "errmsgNotExist");
                return true;
            }
        }

        channelName = channel.getName();

        // 入室権限を確認する
        String node = PERMISSION_NODE + "." + channelName;
        if (sender.isPermissionSet(node) && !sender.hasPermission(node)) {
            sendResourceMessage(sender, PREERR, "errmsgPermission",
                    PERMISSION_NODE + "." + channelName);
            return true;
        }

        // BANされていないか確認する
        if (channel.getBanned().contains(player)) {
            sendResourceMessage(sender, PREERR, "errmsgBanned");
            return true;
        }

        // 個人チャットの場合はエラーにする
        if (channel.isPersonalChat()) {
            sendResourceMessage(sender, PREERR, "errmsgCannotJoinPersonal");
            return true;
        }

        if (channel.getMembers().contains(player)) {

            // 何かメッセージがあるなら、そのままチャット送信する
            if (message.length() > 0 && hasSpeakPermission(sender, channelName)) {
                channel.chat(player, message.toString());
                return true;
            }

            // デフォルトの発言先に設定する
            api.setDefaultChannel(player.getName(), channelName);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", channelName);

        } else {

            // グローバルチャンネルで、何かメッセージがあるなら、そのままチャット送信する
            if (channel.getName().equals(config.getGlobalChannel()) &&
                    message.length() > 0 && hasSpeakPermission(sender, channelName)) {
                channel.chat(player, message.toString());
                return true;
            }

            // パスワードが設定されている場合は、パスワードを確認する
            if ( !channel.getPassword().equals("") ) {
                if ( message.toString().trim().equals("") ) {
                    // パスワード空欄
                    sendResourceMessage(sender, PREERR, "errmsgPassword1");
                    sendResourceMessage(sender, PREERR, "errmsgPassword2");
                    sendResourceMessage(sender, PREERR, "errmsgPassword3");
                    return true;
                } else if ( !channel.getPassword().equals(message.toString().trim()) ) {
                    // パスワード不一致
                    sendResourceMessage(sender, PREERR, "errmsgPasswordNotmatch");
                    sendResourceMessage(sender, PREERR, "errmsgPassword2");
                    sendResourceMessage(sender, PREERR, "errmsgPassword3");
                    return true;
                }
            }

            // チャンネルに参加し、デフォルトの発言先に設定する
            if ( !channel.getName().equals(config.getGlobalChannel()) ) {
                channel.addMember(player);
                sendResourceMessage(sender, PREINFO, "cmdmsgJoin", channelName);
            }
            api.setDefaultChannel(player.getName(), channelName);
            sendResourceMessage(sender, PREINFO, "cmdmsgSet", channelName);
        }

        // チャンネル説明文があるなら、説明文を表示する
        if ( !channel.getDescription().trim().equals("") ) {
            sendResourceMessage(sender, PREINFO, "cmdmsgSetTopic", channel.getDescription().trim());
        }

        // 非表示に設定しているなら、注意を流す
        if ( channel.getHided().contains(player) ) {
            sendResourceMessage(sender, PREINFO, "cmdmsgSetHide");
        }

        return true;
    }

    private boolean hasSpeakPermission(CommandSender sender, String channelName) {
        String node = PERMISSION_SPEAK_PREFIX + "." + channelName;
        return sender.isPermissionSet(node) && sender.hasPermission(node);
    }
}
