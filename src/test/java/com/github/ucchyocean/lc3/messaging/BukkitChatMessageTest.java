/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.messaging;

import com.github.ucchyocean.lc3.member.ChannelMemberOther;

import junit.framework.TestCase;

/**
 *
 * @author ucchy
 */
public class BukkitChatMessageTest extends TestCase {

    public void testSerializeDeserialize() {

        ChannelMemberOther member = new ChannelMemberOther(
                "てすと", "表示名", "ぷれふぃっくす", "さふぃっくす", null, null);

        System.out.println("DEBUG before : " + member.toString());

        BukkitChatMessage message = new BukkitChatMessage(member, "chat message");

        // シリアライズ
        byte[] bytes = message.toByteArray();

        System.out.println("DEBUG bytes : " + new String(bytes));

        // デシリアライズ
        BukkitChatMessage after = BukkitChatMessage.fromByteArray(bytes);

        System.out.println("DEBUG after : " + after.getMember().toString());

        assertTrue(member.toString().equals(after.getMember().toString()));
    }
}
