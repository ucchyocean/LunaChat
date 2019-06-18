package com.github.ucchyocean.lc.japanize;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class GoogleIME {

    protected GoogleIME() {
    }

    public static String parseJson(String json) {
        StringBuilder result = new StringBuilder();
        new Gson().fromJson(json, JsonArray.class).forEach(response -> {
            result.append(response.getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
        });
        return result.toString();
    }
}
