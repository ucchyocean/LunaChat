/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberBlock;
import com.github.ucchyocean.lc3.member.ChannelMemberBukkitConsole;
import com.github.ucchyocean.lc3.member.ChannelMemberBungee;
import com.github.ucchyocean.lc3.member.ChannelMemberPlayer;

/**
 * チャンネル
 * @author ucchy
 * @deprecated Legacy Version
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
     * @deprecated Legacy Version
     */
    public boolean isPersonalChat() {
        return channel.isPersonalChat();
    }

    /**
     * ブロードキャストチャンネルかどうか
     * @return ブロードキャストチャンネルかどうか
     * @deprecated Legacy Version
     */
    public boolean isBroadcastChannel() {
        return channel.isBroadcastChannel();
    }

    /**
     * グローバルチャンネルかどうか
     * @return グローバルチャンネルかどうか
     * @deprecated Legacy Version
     */
    public boolean isGlobalChannel() {
        return channel.isGlobalChannel();
    }

    /**
     * 強制参加チャンネルかどうか
     * @return 強制参加チャンネルかどうか
     * @deprecated Legacy Version
     */
    public boolean isForceJoinChannel() {
        return channel.isForceJoinChannel();
    }

    /**
     * このチャンネルのモデレータ権限を持っているかどうかを確認する
     * @param sender 権限を確認する対象
     * @return チャンネルのモデレータ権限を持っているかどうか
     * @deprecated Legacy Version
     */
    public boolean hasModeratorPermission(CommandSender sender) {
        return channel.hasModeratorPermission(ChannelMember.getChannelMember(sender));
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     * @deprecated Legacy Version
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
     * @deprecated Legacy Version
     */
    public void chatFromOtherSource(String player, String source, String message) {
        channel.chatFromOtherSource(player, source, message);
    }

    /**
     * メンバーを追加する
     * @param name 追加するプレイヤー
     * @deprecated Legacy Version
     */
    public void addMember(ChannelPlayer player) {
        if ( player != null ) {
            channel.addMember(ChannelMember.getChannelMember(player.toString()));
        }
    }

    /**
     * メンバーを削除する
     * @param name 削除するプレイヤー
     * @deprecated Legacy Version
     */
    public void removeMember(ChannelPlayer player) {
        if ( player != null ) {
            channel.removeMember(ChannelMember.getChannelMember(player.toString()));
        }
    }

    /**
     * モデレータを追加する
     * @param player 追加するプレイヤー
     * @deprecated Legacy Version
     */
    public void addModerator(ChannelPlayer player) {
        if ( player != null ) {
            channel.addModerator(ChannelMember.getChannelMember(player.toString()));
        }
    }

    /**
     * モデレータを削除する
     * @param player 削除するプレイヤー
     * @deprecated Legacy Version
     */
    public void removeModerator(ChannelPlayer player) {
        if ( player != null ) {
            channel.removeModerator(ChannelMember.getChannelMember(player.toString()));
        }
    }

    /**
     * プレイヤーに関連する、システムメッセージをチャンネルに流す
     * @param key リソースキー
     * @param player プレイヤー
     * @deprecated Legacy Version
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
     * @deprecated Legacy Version
     */
    public void sendMessage(
            ChannelPlayer player, String message, String format, boolean sendDynmap, String displayName) {
        if ( format != null ) {
            message = format.replace("%msg", message);
        }
        if ( player != null ) {
            channel.chat(ChannelMember.getChannelMember(player.toString()), message);
        } else {
            channel.chatFromOtherSource(displayName, null, message);
        }
    }

    /**
     * チャンネル情報を返す
     * @param forModerator モデレータ向けの情報を含めるかどうか
     * @return チャンネル情報
     * @deprecated Legacy Version
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
     * @deprecated Legacy Version
     */
    public ArrayList<String> getLog(
            String player, String filter, String date, boolean reverse) {
        return new ArrayList<String>(channel.getLog(player, filter, date, reverse));
    }

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     * @deprecated Legacy Version
     */
    public int getOnlineNum() {
        return channel.getOnlineNum();
    }

    /**
     * チャンネルの総参加人数を返す
     * @return 総参加人数
     * @deprecated Legacy Version
     */
    public int getTotalNum() {
        return channel.getTotalNum();
    }

    /**
     * 期限付きBanや期限付きMuteをチェックし、期限が切れていたら解除を行う
     * @deprecated Legacy Version
     */
    public void checkExpires() {
        channel.checkExpires();
    }

    /**
     * チャンネルの別名を返す
     * @return チャンネルの別名
     * @deprecated Legacy Version
     */
    public String getAlias() {
        return channel.getAlias();
    }

    /**
     * チャンネルの別名を設定する
     * @param alias チャンネルの別名
     * @deprecated Legacy Version
     */
    public void setAlias(String alias) {
        channel.setAlias(alias);
    }

    /**
     * チャンネルの説明文を返す
     * @return チャンネルの説明文
     * @deprecated Legacy Version
     */
    public String getDescription() {
        return channel.getDescription();
    }

    /**
     * チャンネルの説明文を設定する
     * @param description チャンネルの説明文
     * @deprecated Legacy Version
     */
    public void setDescription(String description) {
        channel.setDescription(description);
    }

    /**
     * チャンネルのパスワードを返す
     * @return チャンネルのパスワード
     * @deprecated Legacy Version
     */
    public String getPassword() {
        return channel.getPassword();
    }

    /**
     * チャンネルのパスワードを設定する
     * @param password チャンネルのパスワード
     * @deprecated Legacy Version
     */
    public void setPassword(String password) {
        channel.setPassword(password);
    }

    /**
     * チャンネルの可視性を返す
     * @return チャンネルの可視性
     * @deprecated Legacy Version
     */
    public boolean isVisible() {
        return channel.isVisible();
    }

    /**
     * チャンネルの可視性を設定する
     * @param visible チャンネルの可視性
     * @deprecated Legacy Version
     */
    public void setVisible(boolean visible) {
        channel.setVisible(visible);
    }

    /**
     * チャンネルのメッセージフォーマットを返す
     * @return チャンネルのメッセージフォーマット
     * @deprecated Legacy Version
     */
    public String getFormat() {
        return channel.getFormat();
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     * @param format チャンネルのメッセージフォーマット
     * @deprecated Legacy Version
     */
    public void setFormat(String format) {
        channel.setFormat(format);
    }

    /**
     * チャンネルのメンバーを返す
     * @return チャンネルのメンバー
     * @deprecated Legacy Version
     */
    public List<ChannelPlayer> getMembers() {
        return convertMemberListToPlayerList(channel.getMembers());
    }

    /**
     * チャンネルのモデレーターを返す
     * @return チャンネルのモデレーター
     * @deprecated Legacy Version
     */
    public List<ChannelPlayer> getModerator() {
        return convertMemberListToPlayerList(channel.getModerator());
    }

    /**
     * チャンネルのBANリストを返す
     * @return チャンネルのBANリスト
     * @deprecated Legacy Version
     */
    public List<ChannelPlayer> getBanned() {
        return convertMemberListToPlayerList(channel.getBanned());
    }

    /**
     * チャンネルのMuteリストを返す
     * @return チャンネルのMuteリスト
     * @deprecated Legacy Version
     */
    public List<ChannelPlayer> getMuted() {
        return convertMemberListToPlayerList(channel.getMuted());
    }

    /**
     * 期限付きBANの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return banExpires
     * @deprecated Legacy Version
     */
    public Map<ChannelPlayer, Long> getBanExpires() {
        return convertMemberListToPlayerList(channel.getBanExpires());
    }

    /**
     * 期限付きMuteの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return muteExpires
     * @deprecated Legacy Version
     */
    public Map<ChannelPlayer, Long> getMuteExpires() {
        return convertMemberListToPlayerList(channel.getMuteExpires());
    }

    /**
     * 非表示プレイヤーの一覧を返す
     * @return チャンネルの非表示プレイヤーの一覧
     * @deprecated Legacy Version
     */
    public List<ChannelPlayer> getHided() {
        return convertMemberListToPlayerList(channel.getHided());
    }

    /**
     * チャンネル名を返す
     * @return チャンネル名
     * @deprecated Legacy Version
     */
    public String getName() {
        return channel.getName();
    }

    /**
     * チャンネルのカラーコードを返す
     * @return チャンネルのカラーコード
     * @deprecated Legacy Version
     */
    public String getColorCode() {
        return channel.getColorCode();
    }

    /**
     * チャンネルのカラーコードを設定する
     * @param colorCode カラーコード
     * @deprecated Legacy Version
     */
    public void setColorCode(String colorCode) {
        channel.setColorCode(colorCode);
    }

    /**
     * ブロードキャストチャンネルを設定する
     * @param broadcast ブロードキャストチャンネルにするかどうか
     * @deprecated Legacy Version
     */
    public void setBroadcast(boolean broadcast) {
        channel.setBroadcast(broadcast);
    }

    /**
     * チャットを同ワールド内に制限するかどうかを設定する
     * @param isWorldRange 同ワールド制限するかどうか
     * @deprecated Legacy Version
     */
    public void setWorldRange(boolean isWorldRange) {
        channel.setWorldRange(isWorldRange);
    }

    /**
     * チャットの可聴範囲を設定する
     * @param range 可聴範囲
     * @deprecated Legacy Version
     */
    public void setChatRange(int range) {
        channel.setChatRange(range);
    }

    /**
     * 1:1チャットのときに、会話の相手先を取得する
     * @return 会話の相手のプレイヤー名
     * @deprecated Legacy Version
     */
    public String getPrivateMessageTo() {
        if ( channel.getPrivateMessageTo() != null ) return channel.getPrivateMessageTo().getName();
        return null;
    }

    /**
     * 1:1チャットのときに、会話の相手先を設定する
     * @param name 会話の相手のプレイヤー名
     * @deprecated Legacy Version
     */
    public void setPrivateMessageTo(String name) {
        channel.setPrivateMessageTo(ChannelMember.getChannelMember(name));
    }

    /**
     * ワールドチャットかどうか
     * @return ワールドチャットかどうか
     * @deprecated Legacy Version
     */
    public boolean isWorldRange() {
        return channel.isWorldRange();
    }

    /**
     * チャットの可聴範囲、0の場合は無制限
     * @return チャットの可聴範囲
     * @deprecated Legacy Version
     */
    public int getChatRange() {
        return channel.getChatRange();
    }

    /**
     * カラーコードが使用可能な設定かどうか
     * @return allowccを返す
     * @deprecated Legacy Version
     */
    public boolean isAllowCC() {
        return channel.isAllowCC();
    }

    /**
     * カラーコードの使用可否を設定する
     * @param allowcc 使用可否
     * @deprecated Legacy Version
     */
    public void setAllowCC(boolean allowcc) {
        channel.setAllowCC(allowcc);
    }

    /**
     * Japanize変換設定を取得する
     * @return japanize
     * @deprecated Legacy Version
     */
    public JapanizeType getJapanizeType() {
        String value = (channel.getJapanizeType() != null) ? channel.getJapanizeType().name() : "";
        return JapanizeType.fromID(value, null);
    }

    /**
     * Japanize変換設定を再設定する
     * @param japanize japanize
     * @deprecated Legacy Version
     */
    public void setJapanizeType(JapanizeType japanize) {
        channel.setJapanizeType(com.github.ucchyocean.lc3.japanize.JapanizeType.fromID(
                japanize.name(), null));
    }

    /**
     * チャンネルの情報をファイルに保存する。
     * @return 保存をしたかどうか。
     * @deprecated Legacy Version
     */
    public boolean save() {
        return channel.save();
    }

    /**
     * ChannelMemberをChannelPlayerに変換する
     * @param cp
     * @return
     */
    private ChannelPlayer convertChannelMemberToChannelPlayer(ChannelMember cm) {
        if ( cm == null ) return null;
        if ( cm instanceof ChannelMemberBungee ) return null; // Bungeeモードの場合は変換できない
        if ( cm instanceof ChannelMemberPlayer ) {
            return ChannelPlayer.getChannelPlayer(cm.toString());
        } else if ( cm instanceof ChannelMemberBukkitConsole ) {
            return new ChannelPlayerConsole(Bukkit.getConsoleSender());
        } else if ( cm instanceof ChannelMemberBlock ) {
            ChannelMemberBlock cmb = (ChannelMemberBlock)cm;
            if ( cmb.getBlockCommandSender() != null ) {
                return new ChannelPlayerBlock(cmb.getBlockCommandSender());
            }
        }
        return null;
    }

    /**
     * ChannelMemberのリストをChannelPlayerのリストに変換する
     * @param list
     * @return
     */
    private List<ChannelPlayer> convertMemberListToPlayerList(List<ChannelMember> list) {
        List<ChannelPlayer> result = new ArrayList<ChannelPlayer>();
        for ( ChannelMember member : list ) {
            ChannelPlayer player = convertChannelMemberToChannelPlayer(member);
            if ( player != null ) result.add(player);
        }
        return result;
    }

    /**
     * ChannelMemberのマップをChannelPlayerのリストに変換する
     * @param list
     * @return
     */
    private Map<ChannelPlayer, Long> convertMemberListToPlayerList(Map<ChannelMember, Long> map) {
        Map<ChannelPlayer, Long> result = new HashMap<ChannelPlayer, Long>();
        for ( ChannelMember member : map.keySet() ) {
            ChannelPlayer player = convertChannelMemberToChannelPlayer(member);
            if ( player != null ) result.put(player, map.get(member));
        }
        return result;
    }
}
