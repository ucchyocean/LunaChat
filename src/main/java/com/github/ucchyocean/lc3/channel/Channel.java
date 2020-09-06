/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.channel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.LunaChatAPI;
import com.github.ucchyocean.lc3.LunaChatBukkit;
import com.github.ucchyocean.lc3.LunaChatConfig;
import com.github.ucchyocean.lc3.LunaChatLogger;
import com.github.ucchyocean.lc3.LunaChatMode;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.NGWordAction;
import com.github.ucchyocean.lc3.bridge.DynmapBridge;
import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;
import com.github.ucchyocean.lc3.member.ChannelMemberOther;
import com.github.ucchyocean.lc3.util.ChatColor;
import com.github.ucchyocean.lc3.util.ClickableFormat;
import com.github.ucchyocean.lc3.util.Utility;
import com.github.ucchyocean.lc3.util.YamlConfig;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * チャンネル
 * @author ucchy
 */
public abstract class Channel {

    private static final String PERMISSION_SPEAK_PREFIX = "lunachat.speak";

    private static final String FOLDER_NAME_CHANNELS = "channels";

    private static final String KEY_NAME = "name";
    private static final String KEY_ALIAS = "alias";
    private static final String KEY_DESC = "desc";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_MEMBERS = "members";
    private static final String KEY_BANNED = "banned";
    private static final String KEY_MUTED = "muted";
    private static final String KEY_HIDED = "hided";
    private static final String KEY_MODERATOR = "moderator";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_VISIBLE = "visible";
    private static final String KEY_COLOR = "color";
    private static final String KEY_BROADCAST = "broadcast";
    private static final String KEY_WORLD = "world";
    private static final String KEY_RANGE = "range";
    private static final String KEY_BAN_EXPIRES = "ban_expires";
    private static final String KEY_MUTE_EXPIRES = "mute_expires";
    private static final String KEY_ALLOWCC = "allowcc";
    private static final String KEY_JAPANIZE = "japanize";

    /** 参加者 */
    private List<ChannelMember> members;

    /** チャンネルモデレータ */
    private List<ChannelMember> moderator;

    /** BANされたプレイヤー */
    private List<ChannelMember> banned;

    /** Muteされたプレイヤー */
    private List<ChannelMember> muted;

    /** Hideしているプレイヤー */
    private List<ChannelMember> hided;

    /** チャンネルの名称 */
    private String name;

    /** チャンネルの別名 */
    private String alias;

    /** チャンネルの説明文 */
    private String description;

    /** チャンネルのパスワード */
    private String password;

    /** チャンネルリストに表示されるかどうか */
    private boolean visible;

    /** チャンネルのカラー */
    private String colorCode;

    /** メッセージフォーマット<br>
     * 指定可能なキーワードは下記のとおり<br>
     * %ch - チャンネル名<br>
     * %username - ユーザー名<br>
     * %msg - メッセージ<br>
     * %prefix - PermissionsExに設定するprefix<br>
     * %suffix - PermissionsExに設定するsuffix<br>
     * %color - チャンネルのカラーコード
     * */
    private String format;

    /** ブロードキャストチャンネルかどうか */
    private boolean broadcastChannel;

    /** ワールドチャットかどうか */
    private boolean isWorldRange;

    /** チャットの可聴範囲 0は無制限 */
    private int chatRange;

    /** 期限付きBANの期限（key=プレイヤー名、value=期日（ミリ秒）） */
    private Map<ChannelMember, Long> banExpires;

    /** 期限付きMuteの期限（key=プレイヤー名、value=期日（ミリ秒）） */
    private Map<ChannelMember, Long> muteExpires;

    /** 1:1チャットの相手 */
    private ChannelMember privateMessageTo;

    /** カラーコードの使用可否 */
    private boolean allowcc;

    /** チャンネルごとのjapanize変換設定 */
    private JapanizeType japanizeType;


    protected LunaChatLogger logger;

    /**
     * コンストラクタ
     * @param name チャンネルの名称
     */
    protected Channel(String name) {

        this.name = name;
        this.alias = "";
        this.description = "";
        this.members = new ArrayList<ChannelMember>();
        this.banned = new ArrayList<ChannelMember>();
        this.muted = new ArrayList<ChannelMember>();
        this.hided = new ArrayList<ChannelMember>();
        this.moderator = new ArrayList<ChannelMember>();
        this.password = "";
        this.visible = true;
        this.colorCode = "";
        this.broadcastChannel = false;
        this.isWorldRange = false;
        this.chatRange = 0;
        this.banExpires = new HashMap<ChannelMember, Long>();
        this.muteExpires = new HashMap<ChannelMember, Long>();
        this.privateMessageTo = null;
        this.allowcc = true;

        LunaChatConfig config = LunaChat.getConfig();
        if ( isPersonalChat() ) {
            this.format = config.getDefaultFormatForPrivateMessage();
        } else {
            this.format = config.getDefaultFormat();
        }
        this.japanizeType = config.getJapanizeType();

        logger = new LunaChatLogger(name.replace(">", "-").replace("*", "_"));
    }

