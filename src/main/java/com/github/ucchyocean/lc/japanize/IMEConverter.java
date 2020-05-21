/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2020
 */
package com.github.ucchyocean.lc.japanize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.common.io.CharStreams;

/**
 * ひらがなのみの文章を、IMEを使用して変換します。
 * 使用される変換候補は全て第1候補のため、正しくない結果が含まれることもよくあります。
 * @author ucchy
 */
public class IMEConverter {

    private static final String SOCIAL_IME_URL =
        "https://www.social-ime.com/api/?string=";
    private static final String GOOGLE_IME_URL =
        "https://www.google.com/transliterate?langpair=ja-Hira|ja&text=";

    /**
     * GoogleIMEを使って変換する
     * @param org 変換元
     * @return 変換後
     */
    public static String convByGoogleIME(String org) {
        return conv(org, true);
    }

    /**
     * SocialIMEを使って変換する
     * @param org 変換元
     * @return 変換後
     * @deprecated SocialIMEが2016年9月1日にサービス終了するため、このAPIは今後呼び出してはならない。
     */
    @Deprecated
    public static String convBySocialIME(String org) {
        return conv(org, false);
    }

    // 変換の実行
    private static String conv(String org, boolean isGoogleIME) {

        if ( org.length() == 0 ) {
            return "";
        }

        HttpURLConnection urlconn = null;
        BufferedReader reader = null;
        try {
            String baseurl;
            String encode;
            if ( isGoogleIME ) {
                baseurl = GOOGLE_IME_URL + URLEncoder.encode(org , "UTF-8");
                encode = "UTF-8";
            } else {
                baseurl = SOCIAL_IME_URL + URLEncoder.encode(org , "UTF-8");
                encode = "EUC_JP";
            }
            URL url = new URL(baseurl);

            urlconn = (HttpURLConnection)url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.connect();

            reader = new BufferedReader(
                    new InputStreamReader(urlconn.getInputStream(), encode));

            String json = CharStreams.toString(reader);
            String parsed = GoogleIME.parseJson(json);
//            if ( !Utility.isCB19orLater() ) {
//                parsed = YukiKanaConverter.fixBrackets(parsed);
//            }

            return parsed;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( urlconn != null ) {
                urlconn.disconnect();
            }
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) { // do nothing.
                }
            }
        }

        return "";
    }

    // デバッグ用エントリ
    public static void main(String[] args) {
        String testee = "sonnnakotohanak(ry)";
        System.out.println("original : " + testee);
        System.out.println("kana : " + YukiKanaConverter.conv(testee));
        System.out.println("GoogleIME : " + convByGoogleIME(YukiKanaConverter.conv(testee)));
        System.out.println("SocialIME : " + convBySocialIME(YukiKanaConverter.conv(testee)));
    }
}
