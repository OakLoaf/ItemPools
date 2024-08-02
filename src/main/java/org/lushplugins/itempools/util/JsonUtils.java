package org.lushplugins.itempools.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static String getStringOrNull(JsonObject jsonObject, String path) {
        JsonElement element = jsonObject.get(path);
        return !element.isJsonNull() ? element.getAsString() : null;
    }

    public static Integer getIntOrNull(JsonObject jsonObject, String path) {
        JsonElement element = jsonObject.get(path);
        return !element.isJsonNull() ? element.getAsInt() : null;
    }

    public static Boolean getBoolOrNull(JsonObject jsonObject, String path) {
        JsonElement element = jsonObject.get(path);
        return !element.isJsonNull() ? element.getAsBoolean() : null;
    }

    public static List<String> getStringListOrNull(JsonObject jsonObject, String path) {
        JsonElement element = jsonObject.get(path);

        List<String> list;
        if (element.isJsonArray()) {
            JsonArray jsonArr = element.getAsJsonArray();
            list = new ArrayList<>();

            for (JsonElement loreElement : jsonArr) {
                list.add(loreElement.getAsString());
            }
        } else {
            list = null;
        }

        return list;
    }

    public static void setStringList(JsonObject jsonObject, String path, @Nullable List<String> list) {
        if (list == null) {
            return;
        }

        JsonArray jsonArr = new JsonArray();
        for (String string : list) {
            jsonArr.add(string);
        }

        jsonObject.add(path, jsonArr);
    }
}
