package no.nav.dolly.web.config.filters;

@FunctionalInterface
public interface GenerateToken {
    String getToken();
}