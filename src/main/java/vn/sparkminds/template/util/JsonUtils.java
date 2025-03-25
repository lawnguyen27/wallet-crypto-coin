package vn.sparkminds.template.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.sparkminds.template.exception.JsonProcessingException;

public final class JsonUtils {
    private JsonUtils() {}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String JSON_EMPTY = "{}";

    public static String toJsonOrThrow(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception exception) {
            throw new JsonProcessingException(exception);
        }
    }

    public static String toJsonOrEmpty(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception exception) {
            return JSON_EMPTY;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception exception) {
            throw new JsonProcessingException(exception);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception exception) {
            throw new JsonProcessingException(exception);
        }
    }
}
