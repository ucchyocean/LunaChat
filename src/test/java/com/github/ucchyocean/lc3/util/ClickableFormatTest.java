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

    public void testChannelChatKeyword() {

        File dataFolder = new File("target\\LunaChatTest");
        System.out.println("dataFolder : " + dataFolder.getAbsolutePath());
        if ( !dataFolder.exists() ) {
            dataFolder.mkdirs();
        }

        LunaChatStandalone lunachat = new LunaChatStandalone(dataFolder);
        lunachat.onEnable();

        Messages.initialize(new File("src\\main\\resources"), null, "ja");

        String format = "&f[%color%ch&f]%prefix%displayname%suffix&a:&f %msg";
        ChannelMember member = new ChannelMemberDummy();
        Channel channel = new StandaloneChannel("r");

        ClickableFormat f = ClickableFormat.makeFormat(format, member, channel, true);

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

        Messages.initialize(new File("src\\main\\resources"), null, "ja");

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
