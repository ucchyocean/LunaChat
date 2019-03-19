/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * checkコマンドの実行クラス
 * @author ucchy
 */
public class CheckCommand extends SubCommandAbst {

    private static final String LIST_FIRSTLINE = Resources.get("listFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String COMMAND_NAME = "check";
    private static final String PERMISSION_NODE = "lunachat-admin." + COMMAND_NAME;
    private static final String USAGE_KEY1 = "usageCheck1";
    private static final String USAGE_KEY2 = "usageCheck2";
    private static final String COMMAND_TRACKER_NAME = "lunachat-check-command";

    private boolean consoleRemoveTracker;

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
        return CommandType.ADMIN;
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

        boolean isRemove = false;

        Player player = null;
        if ( sender instanceof Player ) {
            player = (Player)sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できる
        if (args.length >= 2 && args[1].equalsIgnoreCase("remove")) {
            if (player != null && player.hasMetadata(COMMAND_TRACKER_NAME)) {
                // フラグをたてて、トラッカーを除去する
                isRemove = true;
                player.removeMetadata(COMMAND_TRACKER_NAME, LunaChat.getInstance());
            } else if (sender instanceof ConsoleCommandSender && consoleRemoveTracker) {
                // フラグをたてて、トラッカーを除去する
                isRemove = true;
                consoleRemoveTracker = false;
            } else {
                // トラッカーが確認できない実行者には、警告を表示して終了する
                sendResourceMessage(sender, PREERR, "errmsgNotInCheckRemoveConfirm", label);
                return true;
            }
        }

        if ( !isRemove ) {
            // チェックの実行と確認メッセージ
            ArrayList<Channel> list = getCheckList();
            if ( list.size() == 0 ) {
                sendResourceMessage(sender, PREINFO, "cmdmsgCheck", list.size());
            } else {
                sendCheckListMessages(sender, list);
                sendResourceMessage(sender, PREINFO, "cmdmsgCheck", list.size());
                sendResourceMessage(sender, PREINFO, "cmdmsgCheckConfirm", label);

                // コマンドトラッカーを設定する
                if (player != null) {
                    player.setMetadata(COMMAND_TRACKER_NAME,
                            new FixedMetadataValue(LunaChat.getInstance(), true));
                } else if (sender instanceof ConsoleCommandSender) {
                    consoleRemoveTracker = true;
                }
            }
            return true;

        } else {
            // クリーンアップの実行
            int counter = 0;
            ArrayList<Channel> list = getCheckList();
            for ( Channel channel : list ) {
                if ( api.removeChannel(channel.getName(), sender) ) {
                    counter++;
                }
            }
            sendResourceMessage(sender, PREINFO, "cmdmsgCheckRemove", counter);

            return true;
        }
    }

    /**
     * 削除対象となるチャンネルのリストを返す
     * @return 削除対象のチャンネルのリスト
     */
    private ArrayList<Channel> getCheckList() {

        ArrayList<Channel> list = new ArrayList<Channel>();
        for ( Channel channel : api.getChannels() ) {
            if ( channel.getModerator().size() == 0 &&
                    !channel.isBroadcastChannel() && !channel.isPersonalChat() ) {
                list.add(channel);
            }
        }
        return list;
    }

    /**
     * 削除対象となるチャンネルを、リスト表示で通知する
     * @param sender 通知先
     * @param list 対象チャンネル
     */
    private void sendCheckListMessages(CommandSender sender, ArrayList<Channel> list) {

        Player player = null;
        if ( sender instanceof Player ) {
            player = (Player)sender;
        }

        ArrayList<String> items = new ArrayList<String>();
        String dchannel = "";
        String playerName = "";
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
        if ( player != null ) {
            playerName = player.getName();
            Channel def = api.getDefaultChannel(playerName);
            if ( def != null ) {
                dchannel = def.getName();
            }
        }

        // メッセージを作成する
        items.add(LIST_FIRSTLINE);
        for ( Channel channel : list ) {

            // デフォルト発言先なら赤にする。
            String disp = ChatColor.WHITE + channel.getName();
            if ( channel.getName().equalsIgnoreCase(dchannel) ) {
                disp = ChatColor.RED + channel.getName();
            }

            if ( player != null &&
                    !channel.getMembers().contains(cp) &&
                    !channel.isGlobalChannel() ) {

                // 参加していないチャンネルならグレーにする
                disp = ChatColor.GRAY + channel.getName();
            }

            String desc = channel.getDescription();
            int onlineNum = channel.getOnlineNum();
            int memberNum = channel.getTotalNum();
            String item = String.format(
                    LIST_FORMAT, disp, onlineNum, memberNum, desc);
            items.add(item);
        }
        items.add(LIST_ENDLINE);

        // メッセージを送信する
        for (String msg : items) {
            sender.sendMessage(msg);
        }
    }
}
