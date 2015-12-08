/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2013
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

/**
 * ひらがなのみの文章を、IMEを使用して変換します。
 * 使用される変換候補は全て第1候補のため、正しくない結果が含まれることもよくあります。
 * @author ucchy
 */
public class IMEConverter {

    private static final String SOCIAL_IME_URL =
        "http://www.social-ime.com/api/?string=";
    private static final String GOOGLE_IME_URL =
        "http://www.google.com/transliterate?langpair=ja-Hira|ja&text=";

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
     */
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
            String line = "";
            StringBuilder result = new StringBuilder();
            while ( (line = reader.readLine()) != null ) {
                if ( isGoogleIME ) {
                    result.append(parseGoogleIMEResult(line));
                } else {
                    result.append(pickFirstElement(line));
                }
            }

            return result.toString();

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

    private static String pickFirstElement(String org) {
        int index = org.indexOf("\t");
        if ( index == -1 ) {
            return org;
        } else {
            return org.substring(0, index);
        }
    }

    private static String parseGoogleIMEResult(String result) {
        StringBuilder buf = new StringBuilder();
        int level = 0;
        int index = 0;
        while ( index < result.length() ) {
            if ( level < 3 ) {
                int nextStart = result.indexOf("[", index);
                int nextEnd = result.indexOf("]", index);
                if ( nextStart == -1 ) {
                    return buf.toString();
                } else {
                    if ( nextStart < nextEnd ) {
                        level++;
                        index = nextStart+1;
                    } else {
                        level--;
                        index = nextEnd+1;
                    }
                }
            } else {
                int start = result.indexOf("\"", index);
                int end = result.indexOf("\"", start+1);
                if ( start == -1 || end == -1 ) {
                    return buf.toString();
                }
                buf.append(result.substring(start+1, end));
                int next = result.indexOf("]", end);
                if ( next == -1 ) {
                    return buf.toString();
                } else {
                    level--;
                    index = next+1;
                }
            }
        }
        return buf.toString();
    }

    // デバッグ用エントリ
    public static void main(String[] args) {
        String testee = "\"";
        System.out.println("kana : " + KanaConverter.conv(testee));
        System.out.println("GoogleIME : " + convByGoogleIME(KanaConverter.conv(testee)));
        System.out.println("SocialIME : " + convBySocialIME(KanaConverter.conv(testee)));
    }
}
