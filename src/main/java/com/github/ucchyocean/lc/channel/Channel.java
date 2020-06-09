/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * チャンネル
 * @author ucchy
 */
public class Channel {

    private com.github.ucchyocean.lc3.channel.Channel channel;

    /**
     * コンストラクタ
     * @param channel 新バージョンのチャンネル
     */
    public Channel(com.github.ucchyocean.lc3.channel.Channel channel) {
        this.channel = channel;
    }

    /**
     * 1:1チャットかどうか
     * @return 1:1チャットかどうか
     */
    public boolean isPersonalChat() {
        return channel.isPersonalChat();
    }

    /**
     * ブロードキャストチャンネルかどうか
     * @return ブロードキャストチャンネルかどうか
     */
    public boolean isBroadcastChannel() {
        return channel.isBroadcastChannel();
    }

    /**
     * グローバルチャンネルかどうか
     * @return グローバルチャンネルかどうか
     */
    public boolean isGlobalChannel() {
        return channel.isGlobalChannel();
    }

    /**
     * 強制参加チャンネルかどうか
     * @return 強制参加チャンネルかどうか
     */
    public boolean isForceJoinChannel() {
        return channel.isForceJoinChannel();
    }

    /**
     * このチャンネルのモデレータ権限を持っているかどうかを確認する
     * @param sender 権限を確認する対象
     * @return チャンネルのモデレータ権限を持っているかどうか
     */
    public boolean hasModeratorPermission(CommandSender sender) {
        return channel.hasModeratorPermission(ChannelMember.getChannelMember(sender));
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    public void chat(ChannelPlayer player, String message) {
        // TODO player.getPlayer()で問題ないか、要確認
        channel.chat(ChannelMember.getChannelMember(player.getPlayer()), message);
    }

    /**
     * ほかの連携先などから、このチャットに発言する
     * @param player プレイヤー名
     * @param source 連携元を判別する文字列
     * @param message メッセージ
     */
    public void chatFromOtherSource(String player, String source, String message) {
        channel.chatFromOtherSource(player, source, message);
    }

    /**
     * メンバーを追加する
     * @param name 追加するプレイヤー
     */
    public void addMember(ChannelPlayer player) {
        throw new NotImplementedException("This is legacy method. Do not call!");
    }

    /**
     * メンバーを削除する
     * @param name 削除するプレイヤー
     */
    public void removeMember(ChannelPlayer player) {
        throw new NotImplementedException("This is legacy method. Do not call!");
    }

    /**
     * モデレータを追加する
     * @param player 追加するプレイヤー
     */
    public void addModerator(ChannelPlayer player) {
        throw new NotImplementedException("This is legacy method. Do not call!");
    }

    /**
     * モデレータを削除する
     * @param player 削除するプレイヤー
     */
    public void removeModerator(ChannelPlayer player) {
        throw new NotImplementedException("This is legacy method. Do not call!");
    }

    /**
     * プレイヤーに関連する、システムメッセージをチャンネルに流す
     * @param key リソースキー
     * @param player プレイヤー
     */
    protected void sendSystemMessage(String key, ChannelPlayer player) {
        // do nothing.
    }

    /**
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param player プレイヤー（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     * @param displayName 発言者の表示名（APIに使用されます）
     */
    public void sendMessage(
            ChannelPlayer player, String message, String format, boolean sendDynmap, String displayName) {
        // do nothing.
    }

    /**
     * チャンネル情報を返す
     * @param forModerator モデレータ向けの情報を含めるかどうか
     * @return チャンネル情報
     */
    public ArrayList<String> getInfo(boolean forModerator) {
        return new ArrayList<String>(channel.getInfo(forModerator));
    }

    /**
     * ログファイルを読み込んで、ログデータを取得する
     * @param player プレイヤー名、フィルタしないならnullを指定すること
     * @param filter フィルタ、フィルタしないならnullを指定すること
     * @param date 日付、今日のデータを取得するならnullを指定すること
     * @param reverse 逆順取得
     * @return ログデータ
     */
    public ArrayList<String> getLog(
            String player, String filter, String date, boolean reverse) {
        return new ArrayList<String>(channel.getLog(player, filter, date, reverse));
    }

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     */
    public int getOnlineNum() {
        return channel.getOnlineNum();
    }

    /**
     * チャンネルの総参加人数を返す
     * @return 総参加人数
     */
    public int getTotalNum() {
        return channel.getTotalNum();
    }

    /**
     * 期限付きBanや期限付きMuteをチェックし、期限が切れていたら解除を行う
     */
    public void checkExpires() {
        channel.checkExpires();
    }

    /**
     * チャンネルの別名を返す
     * @return チャンネルの別名
     */
    public String getAlias() {
        return channel.getAlias();
    }

    /**
     * チャンネルの別名を設定する
     * @param alias チャンネルの別名
     */
    public void setAlias(String alias) {
        channel.setAlias(alias);
    }

    /**
     * チャンネルの説明文を返す
     * @return チャンネルの説明文
     */
    public String getDescription() {
        return channel.getDescription();
    }

    /**
     * チャンネルの説明文を設定する
     * @param description チャンネルの説明文
     */
    public void setDescription(String description) {
        channel.setDescription(description);
    }

    /**
     * チャンネルのパスワードを返す
     * @return チャンネルのパスワード
     */
    public String getPassword() {
        return channel.getPassword();
    }

    /**
     * チャンネルのパスワードを設定する
     * @param password チャンネルのパスワード
     */
    public void setPassword(String password) {
        channel.setPassword(password);
    }

    /**
     * チャンネルの可視性を返す
     * @return チャンネルの可視性
     */
    public boolean isVisible() {
        return channel.isVisible();
    }

    /**
     * チャンネルの可視性を設定する
     * @param visible チャンネルの可視性
     */
    public void setVisible(boolean visible) {
        channel.setVisible(visible);
    }

    /**
     * チャンネルのメッセージフォーマットを返す
     * @return チャンネルのメッセージフォーマット
     */
    public String getFormat() {
        return channel.getFormat();
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     * @param format チャンネルのメッセージフォーマット
     */
    public void setFormat(String format) {
        channel.setFormat(format);
    }

    /**
     * チャンネルのメンバーを返す
     * @return チャンネルのメンバー
     */
    public List<ChannelPlayer> getMembers() {
        List<ChannelPlayer> result = new ArrayList<ChannelPlayer>();

        for ( ChannelMember member : channel.getMembers() ) {
            // TODO 未実装
        }

        return result;
    }

    /**
     * チャンネルのモデレーターを返す
     * @return チャンネルのモデレーター
     */
    public List<ChannelPlayer> getModerator() {
        // TODO 未実装
        return null;
    }

    /**
     * チャンネルのBANリストを返す
     * @return チャンネルのBANリスト
     */
    public List<ChannelPlayer> getBanned() {
        // TODO 未実装
        return null;
    }

    /**
     * チャンネルのMuteリストを返す
     * @return チャンネルのMuteリスト
     */
    public List<ChannelPlayer> getMuted() {
        // TODO 未実装
        return null;
    }

    /**
     * 期限付きBANの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return banExpires
     */
    public Map<ChannelPlayer, Long> getBanExpires() {
        // TODO 未実装
        return null;
    }

    /**
     * 期限付きMuteの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return muteExpires
     */
    public Map<ChannelPlayer, Long> getMuteExpires() {
        // TODO 未実装
        return null;
    }

    /**
     * 非表示プレイヤーの一覧を返す
     * @return チャンネルの非表示プレイヤーの一覧
     */
    public List<ChannelPlayer> getHided() {
        // TODO 未実装
        return null;
    }

    /**
     * チャンネル名を返す
     * @return チャンネル名
     */
    public String getName() {
        return channel.getName();
    }

    /**
     * チャンネルのカラーコードを返す
     * @return チャンネルのカラーコード
     */
    public String getColorCode() {
        return channel.getColorCode();
    }

    /**
     * チャンネルのカラーコードを設定する
     * @param colorCode カラーコード
     */
    public void setColorCode(String colorCode) {
        channel.setColorCode(colorCode);
    }

    /**
     * ブロードキャストチャンネルを設定する
     * @param broadcast ブロードキャストチャンネルにするかどうか
     */
    public void setBroadcast(boolean broadcast) {
        channel.setBroadcast(broadcast);
    }

    /**
     * チャットを同ワールド内に制限するかどうかを設定する
     * @param isWorldRange 同ワールド制限するかどうか
     */
    public void setWorldRange(boolean isWorldRange) {
        channel.setWorldRange(isWorldRange);
    }

    /**
     * チャットの可聴範囲を設定する
     * @param range 可聴範囲
     */
    public void setChatRange(int range) {
        channel.setChatRange(range);
    }

    /**
     * 1:1チャットのときに、会話の相手先を取得する
     * @return 会話の相手のプレイヤー名
     */
    public String getPrivateMessageTo() {
        if ( channel.getPrivateMessageTo() != null ) return channel.getPrivateMessageTo().getName();
        return null;
    }

    /**
     * 1:1チャットのときに、会話の相手先を設定する
     * @param name 会話の相手のプレイヤー名
     */
    public void setPrivateMessageTo(String name) {
        channel.setPrivateMessageTo(ChannelMember.getChannelMember(name));
    }

    /**
     * ワールドチャットかどうか
     * @return ワールドチャットかどうか
     */
    public boolean isWorldRange() {
        return channel.isWorldRange();
    }

    /**
     * チャットの可聴範囲、0の場合は無制限
     * @return チャットの可聴範囲
     */
    public int getChatRange() {
        return channel.getChatRange();
    }

    /**
     * カラーコードが使用可能な設定かどうか
     * @return allowccを返す
     */
    public boolean isAllowCC() {
        return channel.isAllowCC();
    }

    /**
     * カラーコードの使用可否を設定する
     * @param allowcc 使用可否
     */
    public void setAllowCC(boolean allowcc) {
        channel.setAllowCC(allowcc);
    }

    /**
     * Japanize変換設定を取得する
     * @return japanize
     */
    public JapanizeType getJapanizeType() {
        String value = (channel.getJapanizeType() != null) ? channel.getJapanizeType().name() : "";
        return JapanizeType.fromID(value, null);
    }

    /**
     * Japanize変換設定を再設定する
     * @param japanize japanize
     */
    public void setJapanizeType(JapanizeType japanize) {
        // do nothing.
    }

    /**
     * チャンネルの情報をファイルに保存する。
     * @return 保存をしたかどうか。
     */
    public boolean save() {
        return channel.save();
    }
}
