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
import com.github.ucchyocean.lc3.util.Utility;

/**
 * hideコマンドの実行クラス
 * @author ucchy
 */
public class HideCommand extends LunaChatSubCommand {

    private static final String COMMAND_NAME = "hide";
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
        sender.sendMessage(Messages.usageHide(label));
        sender.sendMessage(Messages.usageHidePlayer(label));
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
        String cname = null;
        boolean isPlayerCommand = false;
        boolean isChannelCommand = false;
        if ( args.length <= 1 ) {
            Channel def = api.getDefaultChannel(sender.getName());
            if ( def != null ) {
                cname = def.getName();
            }
        } else if ( args.length >= 2 ) {

            if ( args[1].equals("list") ) {
                // 指定されたコマンドが「/ch hide list」なら、リストを表示して終了
                for ( String item : getHideInfoList(sender) ) {
                    sender.sendMessage(item);
                }
                return true;

            } else if ( args.length >= 3 && args[1].equalsIgnoreCase("player") ) {
                // 指定されたコマンドが「/ch hide player (player名)」なら、対象をプレイヤーとする。
                isPlayerCommand = true;
                isChannelCommand = false;
                cname = args[2];

            } else if ( args.length >= 3 && args[1].equalsIgnoreCase("channel") ) {
                // 指定されたコマンドが「/ch hide channel (channel名)」なら、対象をチャンネルとする。
                isPlayerCommand = false;
                isChannelCommand = true;
                cname = args[2];

            } else {
                // 「/ch hide (player名 または channel名)」
                cname = args[1];
            }
        }

        // チャンネルかプレイヤーが存在するかどうかをチェックする
        Channel channel = api.getChannel(cname);
        if ( !isPlayerCommand && channel != null ) {
            isChannelCommand = true;
        } else if ( Utility.existsOfflinePlayer(cname) ) {
            sender.sendMessage(Messages.errmsgNotExistChannelAndPlayer());
            return true;
        }

        if ( isChannelCommand ) {
            // チャンネルが対象の場合の処理

            // 既に非表示になっていないかどうかをチェックする
            if ( channel.getHided().contains(sender) ) {
                sender.sendMessage(Messages.errmsgAlreadyHided());
                return true;
            }

            // メンバーかどうかをチェックする
            if ( !channel.getMembers().contains(sender) ) {
                sender.sendMessage(Messages.errmsgNomember());
                return true;
            }

            // 設定する
            channel.getHided().add(sender);
            channel.save();
            sender.sendMessage(Messages.cmdmsgHided(channel.getName()));

            return true;

        } else {
            // プレイヤーが対象の場合の処理

            // 既に非表示になっていないかどうかをチェックする
            ChannelMember hided = ChannelMember.getChannelMember(cname);
            if ( api.getHidelist(hided).contains(sender) ) {
                sender.sendMessage(Messages.errmsgAlreadyHidedPlayer());
                return true;
            }

            // 自分自身を指定していないかどうかチェックする
            if ( hided.equals(sender) ) {
                sender.sendMessage(Messages.errmsgCannotHideSelf());
                return true;
            }

            // 設定する
            api.addHidelist(sender, hided);
            sender.sendMessage(Messages.cmdmsgHidedPlayer(hided.getDisplayName()));

            return true;
        }
    }

    /**
     * hide情報のメッセージを取得する
     * @param player 対象となるプレイヤー
     * @return メッセージ
     */
    private ArrayList<String> getHideInfoList(ChannelMember player) {

        ArrayList<String> items = new ArrayList<String>();
        items.add(Messages.hideChannelFirstLine());
        for ( String channel : getHideChannelNameList(player) ) {
            items.add(Messages.listPlainPrefix() + channel);
        }
        items.add(Messages.hidePlayerFirstLine());
        for ( ChannelMember p : api.getHideinfo(player) ) {
            items.add(Messages.listPlainPrefix() + p.getDisplayName());
        }
        items.add(Messages.listEndLine());

        return items;
    }

    /**
     * 指定したプレイヤーが非表示にしているチャンネル名のリストを返す
     * @param player プレイヤー
     * @return 指定したプレイヤーが非表示にしているチャンネルのリスト
     */
    private ArrayList<String> getHideChannelNameList(ChannelMember player) {

        ArrayList<String> names = new ArrayList<String>();
        for ( Channel channel : api.getChannels() ) {
            if ( channel.getHided().contains(player) ) {
                names.add(channel.getName());
            }
        }
        return names;
    }
}
