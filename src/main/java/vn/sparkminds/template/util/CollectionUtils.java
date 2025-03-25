package vn.sparkminds.template.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CollectionUtils {

    private CollectionUtils() {}

    public static List<String> convertToListString(Object object) {
        if (Objects.isNull(object) || !(object instanceof List)) {
            return Collections.emptyList();
        }

        return (List<String>) object;
    }
}
