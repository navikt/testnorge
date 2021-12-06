package no.nav.dolly.domain;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public final class CommonKeysAndUtils {

    public static final String MDC_CONSUMER_ID_KEY = "consumerId";
    public static final String MDC_CALL_ID_KEY = "callId";

    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    public static final String CONSUMER = "Dolly";

    private static final String[] SYNTH_ENV = {"q1", "q2", "q4", "t4"};
    public static final Set<String> PDL_TPS_CREATE_ENV = Set.of("q4", "t6");

    public static String getSynthEnv() {

        return String.format("{%s}", Stream.of(SYNTH_ENV).collect(Collectors.joining(",")));
    }

    public static boolean containsSynthEnv(List<String> environments) {

        return Stream.of(SYNTH_ENV)
                .anyMatch(synth -> environments.stream()
                        .anyMatch(env -> env.equals(synth)));
    }

    public static boolean isPdlTpsCreate(List<String> environments) {

        return PDL_TPS_CREATE_ENV.stream()
                .anyMatch(synth -> environments.stream()
                        .anyMatch(env -> env.equals(synth)));
    }
}
