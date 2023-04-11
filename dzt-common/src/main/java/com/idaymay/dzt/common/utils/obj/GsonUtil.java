package com.idaymay.dzt.common.utils.obj;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonUtil {

    private static Gson gson = new Gson().newBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> c) {
        return gson.fromJson(json, c);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    public static JSONObject fromJson(String json) {
        Map<String, ?> data = gson.fromJson(json, Map.class);
        return new JSONObject(data);
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> c) {
        List<T> list = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, c));
        }
        return list;
    }

    public static JSONArray fromJsonArray(String json) {
        return gson.fromJson(json, JSONArray.class);
    }
}
