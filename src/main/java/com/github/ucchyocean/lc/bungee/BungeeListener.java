/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.bungee;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.LunaChatConfig;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.github.ucchyocean.lc.member.ChannelMember;
import com.github.ucchyocean.lc.member.ChannelMemberBukkit;
import com.github.ucchyocean.lc.member.ChannelMemberPlayer;
import com.github.ucchyocean.lc.member.ChannelMemberProxiedPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * プレイヤーの行動を監視するリスナ（BungeeCord実装）
 * @author ucchy
 */
public class BungeeListener implements Listener {

    private static final String MOTD_FIRSTLINE = Resources.get("motdFirstLine");
    private static final String LIST_ENDLINE = Resources.get("listEndLine");
    private static final String LIST_FORMAT = Resources.get("listFormat");
    private static final String PREERR = Resources.get("errorPrefix");

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    /**
     * プレイヤーがチャット発言した時に呼び出されるメソッド
     * @param event
     */
    @EventHandler
    public void onChat(ChatEvent event) {
        processChatEvent(event);
    }

    /**
     * プレイヤーのサーバー参加ごとに呼び出されるメソッド
     * @param event プレイヤー参加イベント
     */
    @EventHandler
    public void onJoin(PostLoginEvent event) {

        LunaChatConfig config = LunaChatBungee.getInstance().getLunaChatConfig();
        ProxiedPlayer player = event.getPlayer();

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
    public void onQuit(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        // お互いがオフラインになるPMチャンネルがある場合は
        // チャンネルをクリアする
        ArrayList<Channel> deleteList = new ArrayList<Channel>();

        for ( Channel channel : LunaChatBungee.getInstance().getLunaChatAPI().getChannels() ) {
            String cname = channel.getName();
            String pname = player.getName();
            if ( channel.isPersonalChat() && cname.contains(pname) ) {
                boolean isAllOffline = true;
                for ( ChannelMember cp : channel.getMembers() ) {
                    if ( !cp.equals(player) && cp.isOnline() &&
                            (cp instanceof ChannelMemberPlayer) ) {
                        isAllOffline = false;
                    }
                }
                if ( isAllOffline ) {
                    deleteList.add(channel);
                }
            }
        }

        for ( Channel channel : deleteList ) {
            LunaChatBungee.getInstance().getLunaChatAPI().removeChannel(channel.getName());
        }
    }

    /**
     * プレイヤーのチャットごとに呼び出されるメソッド
     * @param event チャットイベント
     */
    private void processChatEvent(ChatEvent event) {

        // コマンド実行の場合は、そのまま無視する
        if ( event.isCommand() ) {
            return;
        }

        // プレイヤーの発言ではない場合は、そのまま無視する
        if ( !(event.getSender() instanceof ProxiedPlayer) ) {
            return;
        }


        LunaChatConfig config = LunaChatBungee.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();

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
                    ProxiedPlayer pp = (ProxiedPlayer)event.getSender();
                    ChannelMember player =
                            ChannelMemberProxiedPlayer.getChannelMemberFromSender(pp);
                    if ( !channel.getMembers().contains(player) ) {
                        // 指定されたチャンネルに参加していないなら、エラーを表示して何も発言せずに終了する。
                        sendResourceMessage(pp, PREERR, "errmsgNomember");
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

        ChannelMember player =
                ChannelMemberProxiedPlayer.getChannelMemberFromSender((ProxiedPlayer)event.getSender());
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
    private void chatGlobal(ChatEvent event) {

        LunaChatConfig config = LunaChatBungee.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();
        ChannelMember player =
                ChannelMemberBukkit.getChannelMember((ProxiedPlayer)event.getSender());

        if ( !config.getGlobalChannel().equals("") ) {
            // グローバルチャンネル設定がある場合

            // グローバルチャンネルの取得、無ければ作成
            Channel global = api.getChannel(config.getGlobalChannel());
            if ( global == null ) {
                global = api.createChannel(config.getGlobalChannel());
            }

            // LunaChatPreChatEvent イベントコール
//            EventResult result = LunaChat.getEventSender().sendLunaChatPreChatEvent(
//                    global.getName(), player, event.getMessage());
//            if ( result.isCancelled() ) {
//                event.setCancelled(true);
//                return;
//            }

//            String altChannelName = result.getValueAsString("channelName");
//            if ( altChannelName != null ) {
//                Channel alt = api.getChannel(altChannelName);
//                if ( alt != null ) {
//                    global = alt;
//                }
//            }
//            String message = result.getValueAsString("message");
            String message = event.getMessage();

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
            // TODO BungeeはisEnableNormalChatMessageFormat() を適用しない
            String format = replaceNormalChatFormatKeywords(
                    config.getNormalChatMessageFormat(), (ProxiedPlayer)event.getSender());

            // カラーコード置き換え
            // 置き換え設定になっていて、発言者がパーミッションを持っているなら、置き換えする
            if ( config.isEnableNormalChatColorCode() &&
                    player.hasPermission("lunachat.allowcc") ) {
                message = Utility.replaceColorCode(message);
            }

            // recipientsを作成する。hideされている場合は対象に加えない
            ArrayList<ChannelMember> recipients = new ArrayList<>();
            List<ChannelMember> hideList = api.getHidelist(player);
            for ( String server : ProxyServer.getInstance().getServers().keySet() ) {
                for ( ProxiedPlayer target : ProxyServer.getInstance().getServerInfo(server).getPlayers() ) {
                    ChannelMember cm = ChannelMemberProxiedPlayer.getChannelPlayer(target);
                    if ( !hideList.contains(cm) ) recipients.add(cm);
                }
            }
            // TODO Bungeeのコンソールをrecipientsに加える？

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

            // 元のイベントをキャンセル
            event.setCancelled(true);

            // Japanize変換と、発言処理
            if ( !skipJapanize &&
                    LunaChatBungee.getInstance().getLunaChatAPI().isPlayerJapanize(player.getName()) &&
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

                    String japanized = api.japanize(
                            kanaTemp, config.getJapanizeType());
                    if ( japanized != null ) {
                        String temp = taskFormat.replace("%msg", message);
                        message += File.separator +temp.replace("%japanize", japanized);
                        // TODO 改行を入れて送信は、現在のJSON形式チャットで可能なのか確認する
                    }
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
    private boolean tryJoinToGlobalChannel(ProxiedPlayer player) {

        LunaChatConfig config = LunaChatBungee.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();

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
    private void forceJoinToForceJoinChannels(ProxiedPlayer player) {

        LunaChatConfig config = LunaChatBungee.getInstance().getLunaChatConfig();
        LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();

        List<String> forceJoinChannels = config.getForceJoinChannels();

        for ( String cname : forceJoinChannels ) {

            // チャンネルが存在しない場合は作成する
            Channel channel = api.getChannel(cname);
            if ( channel == null ) {
                channel = api.createChannel(cname);
            }

            // チャンネルのメンバーでないなら、参加する
            ChannelMember cp = ChannelMemberProxiedPlayer.getChannelMemberFromSender(player);
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
    private String replaceNormalChatFormatKeywords(String org, ProxiedPlayer player) {

        // TODO StringではなくStringBuilderを使う

        String format = org;
        format = format.replace("%username", player.getDisplayName());
//        format = format.replace("%msg", "%2$s");
        format = format.replace("%player", player.getName());

        if ( format.contains("%date") ) {
            format = format.replace("%date", dateFormat.format(new Date()));
        }
        if ( format.contains("%time") ) {
            format = format.replace("%time", timeFormat.format(new Date()));
        }

        if ( format.contains("%prefix") || format.contains("%suffix") ) {

            String prefix = "";
            String suffix = "";

            // TODO BungeeCordの装飾プラグインを確認
//            VaultChatBridge vaultchat = LunaChatBungee.getInstance().getVaultChat();
//            if ( vaultchat != null ) {
//                prefix = vaultchat.getPlayerPrefix(player);
//                suffix = vaultchat.getPlayerSuffix(player);
//            }

            format = format.replace("%prefix", prefix);
            format = format.replace("%suffix", suffix);
        }

        if ( format.contains("%server") ) {
            String serverName = "";
            if ( player.getServer() != null ) {
                serverName = player.getServer().getInfo().getName();
            }
            format = format.replace("%server", serverName);
        }

        if ( format.contains("%world") ) {
            // TODO world名を置き替えるかどうかを検討する
            String worldname = "";
//            player.getWorld().getName();
            format = format.replace("%world", worldname);
        }

        return Utility.replaceColorCode(format);
    }

    /**
     * プレイヤーのサーバー参加時用の参加チャンネルリストを返す
     * @param player プレイヤー
     * @return リスト
     */
    private ArrayList<String> getListForMotd(ProxiedPlayer player) {

        ChannelMember cp = ChannelMemberProxiedPlayer.getChannelMemberFromSender(player);
        LunaChatAPI api = LunaChatBungee.getInstance().getLunaChatAPI();
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
    private boolean chatToChannelWithEvent(ChannelMember player, Channel channel, String message) {

        // LunaChatPreChatEvent イベントコール
//        EventResult result = LunaChat.getEventSender().sendLunaChatPreChatEvent(
//                channel.getName(), player, message);
//        if ( result.isCancelled() ) {
//            return true;
//        }

//        String channelName = result.getValueAsString("channelName");
//        if ( channelName != null ) {
//            Channel alt = LunaChatBungee.getInstance().getLunaChatAPI().getChannel(channelName);
//            if ( alt != null ) {
//                channel = alt;
//            }
//        }
//        message = result.getValueAsString("message");

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

        LunaChatBungee.getInstance().getNormalChatLogger().log(message, player);
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
