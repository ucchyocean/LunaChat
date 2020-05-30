/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.standalone;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.LunaChatLogger;
import com.github.ucchyocean.lc3.LunaChatMode;
import com.github.ucchyocean.lc3.PluginInterface;
import com.github.ucchyocean.lc3.channel.ChannelManager;

/**
 * LunaChatのスタンドアロンサーバー
 * @author ucchy
 */
public class LunaChatStandalone implements PluginInterface {

    private LunaChatConfig config;
    private ChannelManager manager;

    public void onEnable() {

        LunaChat.setPlugin(this);
        LunaChat.setMode(LunaChatMode.STANDALONE);

        // 初期化
        manager = new ChannelManager();

        // コンフィグ取得
        config = new LunaChatConfig(getDataFolder(), getPluginJarFile());
    }

    @Override
    public File getPluginJarFile() {
        try {
            return new File(LunaChatStandalone.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LunaChatConfig getLunaChatConfig() {
        return config;
    }

    @Override
    public LunaChatAPI getLunaChatAPI() {
        return manager ;
    }

    @Override
    public File getDataFolder() {

        // 現在のフォルダ下にLunaChatフォルダを作成して返す
        String cd = new File(".").getAbsoluteFile().getParent();
        File folder = new File(cd, "LunaChat");
        if ( !folder.exists() ) folder.mkdirs();
        return folder;
    }

    @Override
    public LunaChatLogger getNormalChatLogger() {
        return null;
    }

    /**
     * オンラインのプレイヤー名一覧を取得する
     * @return オンラインのプレイヤー名一覧
     */
    public Set<String> getOnlinePlayerNames() {
        return null;
    }

    @Override
    public void log(Level level, String msg) {
        // TODO 未実装
    }

}