    /**
     * 1:1チャットかどうか
     * @return 1:1チャットかどうか
     */
    public boolean isPersonalChat() {
        return name.contains(">");
    }

    /**
     * ブロードキャストチャンネルかどうか
     * @return ブロードキャストチャンネルかどうか
     */
    public boolean isBroadcastChannel() {
        return (isGlobalChannel() || broadcastChannel);
    }

    /**
     * グローバルチャンネルかどうか
     * @return グローバルチャンネルかどうか
     */
    public boolean isGlobalChannel() {
        LunaChatConfig config = LunaChat.getConfig();
        return getName().equals(config.getGlobalChannel());
    }

    /**
     * 強制参加チャンネルかどうか
     * @return 強制参加チャンネルかどうか
     */
    public boolean isForceJoinChannel() {
        LunaChatConfig config = LunaChat.getConfig();
        return config.getForceJoinChannels().contains(getName());
    }

    /**
     * このチャンネルのモデレータ権限を持っているかどうかを確認する
     * @param player 権限を確認する対象
     * @return チャンネルのモデレータ権限を持っているかどうか
     */
    public boolean hasModeratorPermission(ChannelMember player) {
        if ( player == null ) return false;
        return player.hasPermission("lunachat-admin.mod-all-channels") || moderator.contains(player);
    }

