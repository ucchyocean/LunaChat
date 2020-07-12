/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberOther;
import com.github.ucchyocean.lc3.util.BlockLocation;

/**
 * BukkitからBungeeCordへプレイヤーの発言を送信するためのメッセージクラス
 * @author ucchy
 */
public class BukkitChatMessage {

    private ChannelMemberOther member;
    private String message;

    /**
     * コンストラクタ
     * @param member 発言者
     * @param message 発言内容
     */
    public BukkitChatMessage(ChannelMemberOther member, String message) {
        this.member = member;
        this.message = message;
    }

    /**
     * 発言者を取得する
     * @return member
     */
    public ChannelMember getMember() {
        return member;
    }

    /**
     * 発言内容を取得する
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * このメッセージをbyte配列に変換する
     * @return byte配列
     */
    public byte[] toByteArray() {
        try ( ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(baos) ) {
            out.writeUTF(member.getName());
            out.writeUTF(member.getDisplayName());
            out.writeUTF(member.getPrefix());
            out.writeUTF(member.getSuffix());
            out.writeUTF(member.getLocation() == null ? "null" : member.getLocation().toString());
            out.writeUTF(message);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * byte配列からメッセージに変換する
     * @param bytes byte配列
     * @return メッセージ
     */
    public static @Nullable BukkitChatMessage fromByteArray(byte[] bytes) {
        try ( DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes)) ) {
            String name = in.readUTF();
            String displayName = in.readUTF();
            String prefix = in.readUTF();
            String suffix = in.readUTF();
            BlockLocation location = BlockLocation.fromString(in.readUTF());
            ChannelMemberOther member = new ChannelMemberOther(name, displayName, prefix, suffix, location);
            String message = in.readUTF();
            return new BukkitChatMessage(member, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("n=%s, d=%s, p=%s, s=%s, m=%s",
                member.getName(), member.getDisplayName(), member.getPrefix(), member.getSuffix(), message);
    }
}
