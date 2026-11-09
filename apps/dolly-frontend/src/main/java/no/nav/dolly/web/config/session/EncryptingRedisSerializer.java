package no.nav.dolly.web.config.session;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.util.Objects.isNull;

public class EncryptingRedisSerializer implements RedisSerializer<Object> {

    static final byte FORMAT_VERSION = 1;

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int HEADER_LENGTH_BYTES = 1;
    private static final int MIN_LENGTH_BYTES = HEADER_LENGTH_BYTES + IV_LENGTH_BYTES + TAG_LENGTH_BITS / 8;
    private static final byte[] EMPTY = new byte[0];

    private final RedisSerializer<Object> delegate;
    private final SecretKey key;
    private final byte[] context;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptingRedisSerializer(RedisSerializer<Object> delegate, SecretKey key, String context) {
        this.delegate = delegate;
        this.key = key;
        this.context = context.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] serialize(Object value) {
        if (isNull(value)) {
            return EMPTY;
        }
        var plaintext = delegate.serialize(value);
        try {
            return encrypt(plaintext);
        } catch (GeneralSecurityException e) {
            throw new SerializationException("Kryptering av sesjonsverdi feilet", e);
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (isNull(bytes) || bytes.length == 0) {
            return null;
        }
        if (bytes.length < MIN_LENGTH_BYTES || bytes[0] != FORMAT_VERSION) {
            return UnreadableSessionValue.UNKNOWN_FORMAT;
        }
        byte[] plaintext;
        try {
            var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(TAG_LENGTH_BITS, bytes, HEADER_LENGTH_BYTES, IV_LENGTH_BYTES));
            cipher.updateAAD(bytes, 0, HEADER_LENGTH_BYTES);
            cipher.updateAAD(context);
            var ciphertextOffset = HEADER_LENGTH_BYTES + IV_LENGTH_BYTES;
            plaintext = cipher.doFinal(bytes, ciphertextOffset, bytes.length - ciphertextOffset);
        } catch (GeneralSecurityException e) {
            return UnreadableSessionValue.DECRYPTION_FAILED;
        }
        try {
            return delegate.deserialize(plaintext);
        } catch (SerializationException e) {
            return UnreadableSessionValue.DESERIALIZATION_FAILED;
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    private byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        var header = new byte[]{FORMAT_VERSION};
        var iv = new byte[IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);

        var cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
        cipher.updateAAD(header);
        cipher.updateAAD(context);
        var ciphertext = cipher.doFinal(plaintext);

        return ByteBuffer
                .allocate(header.length + iv.length + ciphertext.length)
                .put(header)
                .put(iv)
                .put(ciphertext)
                .array();
    }
}
