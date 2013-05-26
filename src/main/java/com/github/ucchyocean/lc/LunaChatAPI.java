/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.util.Collection;

/**
 * @author ucchy
 * LunaChat APIクラス
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
     * チャンネルを削除する
     * @param channelName 削除するチャンネル名
     * @return 削除したかどうか
     */
    public boolean removeChannel(String channelName);

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
}
