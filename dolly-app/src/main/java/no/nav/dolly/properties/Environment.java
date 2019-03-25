package no.nav.dolly.properties;

public enum Environment {
    PREPROD, TEST;

    public static Environment convertEnv(String environment) {
        return environment.contains("q") ? PREPROD : TEST;
    }
}
