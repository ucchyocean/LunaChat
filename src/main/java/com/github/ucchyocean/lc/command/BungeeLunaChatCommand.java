/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.Collection;

import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.bukkit.LunaChatBukkit;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.member.ChannelMember;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Lunachatコマンドの処理クラス（Bungee実装）
 * @author ucchy
 */
public class BungeeLunaChatCommand extends Command implements TabExecutor {

    private static final String PREERR = Resources.get("errorPrefix");

    private ArrayList<SubCommandAbst> commands;
    private ArrayList<SubCommandAbst> commonCommands;
    private JoinCommand joinCommand;
    private HelpCommand helpCommand;

    public BungeeLunaChatCommand(String name) {
        super(name);

        commands = new ArrayList<SubCommandAbst>();
        joinCommand = new JoinCommand();
        commands.add(joinCommand);
        commands.add(new LeaveCommand());
        commands.add(new ListCommand());
        commands.add(new InviteCommand());
        commands.add(new AcceptCommand());
        commands.add(new DenyCommand());
        commands.add(new KickCommand());
        commands.add(new BanCommand());
        commands.add(new PardonCommand());
        commands.add(new MuteCommand());
        commands.add(new UnmuteCommand());
        commands.add(new InfoCommand());
        commands.add(new LogCommand());
        commands.add(new CreateCommand());
        commands.add(new RemoveCommand());
        commands.add(new FormatCommand());
        commands.add(new ModeratorCommand());
        commands.add(new ModCommand());
        commands.add(new OptionCommand());
        commands.add(new TemplateCommand());
        commands.add(new SetCommand());
        helpCommand = new HelpCommand(commands);
        commands.add(helpCommand);

        commonCommands = new ArrayList<SubCommandAbst>();
        commonCommands.add(new HideCommand());
        commonCommands.add(new UnhideCommand());
        commonCommands.add(new DictionaryCommand());
        commonCommands.add(new DicCommand());
        commonCommands.add(new ReloadCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // チャンネルチャットが無効でも利用できるコマンドはここで処理する
        // （hide, unhide, dic, dictionary, reload）
        if ( args.length >= 1 ) {
            for ( SubCommandAbst c : commonCommands ) {
                if ( c.getCommandName().equalsIgnoreCase(args[0]) ) {

                    // パーミッションの確認
                    String node = c.getPermissionNode();
                    if ( !sender.hasPermission(node) ) {
                        sendResourceMessage(sender, PREERR, "errmsgPermission", node);
                        return;
                    }

                    // 実行
                    c.runCommand(ChannelMember.getChannelMember(sender), getName(), args);
                    return;
                }
            }
        }

        // チャンネルチャット機能が無効になっている場合は、メッセージを表示して終了
        if ( !LunaChatBukkit.getInstance().getLunaChatConfig().isEnableChannelChat()
                && !sender.hasPermission("lunachat-admin") ) {
            sendResourceMessage(sender, PREERR, "errmsgChannelChatDisabled");
            return;
        }

        // 引数なしは、ヘルプを表示
        if (args.length == 0) {
            helpCommand.runCommand(ChannelMember.getChannelMember(sender), getName(), args);
            return;
        }

        // 第1引数に指定されたコマンドを実行する
        for ( SubCommandAbst c : commands ) {
            if ( c.getCommandName().equalsIgnoreCase(args[0]) ) {

                // パーミッションの確認
                String node = c.getPermissionNode();
                if ( !sender.hasPermission(node) ) {
                    sendResourceMessage(sender, PREERR, "errmsgPermission", node);
                    return;
                }

                // 実行
                c.runCommand(ChannelMember.getChannelMember(sender), getName(), args);
                return;
            }
        }

        // 第1引数がコマンドでないなら、joinが指定されたとみなす
        String node = joinCommand.getPermissionNode();
        if ( !sender.hasPermission(node) ) {
            sendResourceMessage(sender, PREERR, "errmsgPermission", node);
            return;
        }

        joinCommand.runCommand(ChannelMember.getChannelMember(sender), getName(), args);
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender TABキー補完の実行者
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     */
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if ( args.length == 1 ) {
            // コマンド名で補完する
            String arg = args[0].toLowerCase();
            ArrayList<String> coms = new ArrayList<String>();
            for ( SubCommandAbst c : commands ) {
                if ( c.getCommandName().startsWith(arg) &&
                        sender.hasPermission(c.getPermissionNode()) ) {
                    coms.add(c.getCommandName());
                }
            }
            for ( SubCommandAbst c : commonCommands ) {
                if ( c.getCommandName().startsWith(arg) &&
                        sender.hasPermission(c.getPermissionNode()) ) {
                    coms.add(c.getCommandName());
                }
            }
            return coms;

        } else if ( args.length == 2 && (
                args[0].equalsIgnoreCase("join") ||
                args[0].equalsIgnoreCase("info") ) ) {
            // 参加可能チャンネル名で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanJoin(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 && (
                args[0].equalsIgnoreCase("ban") ||
                args[0].equalsIgnoreCase("pardon") ||
                args[0].equalsIgnoreCase("kick") ||
                args[0].equalsIgnoreCase("mute") ||
                args[0].equalsIgnoreCase("unmute") ) ) {
            // プレイヤー名で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( ProxiedPlayer player : getOnlinePlayers() ) {
                String pname = player.getName();
                pname = pname == null ? "" : pname.toLowerCase();
                if ( pname.startsWith(arg) ) {
                    items.add(player.getName());
                }
            }
            return items;

        } else if ( args.length == 3 && (
                args[0].equalsIgnoreCase("ban") ||
                args[0].equalsIgnoreCase("pardon") ||
                args[0].equalsIgnoreCase("kick") ||
                args[0].equalsIgnoreCase("mute") ||
                args[0].equalsIgnoreCase("unmute") ) ) {
            // チャンネル名で補完する
            String arg = args[2].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanJoin(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 && (
                args[0].equalsIgnoreCase("hide") ||
                args[0].equalsIgnoreCase("unhide") ) ) {

            // 参加可能チャンネル名とプレイヤー名と
            // 文字列"player", "channel"で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanJoin(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            for ( ProxiedPlayer player : getOnlinePlayers() ) {
                String pname = player.getName();
                pname = pname == null ? "" : pname.toLowerCase();
                if ( pname.startsWith(arg) ) {
                    items.add(player.getName());
                }
            }
            if ( "player".startsWith(arg) ) {
                items.add("player");
            }
            if ( "channel".startsWith(arg) ) {
                items.add("channel");
            }
            return items;

        } else if ( args.length == 3 &&
                (args[0].equalsIgnoreCase("hide") ||
                args[0].equalsIgnoreCase("unhide") ) &&
                args[1].equalsIgnoreCase("player") ) {

            // プレイヤー名で補完する
            String arg = args[2].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( ProxiedPlayer player : getOnlinePlayers() ) {
                String pname = player.getName();
                pname = pname == null ? "" : pname.toLowerCase();
                if ( pname.startsWith(arg) ) {
                    items.add(player.getName());
                }
            }
            return items;

        } else if ( args.length == 3 &&
                (args[0].equalsIgnoreCase("hide") ||
                args[0].equalsIgnoreCase("unhide") ) &&
                args[1].equalsIgnoreCase("channel") ) {

            // チャンネル名で補完する
            String arg = args[2].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanJoin(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("remove") ) {
            // 削除可能チャンネル名で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanRemove(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 &&
                (args[0].equalsIgnoreCase("dic") || args[0].equalsIgnoreCase("dictionary")) ) {
            // add、remove、viewで補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : new String[]{"add", "remove", "view"} ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 3 &&
                (args[0].equalsIgnoreCase("dic") || args[0].equalsIgnoreCase("dictionary")) &&
                args[1].equalsIgnoreCase("remove") ) {
            // 辞書に登録されているワードで補完する
            String arg = args[2].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name :
                    LunaChatBukkit.getInstance().getLunaChatAPI().getAllDictionary().keySet() ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 &&
                args[0].equalsIgnoreCase("set") ) {
            // "default" で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            if ( "default".startsWith(arg) ) {
                items.add("default");
            }
            return items;

        } else if ( args.length == 3 &&
                args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("default") ) {
            // プレイヤー名で補完する
            String arg = args[2].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( ProxiedPlayer player : getOnlinePlayers() ) {
                String pname = player.getName();
                pname = pname == null ? "" : pname.toLowerCase();
                if ( pname.startsWith(arg) ) {
                    items.add(player.getName());
                }
            }
            return items;

        } else if ( args.length == 4 &&
                args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("default") ) {
            // チャンネル名で補完する
            String arg = args[3].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanJoin(ChannelMember.getChannelMember(sender)) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        }
        return null;
    }

    /**
     * メッセージリソースのメッセージを、カラーコード置き換えしつつ、senderに送信する
     * @param sender メッセージの送り先
     * @param pre プレフィックス
     * @param key リソースキー
     * @param args リソース内の置き換え対象キーワード
     */
    private void sendResourceMessage(CommandSender sender, String pre,
            String key, Object... args) {

        String org = Resources.get(key);
        if ( org == null || org.equals("") ) {
            return;
        }
        String msg = String.format(pre + org, args);
        sender.sendMessage(TextComponent.fromLegacyText(msg));
    }

    /**
     * TAB補完用の参加可能チャンネルリストを返す
     * @param sender コマンド実行者
     * @return リスト
     */
    private ArrayList<String> getListCanJoin(ChannelMember sender) {

        ArrayList<String> items = new ArrayList<String>();
        ChannelMember cp = ChannelMember.getChannelMember(sender);

        for ( Channel channel : LunaChatBukkit.getInstance().getLunaChatAPI().getChannels() ) {

            // BANされているチャンネルは対象外
            if ( channel.getBanned().contains(cp) ) {
                continue;
            }

            // 個人チャットは対象外
            if ( channel.isPersonalChat() ) {
                continue;
            }

            // 未参加で visible=false のチャンネルは対象外
            if ( sender instanceof ProxiedPlayer &&
                    !channel.getMembers().contains(cp) &&
                    !channel.isGlobalChannel() &&
                    !channel.isVisible() ) {
                continue;
            }

            items.add(channel.getName());
        }

        return items;
    }

    /**
     * TAB補完用の削除可能チャンネルリストを返す
     * @param sender コマンド実行者
     * @return リスト
     */
    private ArrayList<String> getListCanRemove(ChannelMember sender) {

        ArrayList<String> items = new ArrayList<String>();

        for ( Channel channel : LunaChatBukkit.getInstance().getLunaChatAPI().getChannels() ) {

            // 実行者がチャンネルモデレーターでない場合は対象外
            if ( !channel.hasModeratorPermission(sender) ) {
                continue;
            }

            // 個人チャットは対象外
            if ( channel.isPersonalChat() ) {
                continue;
            }

            // グローバルチャンネルは対象外
            if ( channel.isGlobalChannel() ) {
                continue;
            }

            items.add(channel.getName());
        }

        return items;
    }

    private Collection<ProxiedPlayer> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers();
    }
}
