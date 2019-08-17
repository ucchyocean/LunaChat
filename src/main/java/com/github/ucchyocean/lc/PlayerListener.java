/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ucchyocean.lc.bridge.VaultChatBridge;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.channel.ChannelPlayerName;
import com.github.ucchyocean.lc.channel.ChannelPlayerUUID;
import com.github.ucchyocean.lc.channel.DelayedJapanizeNormalChatTask;
import com.github.ucchyocean.lc.event.LunaChatPreChatEvent;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * プレイヤーの行動を監視するリスナ
 * @author ucchy
 */
public class PlayerListener implements Listener {

    private static final String MOTD_FIRSTLINE = Resources.get("motdFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");
    private static final String PREERR = Resources.get("errorPrefix");

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /**
     * コンストラクタ
     */
    public PlayerListener() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * プレイヤーがチャット発言したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onAsyncPlayerChatLowest(AsyncPlayerChatEvent event) {
        if ( LunaChat.getInstance().getLunaChatConfig().getPlayerChatEventListenerPriority() == EventPriority.LOWEST ) {
            processChatEvent(event);
        }
    }

    /**
     * プレイヤーがチャット発言したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onAsyncPlayerChatLow(AsyncPlayerChatEvent event) {
        if ( LunaChat.getInstance().getLunaChatConfig().getPlayerChatEventListenerPriority() == EventPriority.LOW ) {
            processChatEvent(event);
        }
    }

    /**
     * プレイヤーがチャット発言したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onAsyncPlayerChatNormal(AsyncPlayerChatEvent event) {
        if ( LunaChat.getInstance().getLunaChatConfig().getPlayerChatEventListenerPriority() == EventPriority.NORMAL ) {
            processChatEvent(event);
        }
    }

    /**
     * プレイヤーがチャット発言したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onAsyncPlayerChatHigh(AsyncPlayerChatEvent event) {
        if ( LunaChat.getInstance().getLunaChatConfig().getPlayerChatEventListenerPriority() == EventPriority.HIGH ) {
            processChatEvent(event);
        }
    }

    /**
     * プレイヤーがチャット発言したときに呼び出されるメソッド
     * @param event
     */
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onAsyncPlayerChatHighest(AsyncPlayerChatEvent event) {
        if ( LunaChat.getInstance().getLunaChatConfig().getPlayerChatEventListenerPriority() == EventPriority.HIGHEST ) {
            processChatEvent(event);
        }
    }

    /**
     * プレイヤーのサーバー参加ごとに呼び出されるメソッド
     * @param event プレイヤー参加イベント
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        Player player = event.getPlayer();

        // 強制参加チャンネル設定を確認し、参加させる
        forceJoinToForceJoinChannels(player);

        // グローバルチャンネル設定がある場合
        if ( !config.getGlobalChannel().equals("") ) {
            tryJoinToGlobalChannel(player);
        }

        // チャンネルチャット情報を表示する
        if ( config.isShowListOnJoin() ) {
            ArrayList<String> list = getListForMotd(player);
            for ( String msg : list ) {
                player.sendMessage(msg);
            }
        }
    }

    /**
     * プレイヤーのサーバー退出ごとに呼び出されるメソッド
     * @param event プレイヤー退出イベント
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        // お互いがオフラインになるPMチャンネルがある場合は
        // チャンネルをクリアする
        ArrayList<Channel> deleteList = new ArrayList<Channel>();

        for ( Channel channel : LunaChat.getInstance().getLunaChatAPI().getChannels() ) {
            String cname = channel.getName();
            String pname = player.getName();
            if ( channel.isPersonalChat() && cname.contains(pname) ) {
                boolean isAllOffline = true;
                for ( ChannelPlayer cp : channel.getMembers() ) {
                    if ( !cp.equals(player) && cp.isOnline() &&
                            (cp instanceof ChannelPlayerName || cp instanceof ChannelPlayerUUID) ) {
                        isAllOffline = false;
                    }
                }
                if ( isAllOffline ) {
                    deleteList.add(channel);
                }
            }
        }

        for ( Channel channel : deleteList ) {
            LunaChat.getInstance().getLunaChatAPI().removeChannel(channel.getName());
        }
    }

    /**
     * プレイヤーのチャットごとに呼び出されるメソッド
     * @param event チャットイベント
     */
    private void processChatEvent(AsyncPlayerChatEvent event) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        // 頭にglobalMarkerが付いている場合は、グローバル発言にする
        if ( config.getGlobalMarker() != null &&
                !config.getGlobalMarker().equals("") &&
                event.getMessage().startsWith(config.getGlobalMarker()) &&
                event.getMessage().length() > config.getGlobalMarker().length() ) {

            int offset = config.getGlobalMarker().length();
            event.setMessage( event.getMessage().substring(offset) );
            chatGlobal(event);
            return;
        }

