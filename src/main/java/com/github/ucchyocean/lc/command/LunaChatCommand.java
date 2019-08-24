/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.Resources;
import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;

/**
 * Lunachatコマンドの処理クラス
 * @author ucchy
 */
public class LunaChatCommand implements CommandExecutor {

    private static final String PREERR = Resources.get("errorPrefix");

    private ArrayList<SubCommandAbst> commands;
    private ArrayList<SubCommandAbst> commonCommands;
    private JoinCommand joinCommand;
    private HelpCommand helpCommand;

    /**
     * コンストラクタ
     */
    public LunaChatCommand() {

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
        commands.add(new CheckCommand());
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

    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {

        // チャンネルチャットが無効でも利用できるコマンドはここで処理する
        // （hide, unhide, dic, dictionary, reload）
        if ( args.length >= 1 ) {
            for ( SubCommandAbst c : commonCommands ) {
                if ( c.getCommandName().equalsIgnoreCase(args[0]) ) {

                    // パーミッションの確認
                    String node = c.getPermissionNode();
                    if ( !sender.hasPermission(node) ) {
                        sendResourceMessage(sender, PREERR, "errmsgPermission", node);
                        return true;
                    }

                    // 実行
                    return c.runCommand(sender, label, args);
                }
            }
        }

        // チャンネルチャット機能が無効になっている場合は、メッセージを表示して終了
        if ( !LunaChat.getInstance().getLunaChatConfig().isEnableChannelChat()
                && !sender.isOp() ) {
            sendResourceMessage(sender, PREERR, "errmsgChannelChatDisabled");
            return true;
        }

        // 引数なしは、ヘルプを表示
        if (args.length == 0) {
            helpCommand.runCommand(sender, label, args);
            return true;
        }

        // 第1引数に指定されたコマンドを実行する
        for ( SubCommandAbst c : commands ) {
            if ( c.getCommandName().equalsIgnoreCase(args[0]) ) {

                // パーミッションの確認
                String node = c.getPermissionNode();
                if ( !sender.hasPermission(node) ) {
                    sendResourceMessage(sender, PREERR, "errmsgPermission", node);
                    return true;
                }

                // 実行
                return c.runCommand(sender, label, args);
            }
        }

        // 第1引数がコマンドでないなら、joinが指定されたとみなす
        String node = joinCommand.getPermissionNode();
        if ( !sender.hasPermission(node) ) {
            sendResourceMessage(sender, PREERR, "errmsgPermission", node);
            return true;
        }

        return joinCommand.runCommand(sender, label, args);
    }

    /**
     * TABキー補完が実行されたときに呼び出されるメソッド
     * @param sender TABキー補完の実行者
     * @param command 実行されたコマンド
     * @param label 実行されたコマンドのラベル
     * @param args 実行されたコマンドの引数
     * @return 補完候補
     */
    public List<String> onTabComplete(
            CommandSender sender, Command command, String label, String[] args) {

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
            for ( String name : getListCanJoin(sender) ) {
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
            for ( Player player : Utility.getOnlinePlayers() ) {
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
            for ( String name : getListCanJoin(sender) ) {
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
            for ( String name : getListCanJoin(sender) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            for ( Player player : Utility.getOnlinePlayers() ) {
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
            for ( Player player : Utility.getOnlinePlayers() ) {
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
            for ( String name : getListCanJoin(sender) ) {
                if ( name.toLowerCase().startsWith(arg) ) {
                    items.add(name);
                }
            }
            return items;

        } else if ( args.length == 2 && args[0].equalsIgnoreCase("remove") ) {
            // 削除可能チャンネル名で補完する
            String arg = args[1].toLowerCase();
            ArrayList<String> items = new ArrayList<String>();
            for ( String name : getListCanRemove(sender) ) {
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
                    LunaChat.getInstance().getLunaChatAPI().getAllDictionary().keySet() ) {
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
            for ( Player player : Utility.getOnlinePlayers() ) {
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
            for ( String name : getListCanJoin(sender) ) {
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
     *
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
        sender.sendMessage(msg);
    }

    /**
     * TAB補完用の参加可能チャンネルリストを返す
     * @param sender コマンド実行者
     * @return リスト
     */
    private ArrayList<String> getListCanJoin(CommandSender sender) {

        ArrayList<String> items = new ArrayList<String>();
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(sender);

        for ( Channel channel : LunaChat.getInstance().getLunaChatAPI().getChannels() ) {

            // BANされているチャンネルは対象外
            if ( channel.getBanned().contains(cp) ) {
                continue;
            }

            // 個人チャットは対象外
            if ( channel.isPersonalChat() ) {
                continue;
            }

            // 未参加で visible=false のチャンネルは対象外
            if ( sender instanceof Player &&
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
    private ArrayList<String> getListCanRemove(CommandSender sender) {

        ArrayList<String> items = new ArrayList<String>();

        for ( Channel channel : LunaChat.getInstance().getLunaChatAPI().getChannels() ) {

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
}
