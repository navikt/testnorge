package no.nav.dolly.web.config.session;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class SessionEncryptionKey {

    static final int KEY_LENGTH_BYTES = 32;

    private static final String KEY_NAME = "SESSION_ENCRYPTION_KEY";

    private SessionEncryptionKey() {
    }

    public static SecretKey fromBase64(String base64Key) {
        if (isBlank(base64Key)) {
            throw new IllegalStateException(KEY_NAME + " mangler. Sesjoner skal ikke lagres ukryptert.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(KEY_NAME + " er ikke gyldig base64.");
        }
        if (keyBytes.length != KEY_LENGTH_BYTES) {
            throw new IllegalStateException(KEY_NAME + " må være nøyaktig " + KEY_LENGTH_BYTES + " byte etter base64-dekoding.");
        }
        var key = new SecretKeySpec(keyBytes, "AES");
        Arrays.fill(keyBytes, (byte) 0);
        return key;
    }
}
