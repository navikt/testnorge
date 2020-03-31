package no.nav.registre.inntekt.security.properties;

public enum Environment {
    PREPROD, TEST;

    public static Environment getEnv(String environment) {
        return environment.contains("q") ? PREPROD : TEST;
    }
}
