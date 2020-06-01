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

/**
 * LunaChat APIクラス
 * @author ucchy
 */
public interface LunaChatAPI {

    /**
     * 指定したチャンネル名が存在するかどうかを返す
     * @param channelName チャンネル名
     * @return 存在するかどうか
     */
    public boolean isExistChannel(String channelName);

    /**
     * 全てのチャンネルを返す
     * @return 全てのチャンネル
     */
    public Collection<Channel> getChannels();

    /**
     * プレイヤーが参加しているチャンネルを返す
     * @param playerName プレイヤー名
     * @return チャンネル
     */
    public Collection<Channel> getChannelsByPlayer(String playerName);

    /**
     * プレイヤーが参加しているデフォルトのチャンネルを返す
     * @param playerName プレイヤー
     * @return チャンネル
     */
    public Channel getDefaultChannel(String playerName);

    /**
     * プレイヤーのデフォルトチャンネルを設定する
     * @param playerName プレイヤー
     * @param channelName チャンネル名
     */
    public void setDefaultChannel(String playerName, String channelName);

    /**
     * 指定した名前のプレイヤーに設定されている、デフォルトチャンネルを削除する
     * @param playerName プレイヤー名
     */
    public void removeDefaultChannel(String playerName);

    /**
     * チャンネルを取得する
     * @param channelName チャンネル名
     * @return チャンネル
     */
    public Channel getChannel(String channelName);

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @return 作成されたチャンネル
     */
    public Channel createChannel(String channelName);

    /**
     * 新しいチャンネルを作成する
     * @param channelName チャンネル名
     * @param sender チャンネルを作成した人
     * @return 作成されたチャンネル
     */
    public Channel createChannel(String channelName, CommandSender sender);

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @return 削除したかどうか
     */
    public boolean removeChannel(String channelName);

    /**
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @param sender チャンネルを削除した人
     * @return 削除したかどうか
     */
    public boolean removeChannel(String channelName, CommandSender sender);

    /**
     * テンプレートを取得する
     * @param id テンプレートID
     * @return テンプレート
     */
    public String getTemplate(String id);

    /**
     * テンプレートを登録する
     * @param id テンプレートID
     * @param template テンプレート
     */
    public void setTemplate(String id, String template);

    /**
     * テンプレートを削除する
     * @param id テンプレートID
     */
    public void removeTemplate(String id);

    /**
     * 辞書データを全て取得する
     * @return 辞書データ
     */
    public HashMap<String, String> getAllDictionary();

    /**
     * 新しい辞書データを追加する
     * @param key キー
     * @param value 値
     */
    public void setDictionary(String key, String value);

    /**
     * 指定したキーの辞書データを削除する
     * @param key キー
     */
    public void removeDictionary(String key);

    /**
     * 該当のプレイヤーに関連するhidelistを取得する。
     * @param key プレイヤー
     * @return 指定されたプレイヤーをhideしているプレイヤー(非null)
     */
    public List<ChannelPlayer> getHidelist(ChannelPlayer key);

    /**
     * 該当のプレイヤーがhideしているプレイヤーのリストを返す。
     * @param player プレイヤー
     * @return 指定したプレイヤーがhideしているプレイヤーのリスト
     */
    public ArrayList<ChannelPlayer> getHideinfo(ChannelPlayer player);

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーをhideするように設定する。
     * @param player hideする側のプレイヤー
     * @param hided hideされる側のプレイヤー
     */
    public void addHidelist(ChannelPlayer player, ChannelPlayer hided);

    /**
     * 指定されたプレイヤーが、指定されたプレイヤーのhideを解除するように設定する。
     * @param player hideしていた側のプレイヤー
     * @param hided hideされていた側のプレイヤー
     */
    public void removeHidelist(ChannelPlayer player, ChannelPlayer hided);

    /**
     * Japanize変換を行う
     * @param message 変換するメッセージ
     * @param type 変換タイプ
     * @return 変換後のメッセージ、ただしイベントでキャンセルされた場合はnullが返されるので注意
     */
    public String japanize(String message, JapanizeType type);

    /**
     * 該当プレイヤーのJapanize変換をオン/オフする
     * @param playerName 設定するプレイヤー名
     * @param doJapanize Japanize変換するかどうか
     */
    public void setPlayersJapanize(String playerName, boolean doJapanize);

    /**
     * プレイヤーのJapanize設定を返す
     * @param playerName プレイヤー名
     * @return Japanize設定
     */
    public boolean isPlayerJapanize(String playerName);

    /**
     * LunaChatの全データを再読み込みする
     */
    public void reloadAllData();

    /**
     * Tellコマンドの実行処理を行う
     * @param inviter
     * @param invitedName
     * @param message
     */
    public void sendTellMessage(ChannelPlayer inviter, String invitedName, String message);
}
