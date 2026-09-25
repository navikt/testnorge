package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;
import java.util.regex.Pattern;

public record SystemId(String value) {

    private static final Pattern VALID_SYSTEM_ID = Pattern.compile("[a-z0-9](?:[a-z0-9-]{0,62}[a-z0-9])?");

    public SystemId {
        Objects.requireNonNull(value);
        if (!VALID_SYSTEM_ID.matcher(value).matches()) {
            throw new IllegalArgumentException("System ID is invalid.");
        }
    }

    public static SystemId from(String value) {
        return new SystemId(value);
    }
}
