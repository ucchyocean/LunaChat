/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * listコマンドの実行クラス
 * @author ucchy
 */
public class ListCommand extends SubCommandAbst {

    private static final String LIST_FIRSTLINE = Resources.get("listFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String COMMAND_NAME = "list";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageList";

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

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // リストを取得して表示する
        ArrayList<String> list = getList(player);
        for (String msg : list) {
            sender.sendMessage(msg);
        }
        return true;
    }

    /**
     * リスト表示用のリストを返す
     * @param player プレイヤー、指定しない場合はnullにする
     * @return リスト
     */
    private ArrayList<String> getList(Player player) {

        ArrayList<String> items = new ArrayList<String>();
        String dchannel = "";
        String playerName = "";
        if ( player != null ) {
            playerName = player.getName();
            Channel def = api.getDefaultChannel(playerName);
            if ( def == null ) {
                dchannel = "";
            } else {
                dchannel = def.getName();
            }
        }
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);

        items.add(LIST_FIRSTLINE);
        for ( Channel channel : api.getChannels() ) {

            // BANされているチャンネルは表示しない
            if ( channel.getBanned().contains(cp) ) {
                continue;
            }

            // 個人チャットはリストに表示しない
            if ( channel.isPersonalChat() ) {
                continue;
            }

            // デフォルト発言先なら赤に、非表示中なら暗青にする。
            String disp = ChatColor.WHITE + channel.getName();
            if ( channel.getName().equalsIgnoreCase(dchannel) ) {
                disp = ChatColor.RED + channel.getName();
            } else if ( channel.getHided().contains(cp) ) {
                disp = ChatColor.DARK_AQUA + channel.getName();
            }

            if ( player != null &&
                    !channel.getMembers().contains(cp) &&
                    !channel.isGlobalChannel() ) {

                // 未参加で visible=false のチャンネルは表示しない
                if ( !channel.isVisible() ) {
                    continue;
                }
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

        return items;
    }
}
