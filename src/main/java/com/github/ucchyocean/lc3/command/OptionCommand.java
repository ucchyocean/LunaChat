/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.ucchyocean.lc3.LunaChat;
import com.github.ucchyocean.lc3.Messages;
import com.github.ucchyocean.lc3.Utility;
import com.github.ucchyocean.lc3.channel.Channel;
import com.github.ucchyocean.lc3.event.EventResult;
import com.github.ucchyocean.lc3.japanize.JapanizeType;
import com.github.ucchyocean.lc3.member.ChannelMember;

/**
 * optionコマンドの実行クラス
 * @author ucchy
 */
public class OptionCommand extends LunaChatSubCommand {

    private static final int MAX_LENGTH_DESCRIPTION = 30;
    private static final int MAX_LENGTH_ALIAS = 15;
    private static final int MAX_LENGTH_PASSWORD = 15;

    private static final String COMMAND_NAME = "option";
    private static final String PERMISSION_NODE = "lunachat." + COMMAND_NAME;

    /**
     * コマンドを取得します。
     * @return コマンド
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getCommandName()
     */
    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * パーミッションノードを取得します。
     * @return パーミッションノード
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getPermissionNode()
     */
    @Override
    public String getPermissionNode() {
        return PERMISSION_NODE;
    }

    /**
     * コマンドの種別を取得します。
     * @return コマンド種別
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#getCommandType()
     */
    @Override
    public CommandType getCommandType() {
        return CommandType.MODERATOR;
    }

    /**
     * 使用方法に関するメッセージをsenderに送信します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#sendUsageMessage()
     */
    @Override
    public void sendUsageMessage(
            ChannelMember sender, String label) {
        sender.sendMessage(Messages.usageOption(label));
    }

    /**
     * コマンドを実行します。
     * @param sender コマンド実行者
     * @param label 実行ラベル
     * @param args 実行時の引数
     * @return コマンドが実行されたかどうか
     * @see com.github.ucchyocean.lc3.command.LunaChatSubCommand#runCommand(java.lang.String[])
     */
    @Override
    public boolean runCommand(
            ChannelMember sender, String label, String[] args) {

        // 引数チェック
        // このコマンドは、デフォルトチャンネルでない人も実行できるが、その場合はチャンネル名を指定する必要がある
        ArrayList<String> optionsTemp = new ArrayList<String>();
        String cname = null;
        if ( args.length >= 2 ) {
            Channel def = api.getDefaultChannel(sender.getName());
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
            sender.sendMessage(Messages.errmsgCommand());
            return true;
        }

        Channel channel = api.getChannel(cname);

        // チャンネルが存在するかどうかをチェックする
        if ( channel == null ) {
            sender.sendMessage(Messages.errmsgNotExist());
            return true;
        }

        cname = channel.getName();

        // モデレーターかどうか確認する
        if ( !channel.hasModeratorPermission(sender) ) {
            sender.sendMessage(Messages.errmsgNotModerator());
            return true;
        }

        // 指定内容を解析する
        Map<String, String> options = new HashMap<String, String>();
        for ( String t : optionsTemp ) {
            int index = t.indexOf("=");
            if ( index == -1 ) {
                continue;
            }
            options.put(t.substring(0, index), t.substring(index + 1));
        }

        // LunaChatChannelOptionChangedEvent イベントコール
        EventResult result = LunaChat.getEventSender().sendLunaChatChannelOptionChangedEvent(
                cname, sender, options);
        if ( result.isCancelled() ) {
            return true;
        }
        options = result.getOptions();

        // 設定する
        boolean setOption = false;

        if ( options.containsKey("description") ) {
            // チャンネル説明文

            String pnode = PERMISSION_NODE + ".description";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String desc = options.get("description");
                // チャンネル説明文は最大文字長を超えていないか確認
                if ( desc.length() > MAX_LENGTH_DESCRIPTION ) {
                    sender.sendMessage(Messages.errmsgToolongDescription(MAX_LENGTH_DESCRIPTION));
                } else {
                    channel.setDescription(desc);
                    sender.sendMessage(Messages.cmdmsgOption("description", desc));
                    setOption = true;
                }
            }
        }

