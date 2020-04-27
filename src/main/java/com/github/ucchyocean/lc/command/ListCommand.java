/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private static final String LIST_FIRSTLINE_PAGING = Resources.get("listFirstLinePaging");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");

    private static final String COMMAND_NAME = "list";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageList";

    private static final int PAGE_SIZE = 8;

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

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        int page = 0;
        if ( args.length >= 2 && args[1].matches("[0-9]") ) {
            page = Integer.parseInt(args[1]);
        }

        // リストを取得して表示する
        ArrayList<String> list = getList(player, page);
        for (String msg : list) {
            sender.sendMessage(msg);
        }
        return true;
    }

    /**
     * リスト表示用のリストを返す
     * @param player プレイヤー、指定しない場合はnullにする
     * @param page 表示するページ、0を指定した場合は全表示
     * @return リスト
     */
    private ArrayList<String> getList(Player player, int page) {

        ArrayList<String> list = getPlayerList(player);
        int size = list.size();
        int maxPage = (int)(size / PAGE_SIZE) + 1;

        if ( page < 0 ) page = 0;
        if ( page > maxPage ) page = maxPage;

        ArrayList<String> items = new ArrayList<>();
        if ( page == 0 ) { // 全表示
            items.add(LIST_FIRSTLINE);
            items.addAll(list);
            items.add(LIST_ENDLINE);
        } else { // ページ表示
            items.add(String.format(LIST_FIRSTLINE_PAGING, page, maxPage));
            int endIndex = (page * PAGE_SIZE > size) ? size : page * PAGE_SIZE;
            items.addAll(list.subList((page - 1) * PAGE_SIZE, endIndex));
            items.add(LIST_ENDLINE);
        }

        return items;
    }

    /**
     * 指定されたプレイヤーに対するチャンネルリストを返す
     * @param player プレイヤー
     * @return チャンネルリスト
     */
    private ArrayList<String> getPlayerList(Player player) {

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

        // チャンネルを取得して、チャンネル名でソートする
        ArrayList<Channel> channels = new ArrayList<>(api.getChannels());
        Collections.sort(channels, new Comparator<Channel>() {
            public int compare(Channel c1, Channel c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });

        // 指定されたプレイヤー名に合うように、フィルタ＆表示用整形する。
        for ( Channel channel : channels ) {

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

        return items;
    }
}
