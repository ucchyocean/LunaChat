package com.github.ucchyocean.lc.bungeecord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private static final BungeeMain PLUGIN = BungeeMain.getInstance();
    private static final PlayerListener INSTANCE = new PlayerListener();

    private PlayerListener() {
    }

    static void start() {
        ProxyServer.getInstance().getPluginManager().registerListener(PLUGIN, INSTANCE);
    }

    static void stop() {
        ProxyServer.getInstance().getPluginManager().unregisterListener(INSTANCE);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        onConnectionChange(event, event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        onConnectionChange(event, event.getPlayer().getName());
    }

    private void onConnectionChange(Event event, String player) {
        String operation;
        if (event instanceof PostLoginEvent) {
            operation = "joinplayer";
        } else if (event instanceof PlayerDisconnectEvent) {
            operation = "disconnectplayer";
        } else {
            return;
        }

        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteArrayOut);
            out.writeUTF(operation);
            out.writeUTF(player);
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                server.sendData("lc:tobukkit", byteArrayOut.toByteArray());
            }
            out.close();
            byteArrayOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}