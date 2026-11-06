package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;

import static org.assertj.core.api.Assertions.assertThat;

class SignedSessionIdResolverTest {

    private static final String COOKIE = "__Host-SESSION";
    private static final String SESSION_ID = "6f1c2a9e-0d4b-4c8e-9a51-3b7d2e8f1c04";

    private final SignedSessionIdResolver resolver = resolver(EncryptingRedisSerializerTest.randomKey());

    @Test
    void shouldWriteSignedCookieWithDelegateAttributes() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        resolver.setSessionId(exchange, SESSION_ID);

        ResponseCookie cookie = exchange.getResponse().getCookies().getFirst(COOKIE);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).startsWith(SESSION_ID + ".").isNotEqualTo(SESSION_ID + ".");
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Lax");
    }

    @Test
    void shouldResolveSessionIdFromSignedCookie() {
        assertThat(resolver.resolveSessionIds(exchangeWithCookie(resolver.sign(SESSION_ID)))).containsExactly(SESSION_ID);
    }

    @Test
    void shouldRejectUnsignedSessionId() {
        assertThat(resolver.resolveSessionIds(exchangeWithCookie(SESSION_ID))).isEmpty();
    }

    @Test
    void shouldRejectTamperedSignature() {
        var signed = resolver.sign(SESSION_ID);
        var tampered = signed.substring(0, signed.length() - 1) + (signed.endsWith("A") ? "B" : "A");

        assertThat(resolver.resolveSessionIds(exchangeWithCookie(tampered))).isEmpty();
    }

    @Test
    void shouldRejectSessionIdSignedWithAnotherKey() {
        var signedElsewhere = resolver(EncryptingRedisSerializerTest.randomKey()).sign(SESSION_ID);

        assertThat(resolver.resolveSessionIds(exchangeWithCookie(signedElsewhere))).isEmpty();
    }

    @Test
    void shouldRejectSignatureMovedToAnotherSessionId() {
        var signature = resolver.sign(SESSION_ID).substring(SESSION_ID.length());

        assertThat(resolver.resolveSessionIds(exchangeWithCookie("annen-sesjon" + signature))).isEmpty();
    }

    private static MockServerWebExchange exchangeWithCookie(String value) {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/").cookie(new HttpCookie(COOKIE, value)));
    }

    private static SignedSessionIdResolver resolver(String base64Key) {
        var delegate = new CookieWebSessionIdResolver();
        delegate.setCookieName(COOKIE);
        delegate.addCookieInitializer(builder -> builder.secure(true).httpOnly(true).sameSite("Lax"));
        return new SignedSessionIdResolver(delegate, SessionEncryptionKey.fromBase64(base64Key));
    }
}
