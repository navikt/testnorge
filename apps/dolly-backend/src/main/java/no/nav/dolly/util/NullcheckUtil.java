package no.nav.dolly.util;

import static java.util.Objects.nonNull;

public final class NullcheckUtil {

    private NullcheckUtil() {
    }

    public static <T> T nullcheckSetDefaultValue(T value, T defaultValue) {
        return nonNull(value) ? value : defaultValue;
    }
}
