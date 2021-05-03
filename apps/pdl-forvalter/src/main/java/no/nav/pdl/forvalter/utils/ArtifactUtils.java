package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;

@UtilityClass
public class ArtifactUtils {

    public static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }
}
