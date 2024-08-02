package org.lushplugins.itempools.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
}
