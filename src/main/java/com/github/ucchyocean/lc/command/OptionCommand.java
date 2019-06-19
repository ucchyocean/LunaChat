/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.lc.command;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ucchyocean.lc.Utility;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.event.LunaChatChannelOptionChangedEvent;
import com.github.ucchyocean.lc.japanize.JapanizeType;

/**
 * optionコマンドの実行クラス
 * @author ucchy
 */
public class OptionCommand extends SubCommandAbst {

    private static final int MAX_LENGTH_DESCRIPTION = 30;
    private static final int MAX_LENGTH_ALIAS = 15;
    private static final int MAX_LENGTH_PASSWORD = 15;

    private static final String COMMAND_NAME = "option";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;
    private static final String USAGE_KEY = "usageOption";

    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getPermissionNode()
     */
    @Override
    public String getPermissionNode() {
        return PERMISSION_NODE;
    }

    /**
     * コマンドの種別を取得します。
     * @return コマンド種別
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#getCommandType()
     */
    @Override
    public CommandType getCommandType() {
        return CommandType.MODERATOR;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#sendUsageMessage()
     */
    @Override
    public void sendUsageMessage(
            CommandSender sender, String label) {
        sendResourceMessage(sender, "", USAGE_KEY, label);
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc.command.SubCommandAbst#runCommand(java.lang.String[])
     */
    @Override
    public boolean runCommand(
            CommandSender sender, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        // 引数チェック
        // このコマンドは、コンソールでも実行できるが、その場合はチャンネル名を指定する必要がある
        ArrayList<String> optionsTemp = new ArrayList<String>();
        String cname = null;
        if ( player != null && args.length >= 2 ) {
            Channel def = api.getDefaultChannel(player.getName());
            if ( def != null ) {
                cname = def.getName();
            }
            for (int i = 1; i < args.length; i++) {
                optionsTemp.add(args[i]);
            }
        } else if ( args.length >= 3 ) {
            cname = args[1];
            for (int i = 2; i < args.length; i++) {
                optionsTemp.add(args[i]);
            }
        } else {
            sendResourceMessage(sender, PREERR, "errmsgCommand");
            return true;
        }

        Channel channel = api.getChannel(cname);

        // チャンネルが存在するかどうかをチェックする
        if ( channel == null ) {
            sendResourceMessage(sender, PREERR, "errmsgNotExist");
            return true;
        }

        cname = channel.getName();

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sendResourceMessage(sender, PREERR, "errmsgNotModerator");
            return true;
        }

        // 指定内容を解析する
        HashMap<String, String> options = new HashMap<String, String>();
        for ( String t : optionsTemp ) {
            int index = t.indexOf("=");
            if ( index == -1 ) {
                continue;
            }
            options.put(t.substring(0, index), t.substring(index + 1));
        }

        // イベントコール
        LunaChatChannelOptionChangedEvent event =
                new LunaChatChannelOptionChangedEvent(cname, sender, options);
        Utility.callEventSync(event);
        if ( event.isCancelled() ) {
            return true;
        }
        options = event.getOptions();

        // 設定する
        boolean setOption = false;

        if ( options.containsKey("description") ) {
            // チャンネル説明文

            String pnode = PERMISSION_NODE + ".description";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String desc = options.get("description");
                // チャンネル説明文は最大文字長を超えていないか確認
                if ( desc.length() > MAX_LENGTH_DESCRIPTION ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgToolongDescription", MAX_LENGTH_DESCRIPTION);
                } else {
                    channel.setDescription(desc);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "description", desc);
                    setOption = true;
                }
            }
        }

