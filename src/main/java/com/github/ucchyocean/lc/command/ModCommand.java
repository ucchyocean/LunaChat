/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

/**
 * moderatorコマンドのエイリアス実行クラス、名前のみが異なるが、他は全て一緒。
 * @author ucchy
 */
public class ModCommand extends ModeratorCommand {

    private static final String COMMAND_NAME = "mod";
    
    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }
}