        // クイックチャンネルチャット機能が有効で、専用の記号が含まれるなら、
        // クイックチャンネルチャットとして処理する。
        if ( config.isEnableQuickChannelChat() ) {
            String separator = config.getQuickChannelChatSeparator();
            if ( event.getMessage().contains(separator) ) {
                String[] temp = event.getMessage().split(separator, 2);
                String name = temp[0];
                String value = "";
                if ( temp.length > 0 ) {
                    value = temp[1];
                }

                Channel channel = api.getChannel(name);
                if ( channel != null && !channel.isPersonalChat() ) {
                    ChannelPlayer player =
                            ChannelPlayer.getChannelPlayer(event.getPlayer());
                    if ( !channel.getMembers().contains(player) ) {
                        // 指定されたチャンネルに参加していないなら、エラーを表示して何も発言せずに終了する。
                        sendResourceMessage(event.getPlayer(), PREERR, "errmsgNomember");
                        event.setCancelled(true);
                        return;
                    }

                    // 指定されたチャンネルに発言して終了する。
                    chatToChannelWithEvent(player, channel, value);
                    event.setCancelled(true);
                    return;
                }
            }
        }

        ChannelPlayer player =
                ChannelPlayer.getChannelPlayer(event.getPlayer());
        Channel channel = api.getDefaultChannel(player.getName());

        // デフォルトの発言先が無い場合
        if ( channel == null ) {
            if ( config.isNoJoinAsGlobal() ) {
                // グローバル発言にする
                chatGlobal(event);
                return;

            } else {
                // 発言をキャンセルして終了する
                event.setCancelled(true);
                return;
            }
        }

        chatToChannelWithEvent(player, channel, event.getMessage());

