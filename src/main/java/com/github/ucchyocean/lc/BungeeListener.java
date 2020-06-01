package com.github.ucchyocean.lc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelBungeePlayer;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener implements PluginMessageListener {

    List<String> onlinePlayers = new ArrayList<>();

    BungeeListener() {}

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("lc:tobukkit")) {
            return;
        }

        try {
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(message);
            DataInputStream in = new DataInputStream(byteArrayIn);

            String operation = in.readUTF();
            if (operation.equalsIgnoreCase("chat")) {
                String channelName = in.readUTF();
                String playerName = in.readUTF();
                String playerDisplayName = in.readUTF();
                String playerPrefix = in.readUTF();
                String playerSuffix = in.readUTF();
                String worldName = in.readUTF();
                String chatMessage = in.readUTF();
                boolean japanize = in.readBoolean();
                boolean canUseColorCode = in.readBoolean();

                in.close();
                byteArrayIn.close();

                boolean defaultJapanize = LunaChat.getInstance().getLunaChatAPI().isPlayerJapanize(playerName);
                if (japanize != defaultJapanize) {
                    LunaChat.getInstance().getLunaChatAPI().setPlayersJapanize(playerName, japanize);
                }

                ChannelPlayer channelPlayer = new ChannelBungeePlayer(playerName, playerPrefix, playerSuffix,
                        worldName, playerDisplayName, canUseColorCode);

                // プライベートメッセージ
                if (channelName.contains(">")) {
                    String invited = channelName.substring(channelName.indexOf(">") + 1);
                    LunaChat.getInstance().getLunaChatAPI()
                            .sendTellMessage(channelPlayer, invited, chatMessage);
                    return;
                }

                Channel lcChannel = LunaChat.getInstance().getLunaChatAPI().getChannel(channelName);
                if (lcChannel != null && lcChannel.isBungee() && !lcChannel.isWorldRange() && lcChannel.getChatRange() == 0) {
                    lcChannel.chat(channelPlayer, chatMessage);
                }

                if (japanize != defaultJapanize) {
                    LunaChat.getInstance().getLunaChatAPI().setPlayersJapanize(playerName, defaultJapanize);
                }

            } else if (operation.equalsIgnoreCase("updateplayers")) {
                onlinePlayers = new ArrayList<>(Arrays.asList(in.readUTF().split(",", -1)));
            } else if (operation.equalsIgnoreCase("joinplayer")) {
                String playerName = in.readUTF();
                if (!onlinePlayers.contains(playerName)) {
                    onlinePlayers.add(playerName);
                }
            } else if (operation.equalsIgnoreCase("disconnectplayer")) {
                onlinePlayers.remove(in.readUTF());
            }

            in.close();
            byteArrayIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
