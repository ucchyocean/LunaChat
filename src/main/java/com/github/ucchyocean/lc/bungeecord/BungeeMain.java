package com.github.ucchyocean.lc.bungeecord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

    private static BungeeMain instance;
    private PluginMessageListener pluginMessageListener;

    @Override
    public void onEnable() {
        instance = this;
        pluginMessageListener = new PluginMessageListener();
        getProxy().getScheduler().schedule(instance, this::updatePlayers, 5, 60, TimeUnit.SECONDS);
        getProxy().registerChannel("lc:tobukkit");
        getProxy().registerChannel("lc:tobungee");
        pluginMessageListener.start();
        PlayerListener.start();
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(instance);
        getProxy().unregisterChannel("lc:tobukkit");
        getProxy().unregisterChannel("lc:tobungee");
        pluginMessageListener.stop();
        PlayerListener.stop();
    }

    public static BungeeMain getInstance() {
        if (instance == null) {
            instance = (BungeeMain) ProxyServer.getInstance().getPluginManager().getPlugin("LunaChat");
            if (instance == null) {
                throw new ExceptionInInitializerError("Cannot initialize LunaChat.");
            }
        }
    
        return instance;
    }

    private void updatePlayers() {
        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOutStream);

            out.writeUTF("updateplayers");
            out.writeUTF(ProxyServer.getInstance().getPlayers().stream()
                    .map(ProxiedPlayer::getName).reduce("", (p1, p2) -> p1 + "," + p2));

            byte[] data = byteOutStream.toByteArray();
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("lc:tobukkit", data);
            }

            out.close();
            byteOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}