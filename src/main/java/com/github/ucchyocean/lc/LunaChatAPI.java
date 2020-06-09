/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * LunaChat APIクラス
 * @author ucchy
 */
public class LunaChatAPI {

    /**
     * 指定したチャンネル名が存在するかどうかを返す
     * @param channelName チャンネル名
     * @return 存在するかどうか
     */
    public boolean isExistChannel(String channelName) {
        return com.github.ucchyocean.lc3.LunaChat.getAPI().isExistChannel(channelName);
    }

    /**
     * 全てのチャンネルを返す
     * @return 全てのチャンネル
     */
    public Collection<Channel> getChannels() {
        Collection<Channel> result = new ArrayList<Channel>();
        for ( com.github.ucchyocean.lc3.channel.Channel c :
                com.github.ucchyocean.lc3.LunaChat.getAPI().getChannels() ) {
            result.add(new Channel(c));
        }
        return result;
    }

    /**
     * プレイヤーが参加しているチャンネルを返す
     * @param playerName プレイヤー名
     * @return チャンネル
     */
    public Collection<Channel> getChannelsByPlayer(String playerName) {
        Collection<Channel> result = new ArrayList<Channel>();
        for ( com.github.ucchyocean.lc3.channel.Channel c :
                com.github.ucchyocean.lc3.LunaChat.getAPI().getChannelsByPlayer(playerName) ) {
            result.add(new Channel(c));
        }
        return result;
    }

    /**
     * プレイヤーが参加しているデフォルトのチャンネルを返す
     * @param playerName プレイヤー
     * @return チャンネル
     */
    public Channel getDefaultChannel(String playerName) {
        com.github.ucchyocean.lc3.channel.Channel c =
                com.github.ucchyocean.lc3.LunaChat.getAPI().getDefaultChannel(playerName);
        if ( c != null ) return new Channel(c);
        return null;
    }

    /**
     * プレイヤーのデフォルトチャンネルを設定する
     * @param playerName プレイヤー
     * @param channelName チャンネル名
     */
    public void setDefaultChannel(String playerName, String channelName) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().setDefaultChannel(playerName, channelName);
    }

    /**
     * 指定した名前のプレイヤーに設定されている、デフォルトチャンネルを削除する
     * @param playerName プレイヤー名
     */
    public void removeDefaultChannel(String playerName) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().removeDefaultChannel(playerName);
    }

    /**
     * チャンネルを取得する
     * @param channelName チャンネル名
     * @return チャンネル
     */
    public Channel getChannel(String channelName) {
        com.github.ucchyocean.lc3.channel.Channel c =
                com.github.ucchyocean.lc3.LunaChat.getAPI().getChannel(channelName);
        if ( c != null ) return new Channel(c);
        return null;
    }

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @return 作成されたチャンネル
     */
    public Channel createChannel(String channelName) {
        com.github.ucchyocean.lc3.channel.Channel c =
                com.github.ucchyocean.lc3.LunaChat.getAPI().createChannel(channelName);
        if ( c != null ) return new Channel(c);
        return null;
    }

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @param sender チャンネルを作成した人
     * @return 作成されたチャンネル
     */
    public Channel createChannel(String channelName, CommandSender sender) {
        com.github.ucchyocean.lc3.channel.Channel c =
                com.github.ucchyocean.lc3.LunaChat.getAPI().createChannel(
                        channelName, ChannelMember.getChannelMember(sender));
        if ( c != null ) return new Channel(c);
        return null;
    }

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @return 削除したかどうか
     */
    public boolean removeChannel(String channelName) {
        return com.github.ucchyocean.lc3.LunaChat.getAPI().removeChannel(channelName);
    }

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @param sender チャンネルを削除した人
     * @return 削除したかどうか
     */
    public boolean removeChannel(String channelName, CommandSender sender) {
        return com.github.ucchyocean.lc3.LunaChat.getAPI().removeChannel(
                channelName, ChannelMember.getChannelMember(sender));
    }

    /**
     * テンプレートを取得する
     * @param id テンプレートID
     * @return テンプレート
     */
    public String getTemplate(String id) {
        return com.github.ucchyocean.lc3.LunaChat.getAPI().getTemplate(id);
    }

    /**
     * テンプレートを登録する
     * @param id テンプレートID
     * @param template テンプレート
     */
    public void setTemplate(String id, String template) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().setTemplate(id, template);
    }

    /**
     * テンプレートを削除する
     * @param id テンプレートID
     */
    public void removeTemplate(String id) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().removeTemplate(id);
    }

    /**
     * 辞書データを全て取得する
     * @return 辞書データ
     */
    public HashMap<String, String> getAllDictionary() {
        return new HashMap<String, String>(
                com.github.ucchyocean.lc3.LunaChat.getAPI().getAllDictionary());
    }

    /**
     * 新しい辞書データを追加する
     * @param key キー
     * @param value 値
     */
    public void setDictionary(String key, String value) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().setDictionary(key, value);
    }

    /**
     * 指定したキーの辞書データを削除する
     * @param key キー
     */
    public void removeDictionary(String key) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().removeDictionary(key);
    }

    /**
     * 該当のプレイヤーに関連するhidelistを取得する。
     * @param key プレイヤー
     * @return 指定されたプレイヤーをhideしているプレイヤー(非null)
     */
    public List<ChannelPlayer> getHidelist(ChannelPlayer key) {
        // TODO 未実装
        return null;
    }

    /**
     * 該当のプレイヤーがhideしているプレイヤーのリストを返す。
     * @param player プレイヤー
     * @return 指定したプレイヤーがhideしているプレイヤーのリスト
     */
    public ArrayList<ChannelPlayer> getHideinfo(ChannelPlayer player) {
        // TODO 未実装
        return null;
    }

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーをhideするように設定する。
     * @param player hideする側のプレイヤー
     * @param hided hideされる側のプレイヤー
     */
    public void addHidelist(ChannelPlayer player, ChannelPlayer hided) {
        // TODO 未実装
    }

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーのhideを解除するように設定する。
     * @param player hideしていた側のプレイヤー
     * @param hided hideされていた側のプレイヤー
     */
    public void removeHidelist(ChannelPlayer player, ChannelPlayer hided) {
        // TODO 未実装
    }

    /**
     * Japanize変換を行う
     * @param message 変換するメッセージ
     * @param type 変換タイプ
     * @return 変換後のメッセージ、ただしイベントでキャンセルされた場合はnullが返されるので注意
     */
    public String japanize(String message, JapanizeType type) {
        String value = (type != null) ? type.name() : "";
        com.github.ucchyocean.lc3.japanize.JapanizeType t =
            com.github.ucchyocean.lc3.japanize.JapanizeType.fromID(value,
                    com.github.ucchyocean.lc3.japanize.JapanizeType.GOOGLE_IME);
        return com.github.ucchyocean.lc3.LunaChat.getAPI().japanize(message, t);
    }

    /**
     * 該当プレイヤーのJapanize変換をオン/オフする
     * @param playerName 設定するプレイヤー名
     * @param doJapanize Japanize変換するかどうか
     */
    public void setPlayersJapanize(String playerName, boolean doJapanize) {
        com.github.ucchyocean.lc3.LunaChat.getAPI().setPlayersJapanize(playerName, doJapanize);
    }

    /**
     * プレイヤーのJapanize設定を返す
     * @param playerName プレイヤー名
     * @return Japanize設定
     */
    public boolean isPlayerJapanize(String playerName) {
        return com.github.ucchyocean.lc3.LunaChat.getAPI().isPlayerJapanize(playerName);
    }
}
