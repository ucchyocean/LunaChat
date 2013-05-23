/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.japanize;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ucchyocean.lc.Channel;

/**
 * @author ucchy
 *
 */
public class ConvertTask extends BukkitRunnable {

    private String org;
    private JapanizeType type;
    private Channel channel;

    public ConvertTask(String org, JapanizeType type, Channel channel) {
        this.org = org;
        this.type = type;
        this.channel = channel;
    }

    @Override
    public void run() {

//        long startTime = System.currentTimeMillis();

        if ( type == JapanizeType.NONE ) {
            return;
        }

        String msg = KanaConverter.conv(org);

//        System.out.print("kana:" + msg);
//        System.out.println("(" + (System.currentTimeMillis() - startTime) + " milli sec)");
//
//        startTime = System.currentTimeMillis();

        if ( type == JapanizeType.GOOGLE_IME ) {
            msg = IMEConverter.convByGoogleIME(msg);
        } else if ( type == JapanizeType.SOCIAL_IME ) {
            msg = IMEConverter.convBySocialIME(msg);
        }

//        System.out.print("IME: " + msg);
//        System.out.println("(" + (System.currentTimeMillis() - startTime) + " milli sec)");

        // TODO: msg をフォーマットする仕組みを作るべき
        if ( channel != null ) {
            channel.sendInformation(msg);
        } else {
            Bukkit.broadcastMessage(msg);
        }
    }
}

