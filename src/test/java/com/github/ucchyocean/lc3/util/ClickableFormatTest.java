/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.util;

import java.io.File;

import com.github.ucchyocean.lc3.LunaChatStandalone;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.channel.StandaloneChannel;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberDummy;

import junit.framework.TestCase;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 *
 * @author ucchy
 */
public class ClickableFormatTest extends TestCase {

    private static final String DATA_FOLDER = "target" + File.separator + "LunaChatTest";
    private static final String MESSAGES_FOLDER = "target" + File.separator + "classes";

    public void testChannelChatKeyword() {

        File dataFolder = new File(DATA_FOLDER);
        System.out.println("dataFolder : " + dataFolder.getAbsolutePath());
        if ( !dataFolder.exists() ) {
            dataFolder.mkdirs();
        }

        LunaChatStandalone lunachat = new LunaChatStandalone(dataFolder);
        lunachat.onEnable();

        Messages.initialize(new File(MESSAGES_FOLDER), null, "ja");

        //String format = "&f[%color%ch&f]%prefix%displayname%suffix&a:&f %msg";
        String format = "&7[%color%ch&7]%prefix%username: &f%msg";
        String jpFormat = "%japanize&7 $%msg";

        ChannelMember member = new ChannelMemberDummy();
        Channel channel = new StandaloneChannel("r");
        channel.setColorCode(ChatColor.AQUA.toString());

        ClickableFormat f = ClickableFormat.makeFormat(format, member, channel, true);
        f.replace("%msg", jpFormat);

        System.out.println("pre = " + f.toString() + ", legacy text = " + f.toLegacyText());

        BaseComponent[] comps = f.makeTextComponent();

        System.out.println("post : comps len = " + comps.length + ", legacy text = " + makeLegacyText(comps));
        for ( BaseComponent comp : comps ) {
            System.out.println(comp.toString());
        }

//        assertTrue(f.toLegacyText().equals(makeLegacyText(comps)));
    }

    public void testEventChatKeyword() {

        String format = "&f%prefix%displayname%suffix&a:&f %msg";
        ChannelMember member = new ChannelMemberDummy();

        Messages.initialize(new File(MESSAGES_FOLDER), null, "ja");

        ClickableFormat f = ClickableFormat.makeFormat(format, member);

        System.out.println("pre = " + f.toString() + ", legacy text = " + f.toLegacyText());

        BaseComponent[] comps = f.makeTextComponent();

        System.out.println("post : comps len = " + comps.length + ", legacy text = " + makeLegacyText(comps));
        for ( BaseComponent comp : comps ) {
            System.out.println(comp.toString());
        }

//        assertTrue(f.toLegacyText().equals(makeLegacyText(comps)));
    }

    private static String makeLegacyText(BaseComponent[] comps) {
        StringBuilder builder = new StringBuilder();
        for ( BaseComponent comp : comps ) {
            builder.append(comp.toLegacyText());
        }
        return builder.toString();
    }
}
