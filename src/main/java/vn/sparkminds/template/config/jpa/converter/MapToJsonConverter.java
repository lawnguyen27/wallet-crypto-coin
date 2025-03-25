package vn.sparkminds.template.config.jpa.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.sparkminds.template.util.JsonUtils;
import vn.sparkminds.template.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class MapToJsonConverter implements AttributeConverter<Map<String, String>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, String> stringStringMap) {
        return JsonUtils.toJsonOrThrow(stringStringMap);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        if (StringUtils.isBlank(s)) {
            return new HashMap<>();
        }

        return JsonUtils.fromJson(s, new TypeReference<Map<String, String>>() {});
    }
}