        // もとのイベントをキャンセル
        event.setCancelled(true);
    }

    /**
     * イベントをグローバルチャット発言として処理する
     * @param event 処理するイベント
     */
    private void chatGlobal(AsyncPlayerChatEvent event) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        ChannelPlayer player =
                ChannelPlayer.getChannelPlayer(event.getPlayer());

        if ( !config.getGlobalChannel().equals("") ) {
            // グローバルチャンネル設定がある場合

            // グローバルチャンネルの取得、無ければ作成
            Channel global = api.getChannel(config.getGlobalChannel());
            if ( global == null ) {
                global = api.createChannel(config.getGlobalChannel());
            }

            // LunaChatPreChatEvent イベントコール
            LunaChatPreChatEvent preChatEvent = new LunaChatPreChatEvent(
                    global.getName(), player, event.getMessage());
            Utility.callEventSync(preChatEvent);
            if ( preChatEvent.isCancelled() ) {
                event.setCancelled(true);
                return;
            }
            Channel alt = preChatEvent.getChannel();
            if ( alt != null ) {
                global = alt;
            }
            String message = preChatEvent.getMessage();

            // デフォルト発言先が無いなら、グローバルチャンネルに設定する
            Channel dchannel = api.getDefaultChannel(player.getName());
            if ( dchannel == null ) {
                api.setDefaultChannel(player.getName(), global.getName());
            }

            // チャンネルチャット発言
            chatToChannelWithEvent(player, global, message);

            // もとのイベントをキャンセル
            event.setCancelled(true);

            return;

        } else {
            // グローバルチャンネル設定が無い場合

            String message = event.getMessage();
            // NGワード発言をマスク
            for ( Pattern pattern : config.getNgwordCompiled() ) {
                Matcher matcher = pattern.matcher(message);
                if ( matcher.find() ) {
                    message = matcher.replaceAll(
                            Utility.getAstariskString(matcher.group(0).length()));
                }
            }

            // チャットフォーマット装飾の適用
            if ( config.isEnableNormalChatMessageFormat() ) {
                String format = config.getNormalChatMessageFormat();
                format = replaceNormalChatFormatKeywords(format, event.getPlayer());
                event.setFormat(format);
            }

            // カラーコード置き換え
            // 置き換え設定になっていて、発言者がパーミッションを持っているなら、置き換えする
            if ( config.isEnableNormalChatColorCode() &&
                    event.getPlayer().hasPermission("lunachat.allowcc") ) {
                message = Utility.replaceColorCode(message);
            }

            // hideされているプレイヤーを、recipientから抜く
            for ( ChannelPlayer cp : api.getHidelist(player) ) {
                Player p = cp.getPlayer();
                if ( p != null ) {
                    event.getRecipients().remove(p);
                }
            }

            // 一時的にJapanizeスキップ設定かどうかを確認する
            boolean skipJapanize = false;
            String marker = config.getNoneJapanizeMarker();
            if ( !marker.equals("") && message.startsWith(marker) ) {
                skipJapanize = true;
                message = message.substring(marker.length());
            }

            // 2byteコードを含むなら、Japanize変換は行わない
            String kanaTemp = Utility.stripColor(message);
            if ( !skipJapanize &&
                    ( kanaTemp.getBytes().length > kanaTemp.length() ||
                            kanaTemp.matches("[ \\uFF61-\\uFF9F]+") ) ) {
                skipJapanize = true;
            }

            // Japanize変換と、発言処理
            if ( !skipJapanize &&
                    LunaChat.getInstance().getLunaChatAPI().isPlayerJapanize(player.getName()) &&
                    config.getJapanizeType() != JapanizeType.NONE ) {

                int lineType = config.getJapanizeDisplayLine();

                if ( lineType == 1 ) {

                    String taskFormat = Utility.replaceColorCode(config.getJapanizeLine1Format());

                    String japanized = api.japanize(
                            kanaTemp, config.getJapanizeType());
                    if ( japanized != null ) {
                        String temp = taskFormat.replace("%msg", message);
                        message = temp.replace("%japanize", japanized);
                    }

                } else {

                    String taskFormat = Utility.replaceColorCode(config.getJapanizeLine2Format());

                    DelayedJapanizeNormalChatTask task = new DelayedJapanizeNormalChatTask(
                            message, config.getJapanizeType(), player, taskFormat, event);

                    // 発言処理を必ず先に実施させるため、遅延を入れてタスクを実行する。
                    int wait = config.getJapanizeWait();
                    task.runTaskLater(LunaChat.getInstance(), wait);
                }
            }

            // 発言内容の設定
            event.setMessage(message);

            // ロギング
            logNormalChat(message, player.getName());

            return;
        }
    }

    /**
     * 既定のチャンネルへの参加を試みる。
     * @param player プレイヤー
     * @return 参加できたかどうか
     */
    private boolean tryJoinToGlobalChannel(Player player) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        String gcName = config.getGlobalChannel();

        // チャンネルが存在しない場合は作成する
        Channel global = api.getChannel(gcName);
        if ( global == null ) {
            global = api.createChannel(gcName);
        }

        // デフォルト発言先が無いなら、グローバルチャンネルに設定する
        Channel dchannel = api.getDefaultChannel(player.getName());
        if ( dchannel == null ) {
            api.setDefaultChannel(player.getName(), gcName);
        }

        return true;
    }

    /**
     * 強制参加チャンネルへ参加させる
     * @param player プレイヤー
     */
    private void forceJoinToForceJoinChannels(Player player) {

        LunaChatConfig config = LunaChat.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();

        List<String> forceJoinChannels = config.getForceJoinChannels();

        for ( String cname : forceJoinChannels ) {

            // チャンネルが存在しない場合は作成する
            Channel channel = api.getChannel(cname);
            if ( channel == null ) {
                channel = api.createChannel(cname);
            }

            // チャンネルのメンバーでないなら、参加する
            ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
            if ( !channel.getMembers().contains(cp) ) {
                channel.addMember(cp);
            }

            // デフォルト発言先が無いなら、グローバルチャンネルに設定する
            Channel dchannel = api.getDefaultChannel(player.getName());
            if ( dchannel == null ) {
                api.setDefaultChannel(player.getName(), cname);
            }
        }
    }

    /**
     * 通常チャットのフォーマット設定のキーワードを置き換えして返す
     * @param org フォーマット設定
     * @param player 発言プレイヤー
     * @return キーワード置き換え済みの文字列
     */
    private String replaceNormalChatFormatKeywords(String org, Player player) {

        String format = org;
        format = format.replace("%username", "%1$s");
        format = format.replace("%msg", "%2$s");

        if ( format.contains("%date") ) {
            format = format.replace("%date", dateFormat.format(new Date()));
        }
        if ( format.contains("%time") ) {
            format = format.replace("%time", timeFormat.format(new Date()));
        }

        if ( format.contains("%prefix") || format.contains("%suffix") ) {

            String prefix = "";
            String suffix = "";
            VaultChatBridge vaultchat = LunaChat.getInstance().getVaultChat();
            if ( vaultchat != null ) {
                prefix = vaultchat.getPlayerPrefix(player);
                suffix = vaultchat.getPlayerSuffix(player);
            }
            format = format.replace("%prefix", prefix);
            format = format.replace("%suffix", suffix);
        }

        if ( format.contains("%world") ) {

            String worldname = null;
            if ( LunaChat.getInstance().getMultiverseCore() != null ) {
                worldname = LunaChat.getInstance().getMultiverseCore().getWorldAlias(player.getWorld());
            }
            if ( worldname == null || worldname.equals("") ) {
                worldname = player.getWorld().getName();
            }
            format = format.replace("%world", worldname);
        }

        return Utility.replaceColorCode(format);
    }

    /**
     * プレイヤーのサーバー参加時用の参加チャンネルリストを返す
     * @param player プレイヤー
     * @return リスト
     */
    private ArrayList<String> getListForMotd(Player player) {

        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);
        LunaChatAPI api = LunaChat.getInstance().getLunaChatAPI();
        Channel dc = api.getDefaultChannel(cp.getName());
        String dchannel = "";
        if ( dc != null ) {
            dchannel = dc.getName().toLowerCase();
        }

        ArrayList<String> items = new ArrayList<String>();
        items.add(MOTD_FIRSTLINE);
        for ( Channel channel : api.getChannels() ) {

            // BANされているチャンネルは表示しない
            if ( channel.getBanned().contains(cp) ) {
                continue;
            }

            // 個人チャットはリストに表示しない
            if ( channel.isPersonalChat() ) {
                continue;
            }

            // 参加していないチャンネルは、グローバルチャンネルを除き表示しない
            if ( !channel.getMembers().contains(cp) &&
                    !channel.isGlobalChannel() ) {
                continue;
            }

            String disp = ChatColor.WHITE + channel.getName();
            if ( channel.getName().equals(dchannel) ) {
                disp = ChatColor.RED + channel.getName();
            }
            String desc = channel.getDescription();
            int onlineNum = channel.getOnlineNum();
            int memberNum = channel.getTotalNum();
            String item = String.format(
                    LIST_FORMAT, disp, onlineNum, memberNum, desc);
            items.add(item);
        }
        items.add(LIST_ENDLINE);

        return items;
    }

    /**
     * チャンネルに発言処理を行う
     * @param player プレイヤー
     * @param channel チャンネル
     * @param message 発言内容
     * @return イベントでキャンセルされたかどうか
     */
    private boolean chatToChannelWithEvent(ChannelPlayer player, Channel channel, String message) {

        // LunaChatPreChatEvent イベントコール
        LunaChatPreChatEvent preChatEvent = new LunaChatPreChatEvent(
                channel.getName(), player, message);
        Utility.callEventSync(preChatEvent);
        if ( preChatEvent.isCancelled() ) {
            return true;
        }
        Channel alt = preChatEvent.getChannel();
        if ( alt != null ) {
            channel = alt;
        }
        message = preChatEvent.getMessage();

        // チャンネルチャット発言
        channel.chat(player, message);

        return false;
    }

    /**
     * 通常チャットの発言をログファイルへ記録する
     * @param message
     * @param player
     */
    private void logNormalChat(String message, String player) {

        LunaChat.getInstance().getNormalChatLogger().log(message, player);
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    private void sendResourceMessage(
            CommandSender sender, String pre, String key, Object... args) {

        String org = Resources.get(key);
        if ( org == null || org.equals("") ) {
            return;
        }
        String msg = String.format(pre + org, args);
        sender.sendMessage(msg);
    }
}
