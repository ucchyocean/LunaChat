/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Resources;

/**
 * サブコマンドの抽象クラス
 * @author ucchy
 */
public abstract class SubCommandAbst {

    protected static final String PREINFO = Resources.get("infoPrefix");
    protected static final String PREERR = Resources.get("errorPrefix");

    protected LunaChatAPI api;
    protected LunaChatConfig config;
    
    /**
     * コンストラクタ
     */
    public SubCommandAbst() {
        api = LunaChat.instance.getLunaChatAPI();
        config = LunaChat.instance.getLunaChatConfig();
    }
    
    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     *
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    protected void sendResourceMessage(
            CommandSender sender, String pre,
            String key, Object... args) {
        String msg = String.format(pre + Resources.get(key), args);
        sender.sendMessage(msg);
    }
    
    /**
     * コマンドを取得します。
     * @return コマンド
     */
    public abstract String getCommandName();
    
    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     */
    public abstract String getPermissionNode();
    

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     */
    public abstract void sendUsageMessage(
            CommandSender sender, String label);
    
    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     */
    public abstract boolean runCommand(
            CommandSender sender, String label, String[] args);
}
