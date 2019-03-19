/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;

/**
 * moderatorコマンドのエイリアス実行クラス、名前のみが異なるが、他は全て一緒。
 * @author ucchy
 */
public class ModCommand extends ModeratorCommand {

    private static final String COMMAND_NAME = "mod";
    private static final String USAGE_KEY = "usageMod";

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
}
