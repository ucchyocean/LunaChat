package com.github.ucchyocean.lc.japanize;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class GoogleIME {

    protected GoogleIME() {
    }

    /**
     * GoogleIMEの最初の変換候補を抽出して結合します
     *
     * @param json 変換元のJson形式の文字列
     * @return 変換後の文字列
     * @since 2.8.10
     */
    public static String parseJson(String json) {
        StringBuilder result = new StringBuilder();
        for ( JsonElement response : new Gson().fromJson(json, JsonArray.class) ) {
            result.append(response.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
        }
        return result.toString();
    }
}
