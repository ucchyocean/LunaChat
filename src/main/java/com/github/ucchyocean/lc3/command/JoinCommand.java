/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * joinコマンドの実行クラス
 * @author ucchy
 */
public class JoinCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "join";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";

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
        sender.sendMessage(Messages.usageJoin(label));
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
            sender.sendMessage(Messages.errmsgCommand());
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

                if ( api.getDefaultChannel(sender.getName()) != null ) {
                    api.removeDefaultChannel(sender.getName());
                    sender.sendMessage(Messages.cmdmsgSet("Global"));
                }

                // 何かメッセージがあるなら、そのままチャット送信する
                // TODO 要テスト
                sender.chat(message.toString());

                return true;
            }

            if (config.isCreateChannelOnJoinCommand()) {
                // 存在しないチャットには、チャンネルを作って入る設定の場合

                // 使用可能なチャンネル名かどうかをチェックする
                if ( !channelName.matches("[0-9a-zA-Z\\-_]+") ) {
                    sender.sendMessage(Messages.errmsgCannotUseForChannel(channelName));
                    return true;
                }

                // 最低文字列長を上回っているかをチェックする
                if ( channelName.length() < config.getMinChannelNameLength() ) {
                    sender.sendMessage(Messages.errmsgCannotUseForChannelTooShort(
                            channelName, config.getMinChannelNameLength()));
                    return true;
                }

                // 最大文字列長を下回っているかをチェックする
                if ( channelName.length() > config.getMaxChannelNameLength() ) {
                    sender.sendMessage(Messages.errmsgCannotUseForChannelTooLong(
                            channelName, config.getMaxChannelNameLength()));
                    return true;
                }

                // チャンネル作成
                Channel c = api.createChannel(channelName, sender);
                if ( c != null ) {
                    c.addMember(sender);
                    sender.sendMessage(Messages.cmdmsgCreate(channelName));
                }
                return true;

            } else {
                // 存在しないチャットには入れない設定の場合

                sender.sendMessage(Messages.errmsgNotExist());
                return true;
            }
        }

        channelName = channel.getName();

        // 入室権限を確認する
        String node = PERMISSION_NODE + "." + channelName;
        if (sender.isPermissionSet(node) && !sender.hasPermission(node)) {
            sender.sendMessage(Messages.errmsgPermission(node));
            return true;
        }

        // BANされていないか確認する
        if (channel.getBanned().contains(sender)) {
            sender.sendMessage(Messages.errmsgBanned());
            return true;
        }

        // 個人チャットの場合はエラーにする
        if (channel.isPersonalChat()) {
            sender.sendMessage(Messages.errmsgCannotJoinPersonal());
            return true;
        }

        if (channel.getMembers().contains(sender)) {

            // 何かメッセージがあるなら、そのままチャット送信する
            if (message.length() > 0 && hasSpeakPermission(sender, channelName)) {
                channel.chat(sender, message.toString());
                return true;
            }

            // デフォルトの発言先に設定する
            if ( api.getDefaultChannel(sender.getName()) == null ||
                    !api.getDefaultChannel(sender.getName()).getName().equals(channelName) ) {
                api.setDefaultChannel(sender.getName(), channelName);
                sender.sendMessage(Messages.cmdmsgSet(channelName));
            }

        } else {

            // グローバルチャンネルで、何かメッセージがあるなら、そのままチャット送信する
            if (channel.getName().equals(config.getGlobalChannel()) &&
                    message.length() > 0 && hasSpeakPermission(sender, channelName)) {
                channel.chat(sender, message.toString());
                return true;
            }

            // パスワードが設定されている場合は、パスワードを確認する
            if ( !channel.getPassword().equals("") ) {
                if ( message.toString().trim().equals("") ) {
                    // パスワード空欄
                    sender.sendMessage(Messages.errmsgPassword1());
                    sender.sendMessage(Messages.errmsgPassword2());
                    sender.sendMessage(Messages.errmsgPassword3());
                    return true;
                } else if ( !channel.getPassword().equals(message.toString().trim()) ) {
                    // パスワード不一致
                    sender.sendMessage(Messages.errmsgPasswordNotmatch());
                    sender.sendMessage(Messages.errmsgPassword2());
                    sender.sendMessage(Messages.errmsgPassword3());
                    return true;
                }
            }

            // チャンネルに参加し、デフォルトの発言先に設定する
            if ( !channel.getName().equals(config.getGlobalChannel()) ) {
                channel.addMember(sender);
                sender.sendMessage(Messages.cmdmsgJoin(channelName));
            }

            // デフォルトの発言先に設定する
            if ( api.getDefaultChannel(sender.getName()) == null ||
                    !api.getDefaultChannel(sender.getName()).getName().equals(channelName) ) {
                api.setDefaultChannel(sender.getName(), channelName);
                sender.sendMessage(Messages.cmdmsgSet(channelName));
            }
        }

        // チャンネル説明文があるなら、説明文を表示する
        if ( !channel.getDescription().trim().equals("") ) {
            sender.sendMessage(Messages.cmdmsgSetTopic(channel.getDescription().trim()));
        }

        // 非表示に設定しているなら、注意を流す
        if ( channel.getHided().contains(sender) ) {
            sender.sendMessage(Messages.cmdmsgSetHide());
        }

        return true;
    }

    private boolean hasSpeakPermission(ChannelMember sender, String channelName) {
        String node = PERMISSION_SPEAK_PREFIX + "." + channelName;
        return sender.isPermissionSet(node) && sender.hasPermission(node);
    }
}
