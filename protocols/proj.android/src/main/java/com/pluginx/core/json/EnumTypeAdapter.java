package com.pluginx.core.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class EnumTypeAdapter<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final Class<T> enumClass;

    public EnumTypeAdapter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.ordinal());
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int enumValue = json.getAsInt();
        try {
            return Objects.requireNonNull(enumClass.getEnumConstants())[enumValue];
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid enum value: " + enumValue, e);
        }
    }
}