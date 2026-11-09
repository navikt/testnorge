package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.session.Session;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionStorageConfigTest {

    private static final String TEST_FNR = "12345678901";

    @Test
    void shouldUseEncryptingSerializerForSpringSession() {
        try (var context = context(EncryptingRedisSerializerTest.randomKey())) {
            var repository = context.getBean(ReactiveRedisSessionRepository.class);
            var serializationPair = repository
                    .getSessionRedisOperations()
                    .getSerializationContext()
                    .getHashValueSerializationPair();

            var bytes = serializationPair.write(TEST_FNR);
            var raw = new byte[bytes.remaining()];
            bytes.get(raw);

            assertThat(new String(raw, StandardCharsets.ISO_8859_1)).doesNotContain(TEST_FNR);
            assertThat(raw[0]).isEqualTo(EncryptingRedisSerializer.FORMAT_VERSION);
            assertThat(serializationPair.read(ByteBuffer.wrap(raw))).isEqualTo(TEST_FNR);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "30m", "1800" })
    void shouldUseSessionTimeoutFromConfiguration(String timeout) {
        var properties = new HashMap<String, Object>(baseProperties(EncryptingRedisSerializerTest.randomKey()));
        properties.put("spring.session.timeout", timeout);
        try (var context = context(properties)) {
            Session session = context.getBean(ReactiveRedisSessionRepository.class).createSession().block();

            assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofMinutes(30));
        }
    }

    @Test
    void shouldSignSessionCookie() {
        try (var context = context(baseProperties(EncryptingRedisSerializerTest.randomKey()), CookieResolverConfig.class)) {
            var sessionManager = (DefaultWebSessionManager) context.getBean(WebSessionManager.class);
            assertThat(sessionManager.getSessionIdResolver()).isInstanceOf(SignedSessionIdResolver.class);
        }
    }

    @Test
    void shouldFailStartupWithoutEncryptionKey() {
        assertThatThrownBy(() -> context("").close())
                .isInstanceOf(BeanCreationException.class)
                .hasStackTraceContaining("SESSION_ENCRYPTION_KEY mangler");
    }

    static AnnotationConfigApplicationContext context(String encryptionKey) {
        return context(baseProperties(encryptionKey));
    }

    private static Map<String, Object> baseProperties(String encryptionKey) {
        return Map.of(
                "spring.application.name", "dolly-frontend",
                "spring.data.redis.host", "localhost",
                "spring.data.redis.port", "6379",
                "spring.data.redis.username", "default",
                "spring.data.redis.password", "not_in_use",
                "spring.data.redis.ssl.enabled", "false",
                "SESSION_ENCRYPTION_KEY", encryptionKey);
    }

    static AnnotationConfigApplicationContext context(Map<String, Object> properties, Class<?>... additionalConfig) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("idporten");
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.register(SessionStorageConfig.class);
        if (additionalConfig.length > 0) {
            context.register(additionalConfig);
        }
        context.refresh();
        return context;
    }

    @Configuration
    static class CookieResolverConfig {

        @Bean
        WebSessionIdResolver webSessionIdResolver() {
            return new CookieWebSessionIdResolver();
        }
    }
}
