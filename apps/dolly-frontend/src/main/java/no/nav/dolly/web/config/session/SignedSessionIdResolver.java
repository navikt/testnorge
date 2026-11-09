package no.nav.dolly.web.config.session;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class SignedSessionIdResolver implements WebSessionIdResolver {

    private static final String ALGORITHM = "HmacSHA256";
    private static final byte[] KEY_LABEL = "dolly-session-cookie".getBytes(StandardCharsets.UTF_8);
    private static final char SEPARATOR = '.';
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final WebSessionIdResolver delegate;
    private final SecretKeySpec signingKey;

    public SignedSessionIdResolver(WebSessionIdResolver delegate, SecretKey sessionEncryptionKey) {
        this.delegate = delegate;
        var derivationKey = new SecretKeySpec(sessionEncryptionKey.getEncoded(), ALGORITHM);
        this.signingKey = new SecretKeySpec(hmac(derivationKey, KEY_LABEL), ALGORITHM);
    }

    @Override
    public List<String> resolveSessionIds(ServerWebExchange exchange) {
        return delegate
                .resolveSessionIds(exchange)
                .stream()
                .map(this::verify)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void setSessionId(ServerWebExchange exchange, String sessionId) {
        delegate.setSessionId(exchange, sign(sessionId));
    }

    @Override
    public void expireSession(ServerWebExchange exchange) {
        delegate.expireSession(exchange);
    }

    String sign(String sessionId) {
        return sessionId + SEPARATOR + ENCODER.encodeToString(hmac(signingKey, sessionId.getBytes(StandardCharsets.UTF_8)));
    }

    Optional<String> verify(String cookieValue) {
        var separator = cookieValue.lastIndexOf(SEPARATOR);
        if (separator < 1) {
            return Optional.empty();
        }
        var sessionId = cookieValue.substring(0, separator);
        var expected = sign(sessionId).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, cookieValue.getBytes(StandardCharsets.UTF_8))
                ? Optional.of(sessionId)
                : Optional.empty();
    }

    private static byte[] hmac(SecretKeySpec key, byte[] data) {
        try {
            var mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 er ikke tilgjengelig.", e);
        }
    }
}
