package no.nav.dolly.util;

public final class NullcheckUtil {

    private NullcheckUtil() {}

    public static <T> T nullcheckSetDefaultValue(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
