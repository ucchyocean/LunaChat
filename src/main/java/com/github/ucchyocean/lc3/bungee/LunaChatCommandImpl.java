/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.bungee;

import com.github.ucchyocean.lc3.command.LunaChatCommandBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * LunaChatのlunachatコマンド実装クラス
 * @author ucchy
 */
public class LunaChatCommandImpl extends Command implements TabExecutor {

    private LunaChatCommandBungee com = new LunaChatCommandBungee();

    /**
     * コンストラクタ
     * @param name コマンド
     * @param permission パーミッション
     * @param aliases エイリアス
     */
    public LunaChatCommandImpl(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    /**
     * コマンドを実行したときに呼び出されるメソッド
     * @param sender 実行者
     * @param args 実行されたコマンドの引数
     * @see net.md_5.bungee.api.plugin.Command#execute(net.md_5.bungee.api.CommandSender, java.lang.String[])
     */
    public void execute(CommandSender sender, String[] args) {
        com.execute(sender, args);
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
        return com.onTabComplete(sender, args);
    }
}
