/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.List;

import com.github.ucchyocean.lc.member.ChannelMember;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Lunachatコマンドの処理クラス（Bungee実装）
 * @author ucchy
 */
public class LunaChatCommandBungee extends LunaChatCommand {

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param args 実行されたコマンドの引数
     * @see net.md_5.bungee.api.plugin.Command#execute(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    public void execute(CommandSender sender, String[] args) {
        execute(ChannelMember.getChannelMember(sender), "", args);
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender 実行者
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     * @see net.md_5.bungee.api.plugin.TabExecutor#onTabComplete(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return onTabComplete(ChannelMember.getChannelMember(sender), "", args);
    }

    /**
     * オンラインプレイヤーのうち、プレイヤー名が指定された文字列と前方一致するものをリストにして返す
     * @param pre 検索キー
     * @return プレイヤー名リスト
     * @see com.github.ucchyocean.lc.command.LunaChatCommand#getListPlayerNames(java.lang.String)
     */
    @Override
    protected List<String> getListPlayerNames(String pre) {

        List<String> items = new ArrayList<String>();
        for ( ProxiedPlayer player : ProxyServer.getInstance().getPlayers() ) {
            String pname = player.getName();
            if ( pname == null ) continue;
            if ( pname.toLowerCase().startsWith(pre) ) {
                items.add(player.getName());
            }
        }
        return items;
    }
}