        if ( options.containsKey("alias") ) {
            // チャンネル別名

            String pnode = PERMISSION_NODE + ".alias";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String alias = options.get("alias");
                if ( alias.length() > MAX_LENGTH_ALIAS ) {
                    // チャンネル別名が最大文字長を超えている
                    sendResourceMessage(sender, PREERR,
                            "errmsgToolongAlias", MAX_LENGTH_ALIAS);

                } else if (api.getChannel(alias) != null) {
                    // 別のチャンネル名またはチャンネル別名と重複する
                    sendResourceMessage(sender, PREERR,
                            "errmsgDuplicatedAlias",
                            alias, api.getChannel(alias).getName());

                } else {
                    channel.setAlias(alias);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "alias", alias);
                    setOption = true;
                }
            }
        }

        if ( options.containsKey("color") ) {
            // チャンネルカラー

            String pnode = PERMISSION_NODE + ".color";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String code = options.get("color");
                if ( Utility.isValidColor(code) ) {
                    code = Utility.changeToColorCode(code);
                }
                if ( Utility.isValidColorCode(code) ) {
                    channel.setColorCode(code);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "color", options.get("color"));
                    setOption = true;
                } else {
                    sendResourceMessage(sender, PREERR,
                            "errmsgInvalidColorCode", options.get("color"));
                }
            }
        }

        if ( options.containsKey("broadcast") ) {
            // ブロードキャストチャンネル

            String pnode = PERMISSION_NODE + ".broadcast";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String value = options.get("broadcast");

                if ( value.equals("") || value.equalsIgnoreCase("false") ) {
                    if ( channel.isGlobalChannel() ) {
                        sendResourceMessage(sender, PREERR,
                                "errmsgCannotOffGlobalBroadcast");
                    } else {
                        channel.setBroadcast(false);
                        sendResourceMessage(sender, PREINFO,
                                "cmdmsgOption", "broadcast", "false");
                        setOption = true;
                    }
                } else if ( value.equalsIgnoreCase("true") ) {
                    channel.setBroadcast(true);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "broadcast", "true");
                    setOption = true;
                } else {
                    sendResourceMessage(sender, PREERR,
                            "errmsgInvalidBooleanOption", "broadcast");
                }
            }
        }

        if ( options.containsKey("range") ) {
            // レンジ

            String pnode = PERMISSION_NODE + ".range";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String value = options.get("range");

                if ( value.equals("") ) {
                    channel.setWorldRange(false);
                    channel.setChatRange(0);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "range", "無効");
                    setOption = true;
                } else if ( value.equalsIgnoreCase("world") ) {
                    channel.setWorldRange(true);
                    channel.setChatRange(0);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "range", "world");
                    setOption = true;
                } else if ( value.matches("[0-9]+") ) {
                    channel.setWorldRange(true);
                    channel.setChatRange(Integer.parseInt(value));
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "range", value);
                    setOption = true;
                } else {
                    sendResourceMessage(sender, PREERR,
                            "errmsgInvalidRangeOption");
                }
            }
        }

        if ( options.containsKey("allowcc") ) {
            // カラーコード使用可否

            String pnode = PERMISSION_NODE + ".allowcc";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String value = options.get("allowcc");

                if ( value.equals("") || value.equalsIgnoreCase("false") ) {
                    channel.setAllowCC(false);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "allowcc", "false");
                    setOption = true;
                } else if ( value.equalsIgnoreCase("true") ) {
                    channel.setAllowCC(true);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "allowcc", "true");
                    setOption = true;
                } else {
                    sendResourceMessage(sender, PREERR,
                            "errmsgInvalidBooleanOption", "allowcc");
                }
            }
        }

        if ( options.containsKey("japanize") ) {
            // Japanize変換設定

            String pnode = PERMISSION_NODE + ".japanize";
            if ( !sender.hasPermission(pnode) ) {
                sendResourceMessage(sender, PREERR,
                        "errmsgNotPermission", pnode);
            } else {

                String value = options.get("japanize");

                if ( value.equals("") ) {
                    channel.setJapanizeType(null);
                    sendResourceMessage(sender, PREINFO,
                            "cmdmsgOption", "japanize", "デフォルト");
                    setOption = true;
                } else {
                    JapanizeType type = JapanizeType.fromID(value, null);
                    if ( type == null ) {
                        sendResourceMessage(sender, PREERR,
                                "errmsgInvalidJapanizeOption", "japanize", value);
                    } else {
                        channel.setJapanizeType(type);
                        sendResourceMessage(sender, PREINFO,
                                "cmdmsgOption", "japanize", type.toString());
                        setOption = true;
                    }
                }
            }
        }

        if ( !channel.isGlobalChannel() ) {

            if ( options.containsKey("password") ) {
                // パスワード

                String pnode = PERMISSION_NODE + ".password";
                if ( !sender.hasPermission(pnode) ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgNotPermission", pnode);
                } else {

                    String password = options.get("password");
                    // パスワードが文字制限を超える場合はエラー
                    if ( password.length() > MAX_LENGTH_PASSWORD ) {
                        sendResourceMessage(sender, PREERR,
                                "errmsgToolongPassword", MAX_LENGTH_PASSWORD);
                    } else {
                        channel.setPassword(password);
                        sendResourceMessage(sender, PREINFO,
                                "cmdmsgOption", "password", password);
                        setOption = true;
                    }
                }
            }

            if ( options.containsKey("visible") ) {
                // ビジブル

                String pnode = PERMISSION_NODE + ".visible";
                if ( !sender.hasPermission(pnode) ) {
                    sendResourceMessage(sender, PREERR,
                            "errmsgNotPermission", pnode);
                } else {

                    String temp = options.get("visible");
                    if ( temp.equalsIgnoreCase("false") ) {
                        channel.setVisible(false);
                        sendResourceMessage(sender, PREINFO,
                                "cmdmsgOption", "visible", "false");
                        setOption = true;
                    } else if ( temp.equalsIgnoreCase("true") ) {
                        channel.setVisible(true);
                        sendResourceMessage(sender, PREINFO,
                                "cmdmsgOption", "visible", "true");
                        setOption = true;
                    }
                }
            }
        }

        if ( !setOption ) {
            sendResourceMessage(sender, PREERR, "errmsgInvalidOptions");
        } else {
            channel.save();
        }

        return true;
    }

}