    /**
     * このチャットに発言をする
     * @param player 発言をするプレイヤー
     * @param message 発言をするメッセージ
     */
    public void chat(ChannelMember player, String message) {

        // 発言権限を確認する
        String node = PERMISSION_SPEAK_PREFIX + "." + getName();
        if ( player.isPermissionSet(node) && !player.hasPermission(node) ) {
            player.sendMessage(Messages.errmsgPermission(node));
            return;
        }

        LunaChatConfig config = LunaChat.getConfig();
        LunaChatAPI api = LunaChat.getAPI();

        // Muteされているかどうかを確認する
        if ( getMuted().contains(player) ) {
            player.sendMessage(Messages.errmsgMuted());
            return;
        }

        String maskedMessage = new String(message);

        // 一時的にJapanizeスキップ設定かどうかを確認する
        boolean skipJapanize = false;
        String marker = config.getNoneJapanizeMarker();
        if ( !marker.equals("") && maskedMessage.startsWith(marker) ) {
            skipJapanize = true;
            maskedMessage = maskedMessage.substring(marker.length());
        }

        // NGワード発言をしたかどうかのチェックとマスク
        boolean isNG = false;
        for ( Pattern pattern : config.getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(maskedMessage);
            if ( matcher.find() ) {
                maskedMessage = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
                isNG = true;
            }
        }

        // キーワード置き換え
        ClickableFormat cf = ClickableFormat.makeFormat(getFormat(), player, this, true);

        // カラーコード置き換え
        // チャンネルで許可されていて、発言者がパーミッションを持っている場合に置き換える
        if ( isAllowCC() && player.hasPermission("lunachat.allowcc") ) {
            maskedMessage = Utility.replaceColorCode(maskedMessage);
        } else {
            maskedMessage = Utility.stripColorCode(maskedMessage);
        }

        // LunaChatChannelChatEvent イベントコール
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelChatEvent(
                getName(), player, message, maskedMessage, cf.toLegacyText());
        if ( result.isCancelled() ) {
            return;
        }
//        msgFormat = result.getMessageFormat();
        maskedMessage = result.getNgMaskedMessage();

        // 2byteコードを含むか、半角カタカナのみなら、Japanize変換は行わない
        String kanaTemp = Utility.stripColorCode(maskedMessage);

        if ( !skipJapanize &&
                ( kanaTemp.getBytes(StandardCharsets.UTF_8).length > kanaTemp.length() ||
                        kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
            skipJapanize = true;
        }

        // Japanize変換タスクを作成する
        boolean isIncludeSyncChat = true;
        ChannelChatJapanizeTask delayedTask = null;
        JapanizeType japanizeType = (getJapanizeType() == null)
                ? config.getJapanizeType() : getJapanizeType();

        if ( !skipJapanize &&
                api.isPlayerJapanize(player.getName()) &&
                japanizeType != JapanizeType.NONE ) {

            int lineType = config.getJapanizeDisplayLine();
            String jpFormat;
            ClickableFormat messageFormat = null;
            if ( lineType == 1 ) {
                jpFormat = Utility.replaceColorCode(config.getJapanizeLine1Format());
                messageFormat = cf;
                isIncludeSyncChat = false;
            } else {
                jpFormat = Utility.replaceColorCode(config.getJapanizeLine2Format());
            }

            // タスクを作成しておく
            delayedTask = new ChannelChatJapanizeTask(maskedMessage,
                    japanizeType, this, player, jpFormat, messageFormat);
        }

        if ( isIncludeSyncChat ) {
            // メッセージの送信
            sendMessage(player, maskedMessage, cf, true);
        }

        // 非同期実行タスクがある場合、追加で実行する
        if ( delayedTask != null ) {
            LunaChat.runAsyncTask(delayedTask);
        }

        // NGワード発言者に、NGワードアクションを実行する
        if ( isNG ) {
            if ( config.getNgwordAction() == NGWordAction.BAN ) {
                // BANする

                if ( !isGlobalChannel() ) {
                    getBanned().add(player);
                    removeMember(player);
                    if ( Messages.banNGWordMessage("", "", "").length > 0 ) {
                        BaseComponent[] m = Messages.banNGWordMessage(getColorCode(), getName(), player.getName());
                        player.sendMessage(m);
                        sendSystemMessage(m, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.KICK ) {
                // キックする

                if ( !isGlobalChannel() ) {
                    removeMember(player);
                    if ( Messages.kickNGWordMessage("", "", "").length > 0 ) {
                        BaseComponent[] m = Messages.kickNGWordMessage(getColorCode(), getName(), player.getName());
                        player.sendMessage(m);
                        sendSystemMessage(m, true, "system");
                    }
                }

            } else if ( config.getNgwordAction() == NGWordAction.MUTE ) {
                // Muteする

                getMuted().add(player);
                save();
                if ( Messages.muteNGWordMessage("", "", "").length > 0 ) {
                    BaseComponent[] m = Messages.muteNGWordMessage(getColorCode(), getName(), player.getName());
                    player.sendMessage(m);
                    sendSystemMessage(m, true, "system");
                }
            }
        }
    }

    /**
     * ほかの連携先などから、このチャットに発言する
     * @param player プレイヤー名
     * @param source 連携元を判別する文字列
     * @param message メッセージ
     */
    public void chatFromOtherSource(String player, @Nullable String source, String message) {

        LunaChatConfig config = LunaChat.getConfig();

        // 表示名
        String name;
        if ( source != null && !source.isEmpty() ) {
            name = player + "@" + source;
        } else {
            name = player;
        }

        // NGワード発言のマスク
        String maskedMessage = new String(message);
        for ( Pattern pattern : config.getNgwordCompiled() ) {
            Matcher matcher = pattern.matcher(maskedMessage);
            if ( matcher.find() ) {
                maskedMessage = matcher.replaceAll(
                        Utility.getAstariskString(matcher.group(0).length()));
            }
        }

        // キーワード置き換え
        ClickableFormat msgFormat = ClickableFormat.makeFormat(getFormat(), new ChannelMemberOther(name), this, false);

        // カラーコード置き換え チャンネルで許可されている場合に置き換える。
        if ( isAllowCC() ) {
            maskedMessage = Utility.replaceColorCode(maskedMessage);
        }

        // メッセージの送信
        boolean sendDynmap = source == null || !source.equals("web");
        sendMessage(new ChannelMemberOther(name), maskedMessage, msgFormat, sendDynmap);
    }

    /**
     * チャンネルにシステムメッセージを送信する。
     * @param message メッセージ
     * @param sendDynmap Dynmapにも表示するかどうか
     * @param name 発言者の表示名
     */
    public void sendSystemMessage(String message, boolean sendDynmap, String name) {
        sendSystemMessage(TextComponent.fromLegacyText(message), sendDynmap, name);
    }

    /**
     * チャンネルにシステムメッセージを送信する。
     * @param message メッセージ
     * @param sendDynmap Dynmapにも表示するかどうか
     * @param name 発言者の表示名
     */
    public void sendSystemMessage(BaseComponent[] message, boolean sendDynmap, String name) {

        LunaChatConfig config = LunaChat.getConfig();

        // 受信者（＝メンバー全員からhideしているプレイヤーを除く）
        List<ChannelMember> recipients = new ArrayList<>(getMembers());
        for ( ChannelMember cp : getHided() ) {
            if ( recipients.contains(cp) ) {
                recipients.remove(cp);
            }
        }

        // opListenAllChannel 設定がある場合は、
        // パーミッション lunachat-admin.listen-all-channels を持つプレイヤーを
        // 受信者に加える。
        if ( config.isOpListenAllChannel() ) {
            for ( String playerName : LunaChat.getPlugin().getOnlinePlayerNames() ) {
                ChannelMember cp = ChannelMember.getChannelMember(playerName);
                if ( cp != null
                        && cp.hasPermission("lunachat-admin.listen-all-channels")
                        && !recipients.contains(cp) ) {
                    recipients.add(cp);
                }
            }
        }

        // 通常ブロードキャストなら、設定に応じてdynmapへ送信する
        DynmapBridge dynmap = LunaChatBukkit.getInstance().getDynmap();
        if ( config.isSendBroadcastChannelChatToDynmap() &&
                sendDynmap &&
                dynmap != null &&
                isBroadcastChannel() &&
                !isWorldRange() ) {

            dynmap.broadcast(makeLegacyText(message));
        }

        // 送信する
        for ( ChannelMember p : recipients ) {
            p.sendMessage(message);
        }

        // 設定に応じて、コンソールに出力する
        if ( config.isDisplayChatOnConsole() ) {
            LunaChat.getPlugin().log(Level.INFO, makePlainText(message));
        }

        // ロギング
        log(makePlainText(message), name);
    }

    /**
     * メンバーを追加する
     * @param player 追加するプレイヤー
     */
    public void addMember(ChannelMember player) {

        // 既に参加しているなら、何もしない
        if ( members.contains(player) ) {
            return;
        }

        // 変更後のメンバーリストを作成
        ArrayList<ChannelMember> after = new ArrayList<ChannelMember>(members);
        after.add(player);

        // LunaChatChannelMemberChangedEvent イベントコール
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelMemberChangedEvent(name, members, after);
        if ( result.isCancelled() ) {
            return;
        }

        // メンバー更新
        if ( members.size() == 0 && moderator.size() == 0 ) {
            moderator.add(player);
        }
        members = after;

        if ( !isPersonalChat() ) {
            player.sendMessage(Messages.joinMessage(getColorCode(), getName(), player.getName()));
        }

        save();
    }

    /**
     * メンバーを削除する
     * @param player 削除するプレイヤー
     */
    public void removeMember(ChannelMember player) {

        // 既に削除しているなら、何もしない
        if ( !members.contains(player) ) {
            return;
        }

        // 変更後のメンバーリストを作成
        ArrayList<ChannelMember> after = new ArrayList<ChannelMember>(members);
        after.remove(player);

        // LunaChatChannelMemberChangedEvent イベントコール
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelMemberChangedEvent(name, members, after);
        if ( result.isCancelled() ) {
            return;
        }

        // デフォルト発言先が退出するチャンネルと一致する場合、
        // デフォルト発言先を削除する
        LunaChatAPI api = LunaChat.getAPI();
        Channel def = api.getDefaultChannel(player.getName());
        if ( def != null && def.getName().equals(getName()) ) {
            api.removeDefaultChannel(player.getName());
        }

        // 実際にメンバーから削除する
        members.remove(player);

        if ( !isPersonalChat() ) {
            player.sendMessage(Messages.quitMessage(getColorCode(), getName(), player.getName()));
        }

        // 0人で削除する設定がオンで、0人になったなら、チャンネルを削除する
        LunaChatConfig config = LunaChat.getConfig();
        if ( config.isZeroMemberRemove() && members.size() <= 0 ) {
            api.removeChannel(this.name);
            return;
        }

        // 非表示設定プレイヤーだったら、リストから削除する
        if ( hided.contains(player) ) {
            hided.remove(player);
        }

        // モデレーターだった場合は、モデレーターから除去する
        if ( moderator.contains(player) ) {
            moderator.remove(player);
        }

        save();
    }

    /**
     * モデレータを追加する
     * @param player 追加するプレイヤー
     */
    public void addModerator(ChannelMember player) {

        // 既にモデレータなら何もしない
        if ( moderator.contains(player) ) {
            return;
        }

        // モデレータへ追加
        moderator.add(player);

        // メッセージ
        if ( !isPersonalChat() ) {
            player.sendMessage(Messages.addModeratorMessage(getColorCode(), getName(), player.getName()));
        }

        save();
    }

    /**
     * モデレータを削除する
     * @param player 削除するプレイヤー
     */
    public void removeModerator(ChannelMember player) {

        // 既にモデレータでないなら何もしない
        if ( !moderator.contains(player) ) {
            return;
        }

        // モデレータから削除
        moderator.remove(player);

        // メッセージ
        if ( !isPersonalChat() ) {
            player.sendMessage(Messages.removeModeratorMessage(getColorCode(), getName(), player.getName()));
        }

        save();
    }

    /**
     * メッセージを表示します。指定したプレイヤーの発言として処理されます。
     * @param member 発言者（ワールドチャット、範囲チャットの場合は必須です）
     * @param message メッセージ
     * @param format フォーマット
     * @param sendDynmap dynmapへ送信するかどうか
     */
    protected abstract void sendMessage(
            ChannelMember member, String message, @Nullable ClickableFormat format, boolean sendDynmap);

    /**
     * ログを記録する
     * @param name 発言者
     * @param message 記録するメッセージ
     */
    protected abstract void log(String message, String name);

    /**
     * チャットフォーマット内のキーワードを置き換えする
     * @param format チャットフォーマット
     * @param member プレイヤー
     * @return 置き換え結果
     */
    protected ClickableFormat replaceKeywords(String format, ChannelMember member) {
        return ClickableFormat.makeFormat(format, member, this, true);
    }

    /**
     * チャンネル情報を返す
     * @param forModerator モデレータ向けの情報を含めるかどうか
     * @return チャンネル情報
     */
    public List<String> getInfo(boolean forModerator) {

        ArrayList<String> info = new ArrayList<String>();
        info.add(Messages.channelInfoFirstLine());

        // チャンネル名、参加人数、総人数、チャンネル説明文
        info.add(makeLegacyText(Messages.listFormat(getName(), getOnlineNum(), getTotalNum(), getDescription())));

        // チャンネル別名
        String alias = getAlias();
        if ( alias != null && alias.length() > 0 ) {
            info.add(Messages.channelInfoAlias() + alias);
        }

        // 参加メンバー一覧
        if ( isGlobalChannel() ) {
            info.add(Messages.channelInfoGlobal());
        } else if ( isBroadcastChannel() ) {
            info.add(Messages.channelInfoBroadcast());
        } else {
            // メンバーを、5人ごとに表示する
            StringBuffer buf = new StringBuffer();
            buf.append(Messages.channelInfoPrefix());

            for ( int i=0; i<getMembers().size(); i++ ) {

                if ( i%5 == 0 && i != 0 ) {
                    info.add(buf.toString());
                    buf = new StringBuffer();
                    buf.append(Messages.channelInfoPrefix());
                }

                ChannelMember cp = getMembers().get(i);
                String name = cp.getName();
                String disp;
                if ( getModerator().contains(cp) ) {
                    name = "@" + name;
                }
                if ( cp.isOnline() ) {
                    if ( getHided().contains(cp) )
                        disp = ChatColor.DARK_AQUA + name;
                    else
                        disp = ChatColor.WHITE + name;
                } else {
                    disp = ChatColor.GRAY + name;
                }
                buf.append(disp + ",");
            }

            info.add(buf.toString());
        }

        // シークレットチャンネルかどうか
        if ( !isVisible() ) {
            info.add(Messages.channelInfoSecret());
        }

        // パスワード設定があるかどうか
        if ( getPassword().length() > 0 ) {
            if ( !forModerator ) {
                info.add(Messages.channelInfoPassword());
            } else {
                info.add(Messages.channelInfoPassword() + " " + getPassword());
            }
        }

        // 範囲チャット、ワールドチャット
        if ( isWorldRange() && getChatRange() > 0 ) {
            info.add(Messages.channelInfoRangeChat(getChatRange()));
        } else if ( isWorldRange() ) {
            info.add(Messages.channelInfoWorldChat());
        }

        if ( forModerator ) {

            // フォーマット情報
            info.add(Messages.channelInfoFormat());
            info.add(Messages.channelInfoPrefix() + " " + ChatColor.WHITE + getFormat());

            // Muteリスト情報、5人ごとに表示する
            if ( getMuted().size() > 0 ) {
                info.add(Messages.channelInfoMuted());

                StringBuffer buf = new StringBuffer();
                buf.append(Messages.channelInfoPrefix() + ChatColor.WHITE);
                for ( int i=0; i<getMuted().size(); i++ ) {
                    if ( i%5 == 0 && i != 0 ) {
                        info.add(buf.toString());
                        buf = new StringBuffer();
                        buf.append(Messages.channelInfoPrefix() + ChatColor.WHITE);
                    }
                    buf.append(getMuted().get(i).getName() + ",");
                }

                info.add(buf.toString());
            }

            // BANリスト情報、5人ごとに表示する
            if ( getBanned().size() > 0 ) {
                info.add(Messages.channelInfoBanned());

                StringBuffer buf = new StringBuffer();
                buf.append(Messages.channelInfoPrefix() + ChatColor.WHITE);
                for ( int i=0; i<getBanned().size(); i++ ) {
                    if ( i%5 == 0 && i != 0 ) {
                        info.add(buf.toString());
                        buf = new StringBuffer();
                        buf.append(Messages.channelInfoPrefix() + ChatColor.WHITE);
                    }
                    buf.append(getBanned().get(i).getName() + ",");
                }

                info.add(buf.toString());
            }
        }

        info.add(Messages.listEndLine());

        return info;
    }

    /**
     * 期限付きBanや期限付きMuteをチェックし、期限が切れていたら解除を行う
     */
    public void checkExpires() {

        long now = System.currentTimeMillis();

        // 期限付きBANのチェック
        for ( ChannelMember cp : getBanExpires().keySet() ) {
            if ( getBanExpires().get(cp) <= now ) {

                // 期限マップから削除し、BANを解除
                getBanExpires().remove(cp);
                if ( getBanned().contains(cp) ) {
                    getBanned().remove(cp);
                    save();

                    // メッセージ通知を流す
                    BaseComponent[] msg = Messages.expiredBanMessage(getColorCode(), getName(), cp.getName());
                    if ( msg.length > 0 ) {
                        sendSystemMessage(msg, true, "system");
                    }

                    String pardonedMsg = Messages.cmdmsgPardoned(getName());
                    if ( cp.isOnline() && !pardonedMsg.isEmpty() ) {
                        cp.sendMessage(pardonedMsg);
                    }
                }
            }
        }

        // 期限付きMuteのチェック
        for ( ChannelMember cp : getMuteExpires().keySet() ) {
            if ( getMuteExpires().get(cp) <= now ) {

                // 期限マップから削除し、Muteを解除
                getMuteExpires().remove(cp);
                if ( getMuted().contains(cp) ) {
                    getMuted().remove(cp);
                    save();

                    // メッセージ通知を流す
                    BaseComponent[] msg = Messages.expiredMuteMessage(getColorCode(), getName(), cp.getName());
                    if ( msg.length > 0 ) {
                        sendSystemMessage(msg, true, "system");
                    }

                    String unmutedMsg = Messages.cmdmsgUnmuted(getName());
                    if ( cp.isOnline() && !unmutedMsg.isEmpty() ) {
                        cp.sendMessage(unmutedMsg);
                    }
                }
            }
        }
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

        return logger.getLog(player, filter, date, reverse);
    }

    /**
     * チャンネルのオンライン人数を返す
     * @return オンライン人数
     */
    public int getOnlineNum() {

        // オンラインになっているメンバーの人数を数える
        int onlineNum = 0;
        for ( ChannelMember player : members ) {
            if ( player.isOnline() ) {
                onlineNum++;
            }
        }
        return onlineNum;
    }

    /**
     * チャンネルの総参加人数を返す
     * @return 総参加人数
     */
    public int getTotalNum() {
        return members.size();
    }

    /**
     * シリアライズ<br>
     * ConfigurationSerializable互換のための実装。
     * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
     */
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_NAME, name);
        map.put(KEY_ALIAS, alias);
        map.put(KEY_DESC, description);
        map.put(KEY_FORMAT, format);
        map.put(KEY_MEMBERS, getStringList(members));
        map.put(KEY_BANNED, getStringList(banned));
        map.put(KEY_MUTED, getStringList(muted));
        map.put(KEY_HIDED, getStringList(hided));
        map.put(KEY_MODERATOR, getStringList(moderator));
        map.put(KEY_PASSWORD, password);
        map.put(KEY_VISIBLE, visible);
        map.put(KEY_COLOR, colorCode);
        map.put(KEY_BROADCAST, broadcastChannel);
        map.put(KEY_WORLD, isWorldRange);
        map.put(KEY_RANGE, chatRange);
        map.put(KEY_BAN_EXPIRES, getStringLongMap(banExpires));
        map.put(KEY_MUTE_EXPIRES, getStringLongMap(muteExpires));
        map.put(KEY_ALLOWCC, allowcc);
        map.put(KEY_JAPANIZE, japanizeType == null ? null : japanizeType.toString());
        return map;
    }

    /**
     * デシリアライズ<br>
     * ConfigurationSerializable互換のための実装。
     * @param data デシリアライズ元のMapデータ。
     * @return デシリアライズされたクラス
     */
    public static Channel deserialize(Map<String, Object> data) {

        String name = castWithDefault(data.get(KEY_NAME), (String)null);
        if ( name == null ) {
            return null;
        }

        Channel channel = null;
        if ( LunaChat.getMode() == LunaChatMode.BUKKIT ) {
            channel = new BukkitChannel(name);
        } else if ( LunaChat.getMode() == LunaChatMode.BUNGEE ) {
            channel = new BungeeChannel(name);
        } else {
            channel = new StandaloneChannel(name);
        }

        channel.alias = castWithDefault(data.get(KEY_ALIAS), "");
        channel.description = castWithDefault(data.get(KEY_DESC), "");
        channel.format = castWithDefault(data.get(KEY_FORMAT), channel.format);
        channel.members = castToChannelMemberList(data.get(KEY_MEMBERS));
        channel.banned = castToChannelMemberList(data.get(KEY_BANNED));
        channel.muted = castToChannelMemberList(data.get(KEY_MUTED));
        channel.hided = castToChannelMemberList(data.get(KEY_HIDED));
        channel.moderator = castToChannelMemberList(data.get(KEY_MODERATOR));
        channel.password = castWithDefault(data.get(KEY_PASSWORD), "");
        channel.visible = castWithDefault(data.get(KEY_VISIBLE), true);
        channel.colorCode = castWithDefault(data.get(KEY_COLOR), "");
        channel.broadcastChannel = castWithDefault(data.get(KEY_BROADCAST), false);
        channel.isWorldRange = castWithDefault(data.get(KEY_WORLD), false);
        channel.chatRange = castWithDefault(data.get(KEY_RANGE), 0);
        channel.banExpires = castToChannelMemberLongMap(data.get(KEY_BAN_EXPIRES));
        channel.muteExpires = castToChannelMemberLongMap(data.get(KEY_MUTE_EXPIRES));
        channel.allowcc = castWithDefault(data.get(KEY_ALLOWCC), true);
        channel.japanizeType = JapanizeType.fromID(data.get(KEY_JAPANIZE) + "", null);
        return channel;
    }

    /**
     * チャンネルの別名を返す
     * @return チャンネルの別名
     */
    public String getAlias() {
        return alias;
    }

    /**
     * チャンネルの別名を設定する
     * @param alias チャンネルの別名
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * チャンネルの説明文を返す
     * @return チャンネルの説明文
     */
    public String getDescription() {
        return description;
    }

    /**
     * チャンネルの説明文を設定する
     * @param description チャンネルの説明文
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * チャンネルのパスワードを返す
     * @return チャンネルのパスワード
     */
    public String getPassword() {
        return password;
    }

    /**
     * チャンネルのパスワードを設定する
     * @param password チャンネルのパスワード
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * チャンネルの可視性を返す
     * @return チャンネルの可視性
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * チャンネルの可視性を設定する
     * @param visible チャンネルの可視性
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * チャンネルのメッセージフォーマットを返す
     * @return チャンネルのメッセージフォーマット
     */
    public String getFormat() {
        return format;
    }

    /**
     * チャンネルのメッセージフォーマットを設定する
     * @param format チャンネルのメッセージフォーマット
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * チャンネルのメンバーを返す
     * @return チャンネルのメンバー
     */
    public List<ChannelMember> getMembers() {
        return members;
    }

    /**
     * チャンネルのモデレーターを返す
     * @return チャンネルのモデレーター
     */
    public List<ChannelMember> getModerator() {
        return moderator;
    }

    /**
     * チャンネルのBANリストを返す
     * @return チャンネルのBANリスト
     */
    public List<ChannelMember> getBanned() {
        return banned;
    }

    /**
     * チャンネルのMuteリストを返す
     * @return チャンネルのMuteリスト
     */
    public List<ChannelMember> getMuted() {
        return muted;
    }

    /**
     * 期限付きBANの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return banExpires
     */
    public Map<ChannelMember, Long> getBanExpires() {
        return banExpires;
    }

    /**
     * 期限付きMuteの期限マップを返す（key=プレイヤー名、value=期日（ミリ秒））
     * @return muteExpires
     */
    public Map<ChannelMember, Long> getMuteExpires() {
        return muteExpires;
    }

    /**
     * 非表示プレイヤーの一覧を返す
     * @return チャンネルの非表示プレイヤーの一覧
     */
    public List<ChannelMember> getHided() {
        return hided;
    }

    /**
     * チャンネル名を返す
     * @return チャンネル名
     */
    public String getName() {
        return name;
    }

    /**
     * チャンネルのカラーコードを返す
     * @return チャンネルのカラーコード
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * チャンネルのカラーコードを設定する
     * @param colorCode カラーコード
     */
    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    /**
     * ブロードキャストチャンネルを設定する
     * @param broadcast ブロードキャストチャンネルにするかどうか
     */
    public void setBroadcast(boolean broadcast) {
        this.broadcastChannel = broadcast;
    }

    /**
     * チャットを同ワールド内に制限するかどうかを設定する
     * @param isWorldRange 同ワールド制限するかどうか
     */
    public void setWorldRange(boolean isWorldRange) {
        this.isWorldRange = isWorldRange;
    }

    /**
     * チャットの可聴範囲を設定する
     * @param range 可聴範囲
     */
    public void setChatRange(int range) {
        this.chatRange = range;
    }

    /**
     * 1:1チャットのときに、会話の相手先を取得する
     * @return 会話の相手のプレイヤー
     */
    public ChannelMember getPrivateMessageTo() {
        return privateMessageTo;
    }

    /**
     * 1:1チャットのときに、会話の相手先を設定する
     * @param to 会話の相手のプレイヤー名
     */
    public void setPrivateMessageTo(ChannelMember to) {
        this.privateMessageTo = to;
    }

    /**
     * ワールドチャットかどうか
     * @return ワールドチャットかどうか
     */
    public boolean isWorldRange() {
        return isWorldRange;
    }

    /**
     * チャットの可聴範囲、0の場合は無制限
     * @return チャットの可聴範囲
     */
    public int getChatRange() {
        return chatRange;
    }

    /**
     * カラーコードが使用可能な設定かどうか
     * @return allowccを返す
     */
    public boolean isAllowCC() {
        return allowcc;
    }

    /**
     * カラーコードの使用可否を設定する
     * @param allowcc 使用可否
     */
    public void setAllowCC(boolean allowcc) {
        this.allowcc = allowcc;
    }

    /**
     * Japanize変換設定を取得する
     * @return japanize
     */
    public JapanizeType getJapanizeType() {
        return japanizeType;
    }

    /**
     * Japanize変換設定を再設定する
     * @param japanize japanize
     */
    public void setJapanizeType(JapanizeType japanize) {
        this.japanizeType = japanize;
    }

    /**
     * チャンネルの情報をファイルに保存する。
     * @return 保存をしたかどうか。
     */
    public boolean save() {

        // フォルダーの取得と、必要に応じて作成
        File folder = new File(
                LunaChat.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            folder.mkdirs();
        }

        // 1:1チャットチャンネルの場合は、何もしない。
        if ( isPersonalChat() ) {
            return false;
        }

        File file = new File(folder, name + ".yml");

        // ファイルへ保存する
        YamlConfig conf = new YamlConfig();
        Map<String, Object> data = this.serialize();
        for ( String key : data.keySet() ) {
            conf.set(key, data.get(key));
        }
        try {
            conf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * チャンネルの情報を保存したファイルを、削除する。
     * @return 削除したかどうか。
     */
    protected boolean remove() {

        // フォルダーの取得
        File folder = new File(
                LunaChat.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            return false;
        }
        File file = new File(folder, name + ".yml");
        if ( !file.exists() ) {
            return false;
        }

        // ファイルを削除
        return file.delete();
    }

    /**
     * チャンネルの情報を保存したファイルから全てのチャンネルを復元して返す。
     * @return 全てのチャンネル
     */
    protected static HashMap<String, Channel> loadAllChannels() {

        // フォルダーの取得
        File folder = new File(
                LunaChat.getDataFolder(), FOLDER_NAME_CHANNELS);
        if ( !folder.exists() ) {
            return new HashMap<String, Channel>();
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml");
            }
        });
        if ( files == null ) files = new File[0];

        HashMap<String, Channel> result = new HashMap<String, Channel>();
        for ( File file : files ) {
            YamlConfig config = YamlConfig.load(file);
            Map<String, Object> data = new HashMap<String, Object>();
            for ( String key : config.getKeys(false) ) {
                data.put(key, config.get(key));
            }
            Channel channel = deserialize(data);
            result.put(channel.name.toLowerCase(), channel);
        }

        return result;
    }

    /**
     * List&lt;ChannelMember&gt;を、List&lt;String&gt;に変換する。
     * @param org 変換元
     * @return 変換後
     */
    private static List<String> getStringList(List<ChannelMember> org) {

        List<String> result = new ArrayList<String>();
        for ( ChannelMember cp : org ) {
            if ( cp != null ) result.add(cp.toString());
        }
        return result;
    }

    /**
     * Map&lt;ChannelMember, Long&gt;を、Map&lt;String, Long&gt;に変換する。
     * @param org 変換元
     * @return 変換後
     */
    private static Map<String, Long> getStringLongMap(Map<ChannelMember, Long> org) {

        HashMap<String, Long> result = new HashMap<String, Long>();
        for ( ChannelMember cp : org.keySet() ) {
            if ( cp != null ) result.put(cp.toString(), org.get(cp));
        }
        return result;
    }

    /**
     * Objectを、クラスTに変換する。nullならデフォルトを返す。
     * @param obj 変換元
     * @param def nullだった場合のデフォルト
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static <T> T castWithDefault(Object obj, T def) {

        if ( obj == null ) {
            return def;
        }
        return (T)obj;
    }

    /**
     * Objectを、List&lt;ChannelMember&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    private static List<ChannelMember> castToChannelMemberList(Object obj) {

        List<String> entries = castToStringList(obj);
        ArrayList<ChannelMember> players = new ArrayList<ChannelMember>();

        for ( String entry : entries ) {
            players.add(ChannelMember.getChannelMember(entry));
        }

        return players;
    }

    /**
     * Objectを、List&lt;String&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static List<String> castToStringList(Object obj) {

        if ( obj == null ) {
            return new ArrayList<String>();
        }
        if ( !(obj instanceof List<?>) ) {
            return new ArrayList<String>();
        }
        return (List<String>)obj;
    }

    /**
     * Objectを、Map&lt;ChannelMember, Long&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    private static Map<ChannelMember, Long> castToChannelMemberLongMap(Object obj) {

        Map<String, Long> entries = castToStringLongMap(obj);
        HashMap<ChannelMember, Long> map = new HashMap<ChannelMember, Long>();

        for ( String key : entries.keySet() ) {
            ChannelMember cp = ChannelMember.getChannelMember(key);
            map.put(cp, entries.get(key));
        }

        return map;
    }

    /**
     * Objectを、Map&lt;String, Long&gt;に変換する。nullなら空のリストを返す。
     * @param obj 変換元
     * @return 変換後
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Long> castToStringLongMap(Object obj) {

        if ( obj == null ) {
            return new HashMap<String, Long>();
        }
        if ( !(obj instanceof HashMap<?, ?>) ) {
            return new HashMap<String, Long>();
        }
        return (Map<String, Long>)obj;
    }

    private static String makeLegacyText(BaseComponent[] comps) {
        StringBuilder builder = new StringBuilder();
        for ( BaseComponent comp : comps ) {
            builder.append(comp.toLegacyText());
        }
        return builder.toString();
    }

    private static String makePlainText(BaseComponent[] comps) {
        StringBuilder builder = new StringBuilder();
        for ( BaseComponent comp : comps ) {
            builder.append(comp.toPlainText());
        }
        return builder.toString();
    }
}
