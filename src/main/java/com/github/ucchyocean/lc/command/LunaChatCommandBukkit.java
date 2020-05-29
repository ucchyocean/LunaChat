/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.member.ChannelMember;

/**
 * Lunachatコマンドの処理クラス（Bukkit実装）
 * @author ucchy
 */
public class LunaChatCommandBukkit extends LunaChatCommand implements CommandExecutor {

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param command 実行されたコマンド
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 実行したかどうか（falseを返した場合、サーバーがUsageを表示する）
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(ChannelMember.getChannelMember(sender), label, args);
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender TABキー補完の実行者
     * @param command 実行されたコマンド
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return onTabComplete(ChannelMember.getChannelMember(sender), label, args);
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
        for ( Player player : Bukkit.getOnlinePlayers() ) {
            String pname = player.getName();
            if ( pname == null ) continue;
            if ( pname.toLowerCase().startsWith(pre) ) {
                items.add(player.getName());
            }
        }
        return items;
    }
}
