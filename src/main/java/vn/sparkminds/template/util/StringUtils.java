package vn.sparkminds.template.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringUtils {

    private StringUtils() {}

    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String HYPHEN = "_";
    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final String SPACE = " ";

    public static boolean isBlank(CharSequence cs)
    {
        return org.apache.commons.lang3.StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(CharSequence cs)
    {
        return !isBlank(cs);
    }

    public static String defaultIfBlank(String cs, String defaultStr) {
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(cs, defaultStr);
    }

    public static String byteBufferToString(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

}
