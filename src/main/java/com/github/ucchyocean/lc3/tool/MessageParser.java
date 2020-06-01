/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc3.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.ucchyocean.lc3.YamlConfig;

/**
 * messages_ja.yml を読んで、Messagesクラス用のメソッドを生成するツール
 * @author ucchy
 */
public class MessageParser {

    private static final String FILE_PATH = "src/main/resources/messages_ja.yml";

    public static void main(String[] args) {

        YamlConfig yaml = YamlConfig.load(new File(FILE_PATH));
        for ( String key : yaml.getKeys(false) ) {
            String value = yaml.getString(key);

            ArrayList<String> keywords = new ArrayList<>();

            Pattern pattern = Pattern.compile("%([^%]*)%");
            Matcher matcher = pattern.matcher(value);

            while ( matcher.find() ) {
                keywords.add(matcher.group(1));
            }

            String arguments = "";
            for ( String keyword : keywords ) {
                if ( arguments.length() > 0 ) arguments += ", ";
                arguments += "Object " + keyword;
            }

            // 出力
            System.out.println();
            System.out.println("    /**");
            System.out.println("     * " + value);
            System.out.println("     */");
            System.out.println(String.format(
                    "    public static String %s(%s) {", key, arguments));
            System.out.println(String.format(
                    "        String msg = resources.getString(\"%s\");", key));
            System.out.println(String.format(
                    "        if ( msg == null || msg.equals(\"\") ) return \"\";", key));

            for ( String keyword : keywords ) {
                System.out.println(String.format(
                        "        msg = msg.replace(\"%%%s%%\", %s.toString());", keyword, keyword));
            }
            if ( key.startsWith("errmsg") ) {
                System.out.println("        return resources.getString(\"errorPrefix\", \"\") + msg;");
            } else {
                System.out.println("        return msg;");
            }
            System.out.println("    }");
        }
    }
}
