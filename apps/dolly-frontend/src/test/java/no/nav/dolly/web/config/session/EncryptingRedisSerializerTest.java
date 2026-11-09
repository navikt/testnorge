package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EncryptingRedisSerializerTest {

    private static final String TEST_FNR = "12345678901";
    private static final String CONTEXT = "dolly-idporten";

    private final String currentKey = randomKey();
    private final String otherKey = randomKey();

    @Test
    void shouldRoundTripValue() {
        var serializer = serializer(currentKey, CONTEXT);
        var value = new HashMap<>(Map.of("pid", TEST_FNR));

        var deserialized = serializer.deserialize(serializer.serialize(value));

        assertThat(deserialized).isEqualTo(value);
    }

    @Test
    void shouldNotContainPlaintext() {
        var serializer = serializer(currentKey, CONTEXT);

        var bytes = serializer.serialize("pid=" + TEST_FNR);

        assertThat(new String(bytes, StandardCharsets.ISO_8859_1)).doesNotContain(TEST_FNR);
        assertThat(bytes[0]).isEqualTo(EncryptingRedisSerializer.FORMAT_VERSION);
    }

    @Test
    void shouldUseNewIvForEveryWrite() {
        var serializer = serializer(currentKey, CONTEXT);

        assertThat(serializer.serialize(TEST_FNR)).isNotEqualTo(serializer.serialize(TEST_FNR));
    }

    @Test
    void shouldRejectTamperedCiphertext() {
        var serializer = serializer(currentKey, CONTEXT);
        var bytes = serializer.serialize(TEST_FNR);
        bytes[bytes.length - 1] ^= 1;

        assertThat(serializer.deserialize(bytes)).isEqualTo(UnreadableSessionValue.DECRYPTION_FAILED);
    }

    @Test
    void shouldRejectTamperedIv() {
        var serializer = serializer(currentKey, CONTEXT);
        var bytes = serializer.serialize(TEST_FNR);
        bytes[1] ^= 1;

        assertThat(serializer.deserialize(bytes)).isEqualTo(UnreadableSessionValue.DECRYPTION_FAILED);
    }

    @Test
    void shouldRejectUnknownKey() {
        var bytes = serializer(otherKey, CONTEXT).serialize(TEST_FNR);

        assertThat(serializer(currentKey, CONTEXT).deserialize(bytes))
                .isEqualTo(UnreadableSessionValue.DECRYPTION_FAILED);
    }

    @Test
    void shouldRejectValueFromOtherContext() {
        var bytes = serializer(currentKey, "dolly-frontend").serialize(TEST_FNR);

        assertThat(serializer(currentKey, CONTEXT).deserialize(bytes))
                .isEqualTo(UnreadableSessionValue.DECRYPTION_FAILED);
    }

    @Test
    void shouldRejectLegacyPlaintextValue() {
        var legacy = new JdkSerializationRedisSerializer().serialize(TEST_FNR);

        assertThat(serializer(currentKey, CONTEXT).deserialize(legacy))
                .isEqualTo(UnreadableSessionValue.UNKNOWN_FORMAT);
    }

    @Test
    void shouldHandleNullAndEmpty() {
        var serializer = serializer(currentKey, CONTEXT);

        assertThat(serializer.serialize(null)).isEmpty();
        assertThat(serializer.deserialize(null)).isNull();
        assertThat(serializer.deserialize(new byte[0])).isNull();
    }

    @Test
    void shouldFailWhenKeyIsMissing() {
        assertThatThrownBy(() -> SessionEncryptionKey.fromBase64(" "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SESSION_ENCRYPTION_KEY mangler");
    }

    @Test
    void shouldFailWhenKeyHasWrongLength() {
        var shortKey = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> SessionEncryptionKey.fromBase64(shortKey))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 byte")
                .hasMessageNotContaining(shortKey);
    }

    @Test
    void shouldFailWhenKeyIsNotBase64() {
        assertThatThrownBy(() -> SessionEncryptionKey.fromBase64("ikke-base64!"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("base64");
    }

    static String randomKey() {
        var bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static EncryptingRedisSerializer serializer(String key, String context) {
        return new EncryptingRedisSerializer(
                new JdkSerializationRedisSerializer(),
                SessionEncryptionKey.fromBase64(key),
                context);
    }
}
