package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.session.Session;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;

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

    @Test
    void shouldUseSessionTimeoutFromConfiguration() {
        var properties = new HashMap<String, Object>(baseProperties(EncryptingRedisSerializerTest.randomKey()));
        properties.put("spring.session.timeout", "30m");
        try (var context = context(properties)) {
            Session session = context.getBean(ReactiveRedisSessionRepository.class).createSession().block();

            assertThat(session.getMaxInactiveInterval()).isEqualTo(Duration.ofMinutes(30));
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

    static AnnotationConfigApplicationContext context(Map<String, Object> properties) {
        var context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("idporten");
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", properties));
        context.register(SessionStorageConfig.class);
        context.refresh();
        return context;
    }
}
