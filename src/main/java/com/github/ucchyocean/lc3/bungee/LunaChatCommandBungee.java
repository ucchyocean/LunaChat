/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee;

import com.github.ucchyocean.lc3.command.LunaChatCommand;
import com.github.ucchyocean.lc3.member.ChannelMember;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Lunachatコマンドの処理クラス（Bungee実装）
 * @author ucchy
 */
public class LunaChatCommandBungee extends Command implements TabExecutor {

    private LunaChatCommand command;

    /**
     * コンストラクタ
     * @param name
     * @param permission
     * @param aliases
     */
    public LunaChatCommandBungee(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        command = new LunaChatCommand();
    }

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param args 実行されたコマンドの引数
     * @see net.md_5.bungee.api.plugin.Command#execute(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        command.execute(ChannelMember.getChannelMember(sender), "ch", args);
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender 実行者
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     * @see net.md_5.bungee.api.plugin.TabExecutor#onTabComplete(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return command.onTabComplete(ChannelMember.getChannelMember(sender), "ch", args);
    }
}