        if ( options.containsKey("alias") ) {
            // チャンネル別名

            String pnode = PERMISSION_NODE + ".alias";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String alias = options.get("alias");
                if ( alias.length() > MAX_LENGTH_ALIAS ) {
                    // チャンネル別名が最大文字長を超えている
                    sender.sendMessage(Messages.errmsgToolongAlias(MAX_LENGTH_ALIAS));

                } else if (api.getChannel(alias) != null) {
                    // 別のチャンネル名またはチャンネル別名と重複する
                    sender.sendMessage(Messages.errmsgDuplicatedAlias(
                            alias, api.getChannel(alias).getName()));

                } else {
                    channel.setAlias(alias);
                    sender.sendMessage(Messages.cmdmsgOption("alias", alias));
                    setOption = true;
                }
            }
        }

        if ( options.containsKey("color") ) {
            // チャンネルカラー

            String pnode = PERMISSION_NODE + ".color";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String code = options.get("color");

                if ( Utility.isValidColor(code) ) {
                    code = Utility.changeToColorCode(code);
                }
                if ( Utility.isAltColorCode(code) ) {
                    channel.setColorCode(code);
                    sender.sendMessage(Messages.cmdmsgOption("color", options.get("color")));
                    setOption = true;
                } else {
                    sender.sendMessage(Messages.errmsgInvalidColorCode(options.get("color")));
                }
            }
        }

        if ( options.containsKey("broadcast") ) {
            // ブロードキャストチャンネル

            String pnode = PERMISSION_NODE + ".broadcast";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String value = options.get("broadcast");

                if ( value.equals("") || value.equalsIgnoreCase("false") ) {
                    if ( channel.isGlobalChannel() ) {
                        sender.sendMessage(Messages.errmsgCannotOffGlobalBroadcast());
                    } else {
                        channel.setBroadcast(false);
                        sender.sendMessage(Messages.cmdmsgOption("broadcast", "false"));
                        setOption = true;
                    }
                } else if ( value.equalsIgnoreCase("true") ) {
                    channel.setBroadcast(true);
                    sender.sendMessage(Messages.cmdmsgOption("broadcast", "true"));
                    setOption = true;
                } else {
                    sender.sendMessage(Messages.errmsgInvalidBooleanOption("broadcast"));
                }
            }
        }

        if ( options.containsKey("range") ) {
            // レンジ

            String pnode = PERMISSION_NODE + ".range";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String value = options.get("range");

                if ( value.equals("") ) {
                    channel.setWorldRange(false);
                    channel.setChatRange(0);
                    sender.sendMessage(Messages.cmdmsgOption("range", "off"));
                    setOption = true;
                } else if ( value.equalsIgnoreCase("world") ) {
                    channel.setWorldRange(true);
                    channel.setChatRange(0);
                    sender.sendMessage(Messages.cmdmsgOption("range", "world"));
                    setOption = true;
                } else if ( value.matches("[0-9]+") ) {
                    channel.setWorldRange(true);
                    channel.setChatRange(Integer.parseInt(value));
                    sender.sendMessage(Messages.cmdmsgOption("range", value));
                    setOption = true;
                } else {
                    sender.sendMessage(Messages.errmsgInvalidRangeOption());
                }
            }
        }

        if ( options.containsKey("allowcc") ) {
            // カラーコード使用可否

            String pnode = PERMISSION_NODE + ".allowcc";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String value = options.get("allowcc");

                if ( value.equals("") || value.equalsIgnoreCase("false") ) {
                    channel.setAllowCC(false);
                    sender.sendMessage(Messages.cmdmsgOption("allowcc", "false"));
                    setOption = true;
                } else if ( value.equalsIgnoreCase("true") ) {
                    channel.setAllowCC(true);
                    sender.sendMessage(Messages.cmdmsgOption("allowcc", "true"));
                    setOption = true;
                } else {
                    sender.sendMessage(Messages.errmsgInvalidBooleanOption("allowcc"));
                }
            }
        }

        if ( options.containsKey("japanize") ) {
            // Japanize変換設定

            String pnode = PERMISSION_NODE + ".japanize";
            if ( !sender.hasPermission(pnode) ) {
                sender.sendMessage(Messages.errmsgNotPermission(pnode));
            } else {

                String value = options.get("japanize");

                if ( value.equals("") ) {
                    channel.setJapanizeType(null);
                    sender.sendMessage(Messages.cmdmsgOption("japanize", "default"));
                    setOption = true;
                } else {
                    JapanizeType type = JapanizeType.fromID(value, null);
                    if ( type == null ) {
                        sender.sendMessage(Messages.errmsgCommand());
                    } else {
                        channel.setJapanizeType(type);
                        sender.sendMessage(Messages.cmdmsgOption("japanize", type.toString()));
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
                    sender.sendMessage(Messages.errmsgNotPermission(pnode));
                } else {

                    String password = options.get("password");
                    // パスワードが文字制限を超える場合はエラー
                    if ( password.length() > MAX_LENGTH_PASSWORD ) {
                        sender.sendMessage(Messages.errmsgToolongPassword(MAX_LENGTH_PASSWORD));
                    } else {
                        channel.setPassword(password);
                        sender.sendMessage(Messages.cmdmsgOption("password", password));
                        setOption = true;
                    }
                }
            }

            if ( options.containsKey("visible") ) {
                // ビジブル

                String pnode = PERMISSION_NODE + ".visible";
                if ( !sender.hasPermission(pnode) ) {
                    sender.sendMessage(Messages.errmsgNotPermission(pnode));
                } else {

                    String temp = options.get("visible");
                    if ( temp.equalsIgnoreCase("false") ) {
                        channel.setVisible(false);
                        sender.sendMessage(Messages.cmdmsgOption("visible", "false"));
                        setOption = true;
                    } else if ( temp.equalsIgnoreCase("true") ) {
                        channel.setVisible(true);
                        sender.sendMessage(Messages.cmdmsgOption("visible", "true"));
                        setOption = true;
                    } else {
                        sender.sendMessage(Messages.errmsgInvalidBooleanOption("visible"));
                    }
                }
            }
        }

        if ( !setOption ) {
            sender.sendMessage(Messages.errmsgInvalidOptions());
        } else {
            channel.save();
        }

        return true;
    }

}
