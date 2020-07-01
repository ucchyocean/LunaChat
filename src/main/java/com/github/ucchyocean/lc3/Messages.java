/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.github.ucchyocean.lc3.util.KeywordReplacer;
import com.github.ucchyocean.lc3.util.Utility;
import com.github.ucchyocean.lc3.util.YamlConfig;

/**
 * プラグインのメッセージリソース管理クラス
 * @author ucchy
 */
public class Messages {

    private static YamlConfig resources;
    private static File _messageFolder;
    private static File _jar;

    /**
     * Jarファイル内から直接 messages_en.yml をdefaultMessagesとしてロードし、
     * langに対応するメッセージをファイルからロードする。
     * @param messagesFolder メッセージ格納フォルダ
     * @param jar jarファイル（JTestからの実行時はnullを指定可）
     * @param lang デフォルト言語
     */
    public static void initialize(File messagesFolder, File jar, String lang) {

        _jar = jar;
        _messageFolder = messagesFolder;
        if (!_messageFolder.exists()) {
            _messageFolder.mkdirs();
        }

        // コンフィグフォルダにメッセージファイルがまだ無いなら、コピーしておく
        for (String filename : new String[] {
                "messages_en.yml", "messages_ja.yml" }) {
            File file = new File(_messageFolder, filename);
            if (!file.exists()) {
                Utility.copyFileFromJar(_jar, file, filename, true);
            }
        }

        // デフォルトメッセージを、jarファイル内からロードする
        YamlConfig defaultMessages = null;
        if ( _jar != null ) {
            try (JarFile jarFile = new JarFile(_jar)) {

                ZipEntry zipEntry = jarFile.getEntry(String.format("messages_%s.yml", lang));
                if (zipEntry == null) {
                    zipEntry = jarFile.getEntry("messages_en.yml");
                }

                defaultMessages = YamlConfig.load(jarFile.getInputStream(zipEntry));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 対応する言語のメッセージをロードする
        File file = new File(_messageFolder, String.format("messages_%s.yml", lang));
        if (!file.exists()) {
            file = new File(_messageFolder, "messages_en.yml");
        }

        resources = YamlConfig.load(file);
        resources.addDefaults(defaultMessages);
    }

    /**
     * 指定された言語でリロードを行う。
     * @param lang 言語
     */
    public static void reload(String lang) {
        initialize(_jar, _messageFolder, lang);
    }

    // ここから下は自動生成メソッドです。変更をしないでください。

    // === Auto-generated methods area start. ===

    /**
     * &f[%color%%channel%&f]&7%player% さんがチャンネルに参加しました。
     */
    public static String joinMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("joinMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんがチャンネルから退出しました。
     */
    public static String quitMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("quitMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7チャンネルが削除されました。
     */
    public static String breakupMessage(Object color, Object channel) {
        String msg = resources.getString("breakupMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんをチャンネルからBANしました。
     */
    public static String banMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("banMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんをチャンネルからキックしました。
     */
    public static String kickMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("kickMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんをチャンネルからMuteしました。
     */
    public static String muteMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("muteMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7NGワード発言により、%player% さんをチャンネルから自動BANしました。
     */
    public static String banNGWordMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("banNGWordMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7NGワード発言により、%player% さんをチャンネルから自動キックしました。
     */
    public static String kickNGWordMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("kickNGWordMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7NGワード発言により、%player% さんをチャンネルから自動Muteしました。
     */
    public static String muteNGWordMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("muteNGWordMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんを期限 %minutes% 分でチャンネルからBANしました。
     */
    public static String banWithExpireMessage(Object color, Object channel, Object player, Object minutes) {
        String msg = resources.getString("banWithExpireMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        kr.replace("%minutes%", minutes.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんを期限 %minutes% 分でチャンネルからMuteしました。
     */
    public static String muteWithExpireMessage(Object color, Object channel, Object player, Object minutes) {
        String msg = resources.getString("muteWithExpireMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        kr.replace("%minutes%", minutes.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんのBANが解除されました。
     */
    public static String pardonMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("pardonMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんのMuteが解除されました。
     */
    public static String unmuteMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("unmuteMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんの期限付きBANが解除されました。
     */
    public static String expiredBanMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("expiredBanMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんの期限付きMuteが解除されました。
     */
    public static String expiredMuteMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("expiredMuteMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんがチャンネルのモデレーターになりました。
     */
    public static String addModeratorMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("addModeratorMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7%player% さんがチャンネルのモデレーターから外れました。
     */
    public static String removeModeratorMessage(Object color, Object channel, Object player) {
        String msg = resources.getString("removeModeratorMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[%color%%channel%&f]&7あなたの発言は、誰にも届きませんでした。
     */
    public static String noRecipientMessage(Object color, Object channel) {
        String msg = resources.getString("noRecipientMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%color%", color.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7---------- &bチャンネルリスト &7----------
     */
    public static String listFirstLine() {
        String msg = resources.getString("listFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7---------- &bチャンネルリスト&7(&c%page%&7/&c%max%&7) ----------
     */
    public static String listFirstLinePaging(Object page, Object max) {
        String msg = resources.getString("listFirstLinePaging");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%page%", page.toString());
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----------------------------------
     */
    public static String listEndLine() {
        String msg = resources.getString("listEndLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &f%channel%&7(&c%online%&7/&c%total%&7) &a%topic%
     */
    public static String listFormat(Object channel, Object online, Object total, Object topic) {
        String msg = resources.getString("listFormat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        kr.replace("%online%", online.toString());
        kr.replace("%total%", total.toString());
        kr.replace("%topic%", topic.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7|
     */
    public static String listPlainPrefix() {
        String msg = resources.getString("listPlainPrefix");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7---------- &bチャンネル情報 &7----------
     */
    public static String channelInfoFirstLine() {
        String msg = resources.getString("channelInfoFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7|
     */
    public static String channelInfoPrefix() {
        String msg = resources.getString("channelInfoPrefix");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cチャンネル別名：&f
     */
    public static String channelInfoAlias() {
        String msg = resources.getString("channelInfoAlias");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cグローバルチャンネル
     */
    public static String channelInfoGlobal() {
        String msg = resources.getString("channelInfoGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cブロードキャストチャンネル
     */
    public static String channelInfoBroadcast() {
        String msg = resources.getString("channelInfoBroadcast");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cシークレットチャンネル
     */
    public static String channelInfoSecret() {
        String msg = resources.getString("channelInfoSecret");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cパスワード設定あり
     */
    public static String channelInfoPassword() {
        String msg = resources.getString("channelInfoPassword");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cワールドチャット
     */
    public static String channelInfoWorldChat() {
        String msg = resources.getString("channelInfoWorldChat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &c範囲チャット：%block% ブロック
     */
    public static String channelInfoRangeChat(Object block) {
        String msg = resources.getString("channelInfoRangeChat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%block%", block.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cフォーマット設定：
     */
    public static String channelInfoFormat() {
        String msg = resources.getString("channelInfoFormat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cBANリスト：
     */
    public static String channelInfoBanned() {
        String msg = resources.getString("channelInfoBanned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &cMuteリスト：
     */
    public static String channelInfoMuted() {
        String msg = resources.getString("channelInfoMuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----- &b参加中のチャット &7-----
     */
    public static String motdFirstLine() {
        String msg = resources.getString("motdFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----- &b非表示にしているチャット &7-----
     */
    public static String hideChannelFirstLine() {
        String msg = resources.getString("hideChannelFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----- &b非表示にしているプレイヤー &7-----
     */
    public static String hidePlayerFirstLine() {
        String msg = resources.getString("hidePlayerFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----- &b%channel%の発言ログ &7-----
     */
    public static String logDisplayFirstLine(Object channel) {
        String msg = resources.getString("logDisplayFirstLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7----------------------------------
     */
    public static String logDisplayEndLine() {
        String msg = resources.getString("logDisplayEndLine");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &7| &c%date%&7, &f%player%&7: &f%message%
     */
    public static String logDisplayFormat(Object date, Object player, Object message) {
        String msg = resources.getString("logDisplayFormat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%date%", date.toString());
        kr.replace("%player%", player.toString());
        kr.replace("%message%", message.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[&aLC&f]
     */
    public static String infoPrefix() {
        String msg = resources.getString("infoPrefix");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &f[&cLC&f]
     */
    public static String errorPrefix() {
        String msg = resources.getString("errorPrefix");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * チャンネル %channel% に参加しました。
     */
    public static String cmdmsgJoin(Object channel) {
        String msg = resources.getString("cmdmsgJoin");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * デフォルトの発言先を %channel% に設定しました。
     */
    public static String cmdmsgSet(Object channel) {
        String msg = resources.getString("cmdmsgSet");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * トピック: &a%topic%
     */
    public static String cmdmsgSetTopic(Object topic) {
        String msg = resources.getString("cmdmsgSetTopic");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%topic%", topic.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * <注意> 現在このチャンネルを非表示に設定しています。
     */
    public static String cmdmsgSetHide() {
        String msg = resources.getString("cmdmsgSetHide");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% から退出しました。
     */
    public static String cmdmsgLeave(Object channel) {
        String msg = resources.getString("cmdmsgLeave");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% に招待しました。
     */
    public static String cmdmsgInvite(Object player, Object channel) {
        String msg = resources.getString("cmdmsgInvite");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんから、チャンネル %channel% に招待されました。
     */
    public static String cmdmsgInvited1(Object player, Object channel) {
        String msg = resources.getString("cmdmsgInvited1");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * 入室するには /ch accept、拒否するには /ch deny を実行してください。
     */
    public static String cmdmsgInvited2() {
        String msg = resources.getString("cmdmsgInvited2");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * 招待を拒否しました。
     */
    public static String cmdmsgDeny() {
        String msg = resources.getString("cmdmsgDeny");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * 招待が拒否されました。
     */
    public static String cmdmsgDenyed() {
        String msg = resources.getString("cmdmsgDenyed");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% からキックしました。
     */
    public static String cmdmsgKick(Object player, Object channel) {
        String msg = resources.getString("cmdmsgKick");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% からキックされました。
     */
    public static String cmdmsgKicked(Object channel) {
        String msg = resources.getString("cmdmsgKicked");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% からBANしました。
     */
    public static String cmdmsgBan(Object player, Object channel) {
        String msg = resources.getString("cmdmsgBan");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% から期限 %minutes% 分でBANしました。
     */
    public static String cmdmsgBanWithExpire(Object player, Object channel, Object minutes) {
        String msg = resources.getString("cmdmsgBanWithExpire");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%minutes%", minutes.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% からBANされました。
     */
    public static String cmdmsgBanned(Object channel) {
        String msg = resources.getString("cmdmsgBanned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんの、チャンネル %channel% のBANを解除しました。
     */
    public static String cmdmsgPardon(Object player, Object channel) {
        String msg = resources.getString("cmdmsgPardon");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% のBANが解除されました。
     */
    public static String cmdmsgPardoned(Object channel) {
        String msg = resources.getString("cmdmsgPardoned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% でMuteしました。
     */
    public static String cmdmsgMute(Object player, Object channel) {
        String msg = resources.getString("cmdmsgMute");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんを、チャンネル %channel% から期限 %minutes% 分でMuteしました。
     */
    public static String cmdmsgMuteWithExpire(Object player, Object channel, Object minutes) {
        String msg = resources.getString("cmdmsgMuteWithExpire");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        kr.replace("%minutes%", minutes.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% からMuteされました。
     */
    public static String cmdmsgMuted(Object channel) {
        String msg = resources.getString("cmdmsgMuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんの、チャンネル %channel% のMuteを解除しました。
     */
    public static String cmdmsgUnmute(Object player, Object channel) {
        String msg = resources.getString("cmdmsgUnmute");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% のMuteが解除されました。
     */
    public static String cmdmsgUnmuted(Object channel) {
        String msg = resources.getString("cmdmsgUnmuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% を非表示に設定しました。
     */
    public static String cmdmsgHided(Object channel) {
        String msg = resources.getString("cmdmsgHided");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * プレイヤー %player% を非表示に設定しました。
     */
    public static String cmdmsgHidedPlayer(Object player) {
        String msg = resources.getString("cmdmsgHidedPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% を表示に設定しました。
     */
    public static String cmdmsgUnhided(Object channel) {
        String msg = resources.getString("cmdmsgUnhided");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * プレイヤー %channel% を表示に設定しました。
     */
    public static String cmdmsgUnhidedPlayer(Object channel) {
        String msg = resources.getString("cmdmsgUnhidedPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * LunaChatの設定を再読み込みしました。
     */
    public static String cmdmsgReload() {
        String msg = resources.getString("cmdmsgReload");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% を新規作成しました。
     */
    public static String cmdmsgCreate(Object channel) {
        String msg = resources.getString("cmdmsgCreate");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% を削除しました。
     */
    public static String cmdmsgRemove(Object channel) {
        String msg = resources.getString("cmdmsgRemove");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * メッセージフォーマットを %format% に設定しました。
     */
    public static String cmdmsgFormat(Object format) {
        String msg = resources.getString("cmdmsgFormat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%format%", format.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんをチャンネル %channel% のモデレーターに設定しました。
     */
    public static String cmdmsgModerator(Object player, Object channel) {
        String msg = resources.getString("cmdmsgModerator");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんをチャンネル %channel% のモデレーターから外しました。
     */
    public static String cmdmsgModeratorMinus(Object player, Object channel) {
        String msg = resources.getString("cmdmsgModeratorMinus");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %key% を %value% と覚えました。
     */
    public static String cmdmsgDictionaryAdd(Object key, Object value) {
        String msg = resources.getString("cmdmsgDictionaryAdd");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %key% を忘れました。
     */
    public static String cmdmsgDictionaryRemove(Object key) {
        String msg = resources.getString("cmdmsgDictionaryRemove");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %key% を %value% に設定しました。
     */
    public static String cmdmsgOption(Object key, Object value) {
        String msg = resources.getString("cmdmsgOption");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * テンプレート %index% を、%value% に設定しました。
     */
    public static String cmdmsgTemplate(Object index, Object value) {
        String msg = resources.getString("cmdmsgTemplate");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%index%", index.toString());
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * テンプレート %index% を削除しました。
     */
    public static String cmdmsgTemplateRemove(Object index) {
        String msg = resources.getString("cmdmsgTemplateRemove");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%index%", index.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんの発言先を %channel% に設定しました。
     */
    public static String cmdmsgSetDefault(Object player, Object channel) {
        String msg = resources.getString("cmdmsgSetDefault");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * Your chat's Japanize conversion was turned %value%.
     */
    public static String cmdmsgPlayerJapanize(Object value) {
        String msg = resources.getString("cmdmsgPlayerJapanize");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %player% さんのJapanize変換を %value% にしました。
     */
    public static String cmdmsgPlayerJapanizeOther(Object player, Object value) {
        String msg = resources.getString("cmdmsgPlayerJapanizeOther");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %inviter%の現在の会話相手 : %invited%
     */
    public static String cmdmsgReplyInviter(Object inviter, Object invited) {
        String msg = resources.getString("cmdmsgReplyInviter");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%inviter%", inviter.toString());
        kr.replace("%invited%", invited.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * %inviter%の現在の会話相手 : 相手がいません。
     */
    public static String cmdmsgReplyInviterNone(Object inviter) {
        String msg = resources.getString("cmdmsgReplyInviterNone");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%inviter%", inviter.toString());
        return Utility.replaceColorCode(resources.getString("infoPrefix", "") + kr.toString());
    }

    /**
     * このコマンドはゲーム内からしか実行できません。
     */
    public static String errmsgIngame() {
        String msg = resources.getString("errmsgIngame");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * コマンドの指定が正しくありません。
     */
    public static String errmsgCommand() {
        String msg = resources.getString("errmsgCommand");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネルが存在しません。
     */
    public static String errmsgNotExist() {
        String msg = resources.getString("errmsgNotExist");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネルもプレイヤーも存在しません。
     */
    public static String errmsgNotExistChannelAndPlayer() {
        String msg = resources.getString("errmsgNotExistChannelAndPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネルが存在しないか、チャンネルが指定されませんでした。
     */
    public static String errmsgNotExistOrNotSpecified() {
        String msg = resources.getString("errmsgNotExistOrNotSpecified");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネル名が既に存在します。
     */
    public static String errmsgExist() {
        String msg = resources.getString("errmsgExist");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネルに参加していません。
     */
    public static String errmsgNomember() {
        String msg = resources.getString("errmsgNomember");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤーはチャンネルに参加していません。
     */
    public static String errmsgNomemberOther() {
        String msg = resources.getString("errmsgNomemberOther");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤー %player% が見つかりません。
     */
    public static String errmsgNotfoundPlayer(Object player) {
        String msg = resources.getString("errmsgNotfoundPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 招待を受けたプレイヤーではありません。
     */
    public static String errmsgNotInvited() {
        String msg = resources.getString("errmsgNotInvited");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネルが無くなってしまったため、参加できませんでした。
     */
    public static String errmsgNotfoundChannel() {
        String msg = resources.getString("errmsgNotfoundChannel");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 招待された %player% さんは、既にチャンネルに参加しています。
     */
    public static String errmsgInvitedAlreadyExist(Object player) {
        String msg = resources.getString("errmsgInvitedAlreadyExist");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 既にチャンネルに参加しています。
     */
    public static String errmsgInvitedAlreadyJoin() {
        String msg = resources.getString("errmsgInvitedAlreadyJoin");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 現在チャンネルに参加していません。
     */
    public static String errmsgNoJoin() {
        String msg = resources.getString("errmsgNoJoin");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * あなたはこのチャンネルからBANされています。
     */
    public static String errmsgBanned() {
        String msg = resources.getString("errmsgBanned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * あなたはこのチャンネルからMuteされているため、発言できません。
     */
    public static String errmsgMuted() {
        String msg = resources.getString("errmsgMuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤーは既にBANリストに含まれています。
     */
    public static String errmsgAlreadyBanned() {
        String msg = resources.getString("errmsgAlreadyBanned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤーは既にMuteリストに含まれています。
     */
    public static String errmsgAlreadyMuted() {
        String msg = resources.getString("errmsgAlreadyMuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このチャンネルは既に非表示になっています。
     */
    public static String errmsgAlreadyHided() {
        String msg = resources.getString("errmsgAlreadyHided");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このプレイヤーは既に非表示になっています。
     */
    public static String errmsgAlreadyHidedPlayer() {
        String msg = resources.getString("errmsgAlreadyHidedPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このチャンネルは非表示になっていません。
     */
    public static String errmsgAlreadyUnhided() {
        String msg = resources.getString("errmsgAlreadyUnhided");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このプレイヤーは非表示になっていません。
     */
    public static String errmsgAlreadyUnhidedPlayer() {
        String msg = resources.getString("errmsgAlreadyUnhidedPlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 個人チャットチャンネルには参加できません。
     */
    public static String errmsgCannotJoinPersonal() {
        String msg = resources.getString("errmsgCannotJoinPersonal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * あなたはモデレーターではないため、そのコマンドを実行できません。
     */
    public static String errmsgNotModerator() {
        String msg = resources.getString("errmsgNotModerator");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤーはBANリストに含まれていません。
     */
    public static String errmsgNotBanned() {
        String msg = resources.getString("errmsgNotBanned");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたプレイヤーはMuteリストに含まれていません。
     */
    public static String errmsgNotMuted() {
        String msg = resources.getString("errmsgNotMuted");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 有効なオプション指定が1つもありませんでした。
     */
    public static String errmsgInvalidOptions() {
        String msg = resources.getString("errmsgInvalidOptions");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このチャンネルはパスワードが設定されているため入れません。
     */
    public static String errmsgPassword1() {
        String msg = resources.getString("errmsgPassword1");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * パスワードを指定して、チャンネルに入ってください。
     */
    public static String errmsgPassword2() {
        String msg = resources.getString("errmsgPassword2");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * /ch (channel) (password)
     */
    public static String errmsgPassword3() {
        String msg = resources.getString("errmsgPassword3");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * パスワードが正しくないため、チャンネルに入れません。
     */
    public static String errmsgPasswordNotmatch() {
        String msg = resources.getString("errmsgPasswordNotmatch");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 権限 "%permission%" が無いため、実行できません。
     */
    public static String errmsgPermission(Object permission) {
        String msg = resources.getString("errmsgPermission");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%permission%", permission.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% はグローバルチャンネルなので、退出できません。
     */
    public static String errmsgCannotLeaveGlobal(Object channel) {
        String msg = resources.getString("errmsgCannotLeaveGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% はグローバルチャンネルなので、キックできません。
     */
    public static String errmsgCannotKickGlobal(Object channel) {
        String msg = resources.getString("errmsgCannotKickGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% はグローバルチャンネルなので、BANできません。
     */
    public static String errmsgCannotBANGlobal(Object channel) {
        String msg = resources.getString("errmsgCannotBANGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% はグローバルチャンネルなので、削除できません。
     */
    public static String errmsgCannotRemoveGlobal(Object channel) {
        String msg = resources.getString("errmsgCannotRemoveGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% はグローバルチャンネルなので、モデレーターを設定できません。
     */
    public static String errmsgCannotModeratorGlobal(Object channel) {
        String msg = resources.getString("errmsgCannotModeratorGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル %channel% は強制参加チャンネルなので、退出できません。
     */
    public static String errmsgCannotLeaveForceJoin(Object channel) {
        String msg = resources.getString("errmsgCannotLeaveForceJoin");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * あなたが受信したプライベートメッセージがありません。
     */
    public static String errmsgNotfoundPM() {
        String msg = resources.getString("errmsgNotfoundPM");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 自分自身にプライベートメッセージを送ることはできません。
     */
    public static String errmsgCannotSendPMSelf() {
        String msg = resources.getString("errmsgCannotSendPMSelf");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %channel% はチャンネル名に使用できない文字を含んでいます。
     */
    public static String errmsgCannotUseForChannel(Object channel) {
        String msg = resources.getString("errmsgCannotUseForChannel");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %channel% は短すぎてチャンネル名に使用できません。%min% 文字以上にしてください。
     */
    public static String errmsgCannotUseForChannelTooShort(Object channel, Object min) {
        String msg = resources.getString("errmsgCannotUseForChannelTooShort");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        kr.replace("%min%", min.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %channel% は長すぎてチャンネル名に使用できません。%max% 文字以下にしてください。
     */
    public static String errmsgCannotUseForChannelTooLong(Object channel, Object max) {
        String msg = resources.getString("errmsgCannotUseForChannelTooLong");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %word% はグローバルチャンネル名に使用できない文字を含んでいます。
     */
    public static String errmsgCannotUseForGlobal(Object word) {
        String msg = resources.getString("errmsgCannotUseForGlobal");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%word%", word.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %value% はカラーコードとして正しくありません。
     */
    public static String errmsgInvalidColorCode(Object value) {
        String msg = resources.getString("errmsgInvalidColorCode");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * テンプレート番号は、0から9までの数字を指定してください。
     */
    public static String errmsgInvalidTemplateNumber() {
        String msg = resources.getString("errmsgInvalidTemplateNumber");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 説明文は %max% 文字以下にしてください。
     */
    public static String errmsgToolongDescription(Object max) {
        String msg = resources.getString("errmsgToolongDescription");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * チャンネル別名は %max% 文字以下にしてください。
     */
    public static String errmsgToolongAlias(Object max) {
        String msg = resources.getString("errmsgToolongAlias");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * パスワードは %max% 文字以下にしてください。
     */
    public static String errmsgToolongPassword(Object max) {
        String msg = resources.getString("errmsgToolongPassword");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %key% は true/false で指定してください。
     */
    public static String errmsgInvalidBooleanOption(Object key) {
        String msg = resources.getString("errmsgInvalidBooleanOption");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * range に正しくない値が指定されました。
     */
    public static String errmsgInvalidRangeOption() {
        String msg = resources.getString("errmsgInvalidRangeOption");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * %key% に指定された %value% は、Japanize変換タイプとして正しくありません。
     */
    public static String errmsgInvalidJapanizeOption(Object key, Object value) {
        String msg = resources.getString("errmsgInvalidJapanizeOption");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        kr.replace("%value%", value.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このチャンネルはグローバルチャンネルのため、ブロードキャストをオフにできません。
     */
    public static String errmsgCannotOffGlobalBroadcast() {
        String msg = resources.getString("errmsgCannotOffGlobalBroadcast");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 必須キーワード %key% が指定されていません。
     */
    public static String errmsgFormatConstraint(Object key) {
        String msg = resources.getString("errmsgFormatConstraint");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%key%", key.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * BAN期限(分)の指定が正しくありません。1 から 43200 の間の数値を指定してください。
     */
    public static String errmsgInvalidBanExpireParameter() {
        String msg = resources.getString("errmsgInvalidBanExpireParameter");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * Mute期限(分)の指定が正しくありません。1 から 43200 の間の数値を指定してください。
     */
    public static String errmsgInvalidMuteExpireParameter() {
        String msg = resources.getString("errmsgInvalidMuteExpireParameter");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 自分の発言を非表示にすることはできません。
     */
    public static String errmsgCannotHideSelf() {
        String msg = resources.getString("errmsgCannotHideSelf");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 指定されたチャンネル別名 %aliase% は、チャンネル %channel% と重複するので設定できません。
     */
    public static String errmsgDuplicatedAlias(Object aliase, Object channel) {
        String msg = resources.getString("errmsgDuplicatedAlias");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%aliase%", aliase.toString());
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * 権限がありません&7(%permission%)
     */
    public static String errmsgNotPermission(Object permission) {
        String msg = resources.getString("errmsgNotPermission");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%permission%", permission.toString());
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * このサーバーでは、チャンネルチャットは動作しません。
     */
    public static String errmsgChannelChatDisabled() {
        String msg = resources.getString("errmsgChannelChatDisabled");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(resources.getString("errorPrefix", "") + kr.toString());
    }

    /**
     * &6/%label% join (channel) &7- チャンネルに参加します。
     */
    public static String usageJoin(Object label) {
        String msg = resources.getString("usageJoin");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% leave &7- 参加しているチャンネルから退出します。
     */
    public static String usageLeave(Object label) {
        String msg = resources.getString("usageLeave");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% list &7- チャンネルのリストを表示します。
     */
    public static String usageList(Object label) {
        String msg = resources.getString("usageList");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% invite (name) &7- 指定したプレイヤーをチャンネルチャットに招待します。
     */
    public static String usageInvite(Object label) {
        String msg = resources.getString("usageInvite");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% accept &7- 招待を受けてチャンネルチャットに入室します。
     */
    public static String usageAccept(Object label) {
        String msg = resources.getString("usageAccept");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% deny &7- 招待を拒否します。
     */
    public static String usageDeny(Object label) {
        String msg = resources.getString("usageDeny");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% kick (name) &7- 指定したプレイヤーをチャンネルチャットからキックします。
     */
    public static String usageKick(Object label) {
        String msg = resources.getString("usageKick");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% ban (name) &7- 指定したプレイヤーをチャンネルチャットからBANします。
     */
    public static String usageBan(Object label) {
        String msg = resources.getString("usageBan");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% ban (name) [minutes] &7- 指定したプレイヤーを指定した分の間、BANします。
     */
    public static String usageBan2(Object label) {
        String msg = resources.getString("usageBan2");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% pardon (name) &7- 指定したプレイヤーのBANを解除します。
     */
    public static String usagePardon(Object label) {
        String msg = resources.getString("usagePardon");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% mute (name) &7- 指定したプレイヤーのチャンネルでの発言権を剥奪します。
     */
    public static String usageMute(Object label) {
        String msg = resources.getString("usageMute");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% mute (name) [minutes] &7- 指定したプレイヤーを指定した分の間、発言権剥奪します。
     */
    public static String usageMute2(Object label) {
        String msg = resources.getString("usageMute2");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% unmute (name) &7- 指定したプレイヤーのチャンネルでの発言権剥奪を解除します。
     */
    public static String usageUnmute(Object label) {
        String msg = resources.getString("usageUnmute");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% hide [channel] &7- 指定したチャンネルの発言内容を非表示にします。
     */
    public static String usageHide(Object label) {
        String msg = resources.getString("usageHide");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% hide (player) &7- 指定したプレイヤーの発言内容を非表示にします。
     */
    public static String usageHidePlayer(Object label) {
        String msg = resources.getString("usageHidePlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% unhide [channel] &7- 指定したチャンネルの発言内容を非表示から表示に戻します。
     */
    public static String usageUnhide(Object label) {
        String msg = resources.getString("usageUnhide");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% unhide (player) &7- 指定したプレイヤーの発言内容を非表示から表示に戻します。
     */
    public static String usageUnhidePlayer(Object label) {
        String msg = resources.getString("usageUnhidePlayer");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% info [channel] &7- チャンネルの情報を表示します。
     */
    public static String usageInfo(Object label) {
        String msg = resources.getString("usageInfo");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% log [channel] [p=player] [f=filter] [d=date] [r] &7- チャンネルの発言ログを表示します。
     */
    public static String usageLog(Object label) {
        String msg = resources.getString("usageLog");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% create (channel) [description] &7- チャンネルを作成します。
     */
    public static String usageCreate(Object label) {
        String msg = resources.getString("usageCreate");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% remove [channel] &7- チャンネルを削除します。
     */
    public static String usageRemove(Object label) {
        String msg = resources.getString("usageRemove");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% format [channel] (format...) &7- チャンネルのメッセージフォーマットを設定します。
     */
    public static String usageFormat(Object label) {
        String msg = resources.getString("usageFormat");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% moderator [channel] (player) &7- チャンネルのモデレーターを指定したプレイヤーに設定します。
     */
    public static String usageModerator(Object label) {
        String msg = resources.getString("usageModerator");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% mod [channel] (player) &7- チャンネルのモデレーターを指定したプレイヤーに設定します。
     */
    public static String usageMod(Object label) {
        String msg = resources.getString("usageMod");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% dictionary (add (word) (value)|remove (word)) &7- Japanize変換辞書に新しいワードを登録したり、指定したワードを削除したりします。
     */
    public static String usageDictionary(Object label) {
        String msg = resources.getString("usageDictionary");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% dic (add (word) (value)|remove (word)) &7- Japanize変換辞書に新しいワードを登録したり、指定したワードを削除したりします。
     */
    public static String usageDic(Object label) {
        String msg = resources.getString("usageDic");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% option [channel] (key=value...) &7- チャンネルのオプションを設定します。
     */
    public static String usageOption(Object label) {
        String msg = resources.getString("usageOption");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% template (number) (template...) &7- メッセージフォーマットのテンプレートを登録します。
     */
    public static String usageTemplate(Object label) {
        String msg = resources.getString("usageTemplate");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% check &7- モデレーターがいないチャンネルを一覧します。
     */
    public static String usageCheck1(Object label) {
        String msg = resources.getString("usageCheck1");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% check remove &7- /ch check で一覧されたチャンネルを全て削除します。
     */
    public static String usageCheck2(Object label) {
        String msg = resources.getString("usageCheck2");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% reload &7- config.ymlの再読み込みをします。
     */
    public static String usageReload(Object label) {
        String msg = resources.getString("usageReload");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% help [user|mod|admin] [page] &7- ヘルプを表示します。
     */
    public static String usageHelp(Object label) {
        String msg = resources.getString("usageHelp");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% set default (player) [channel] &7- 指定したプレイヤーの発言先チャンネルを、指定したチャンネルに設定します。
     */
    public static String usageSet1(Object label) {
        String msg = resources.getString("usageSet1");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% (name) [message] &7- 指定したプレイヤーとの個人チャットを開始します。
     */
    public static String usageMessage(Object label) {
        String msg = resources.getString("usageMessage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% [message] &7- 受信した個人チャットに返信します。
     */
    public static String usageReply(Object label) {
        String msg = resources.getString("usageReply");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% on|off &7- Turn on/off the Japanize conversion of your chat.
     */
    public static String usageJapanize(Object label) {
        String msg = resources.getString("usageJapanize");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6/%label% (player) on|off &7- Turn on/off the Japanize conversion of other player's chat.
     */
    public static String usageJapanizeOther(Object label) {
        String msg = resources.getString("usageJapanizeOther");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &e----- &6LunaChat %type% command (&c%num%&6/&c%max%&6) &e-----
     */
    public static String usageTop(Object type, Object num, Object max) {
        String msg = resources.getString("usageTop");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%type%", type.toString());
        kr.replace("%num%", num.toString());
        kr.replace("%max%", max.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &e-----------------------------------------
     */
    public static String usageFoot() {
        String msg = resources.getString("usageFoot");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * &6次のページを見るには、&c/%label% help %type% %next%&6 と実行してください。
     */
    public static String usageNoticeNextPage(Object label, Object type, Object next) {
        String msg = resources.getString("usageNoticeNextPage");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%label%", label.toString());
        kr.replace("%type%", type.toString());
        kr.replace("%next%", next.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * 発言先を%channel%にする
     */
    public static String hoverChannelName(Object channel) {
        String msg = resources.getString("hoverChannelName");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%channel%", channel.toString());
        return Utility.replaceColorCode(kr.toString());
    }

    /**
     * %player%にプライベートメッセージを送る
     */
    public static String hoverPlayerName(Object player) {
        String msg = resources.getString("hoverPlayerName");
        if ( msg == null ) return "";
        KeywordReplacer kr = new KeywordReplacer(msg);
        kr.replace("%player%", player.toString());
        return Utility.replaceColorCode(kr.toString());
    }
    // === Auto-generated methods area end. ===
}
