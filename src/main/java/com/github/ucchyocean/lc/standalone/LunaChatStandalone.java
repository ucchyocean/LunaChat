/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.standalone;

import java.io.File;
import java.net.URISyntaxException;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.LunaChatLogger;
import com.github.ucchyocean.lc.LunaChatMode;
import com.github.ucchyocean.lc.PluginInterface;
import com.github.ucchyocean.lc.channel.ChannelManager;

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
}
