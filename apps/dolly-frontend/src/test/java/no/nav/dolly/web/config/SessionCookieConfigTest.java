package no.nav.dolly.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SessionCookieConfigTest {

    private static final String COOKIE = "server.reactive.session.cookie.";

    @Test
    void shouldUseHostPrefixedSecureCookieInNais() throws IOException {
        var properties = load("application.yml");

        assertThat(properties.getProperty(COOKIE + "name")).isEqualTo("__Host-SESSION");
        assertThat(properties.getProperty(COOKIE + "secure")).isEqualTo(true);
        assertThat(properties.getProperty(COOKIE + "http-only")).isEqualTo(true);
        assertThat(properties.getProperty(COOKIE + "same-site")).isEqualTo("lax");
    }

    @Test
    void shouldNotOverrideCookieInIdportenOrDev() throws IOException {
        assertThat(load("application-idporten.yml").getProperty(COOKIE + "name")).isNull();
        assertThat(load("application-idporten.yml").getProperty(COOKIE + "secure")).isNull();
        assertThat(load("application-dev.yml").getProperty(COOKIE + "name")).isNull();
        assertThat(load("application-dev.yml").getProperty(COOKIE + "secure")).isNull();
    }

    @Test
    void shouldUsePlainCookieLocallyOverHttp() throws IOException {
        var properties = load("application-local.yml");

        assertThat(properties.getProperty(COOKIE + "name")).isEqualTo("SESSION");
        assertThat(properties.getProperty(COOKIE + "secure")).isEqualTo(false);
    }

    private static PropertySource<?> load(String file) throws IOException {
        return new YamlPropertySourceLoader().load(file, new ClassPathResource(file)).getFirst();
    }
}
