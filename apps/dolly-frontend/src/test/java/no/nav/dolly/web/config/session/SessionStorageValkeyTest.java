package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.server.session.WebSessionStore;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class SessionStorageValkeyTest {

    private static final String TEST_FNR = "12345678901";
    private static final String TEST_SID = "test-sid-123";
    private static final String PASSWORD = "test-password";
    private static final String AUTHORIZED_CLIENTS_ATTRIBUTE =
            "org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository.AUTHORIZED_CLIENTS";

    @Container
    private static final GenericContainer<?> VALKEY = new GenericContainer<>("valkey/valkey:8-alpine")
            .withCommand("valkey-server", "--requirepass", PASSWORD)
            .withExposedPorts(6379);

    private static AnnotationConfigApplicationContext context;

    @BeforeAll
    static void startContext() {
        context = SessionStorageConfigTest.context(Map.of(
                "spring.application.name", "dolly-frontend",
                "spring.data.redis.host", VALKEY.getHost(),
                "spring.data.redis.port", String.valueOf(VALKEY.getMappedPort(6379)),
                "spring.data.redis.username", "default",
                "spring.data.redis.password", PASSWORD,
                "spring.data.redis.ssl.enabled", "false",
                "SESSION_ENCRYPTION_KEY", EncryptingRedisSerializerTest.randomKey()));
    }

    @AfterAll
    static void stopContext() {
        context.close();
    }

    @Test
    void shouldNotPersistPidOrTokensInPlaintext() {
        var idToken = jwt(Map.of("sub", "pseudonym", "pid", TEST_FNR, "sid", TEST_SID));
        var accessToken = jwt(Map.of("sub", "pseudonym", "pid", TEST_FNR));
        var refreshToken = jwt(Map.of("pid", TEST_FNR));

        var session = sessionStore().createWebSession().block();
        session.getAttributes().put(
                WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                securityContext(idToken));
        session.getAttributes().put(AUTHORIZED_CLIENTS_ATTRIBUTE, authorizedClients(accessToken, refreshToken));
        session.getAttributes().put("SESSION_USER_ID_KEY", TEST_FNR);
        session.save().block();

        var forbidden = new ArrayList<String>();
        forbidden.add(TEST_FNR);
        forbidden.add("eyJ");
        for (var token : List.of(idToken, accessToken, refreshToken)) {
            forbidden.addAll(List.of(token.split("\\.")));
        }

        try (var connection = rawConnection()) {
            var keys = connection.keyCommands().keys("*".getBytes(StandardCharsets.UTF_8));
            assertThat(keys).isNotEmpty();
            for (var key : keys) {
                assertNoForbidden(key, forbidden);
                var hash = connection.hashCommands().hGetAll(key);
                assertThat(hash).isNotEmpty();
                hash.forEach((field, value) -> {
                    assertNoForbidden(field, forbidden);
                    assertNoForbidden(value, forbidden);
                });
            }
        }

        var restored = sessionStore().retrieveSession(session.getId()).block();
        assertThat(restored).isNotNull();
        SecurityContext restoredContext = restored.getAttribute(
                WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
        var user = (OidcUser) restoredContext.getAuthentication().getPrincipal();
        assertThat(user.getIdToken().getClaimAsString("pid")).isEqualTo(TEST_FNR);
        Map<String, OAuth2AuthorizedClient> clients = restored.getAttribute(AUTHORIZED_CLIENTS_ATTRIBUTE);
        assertThat(clients.get("idporten").getRefreshToken().getTokenValue()).isEqualTo(refreshToken);
    }

    @Test
    void shouldDiscardSessionThatCannotBeDecrypted() {
        var session = sessionStore().createWebSession().block();
        session.getAttributes().put("noe", "verdi");
        session.save().block();
        var key = ("spring:session:sessions:" + session.getId()).getBytes(StandardCharsets.UTF_8);

        try (var connection = rawConnection()) {
            connection.hashCommands().hSet(
                    key,
                    "sessionAttr:legacy".getBytes(StandardCharsets.UTF_8),
                    new JdkSerializationRedisSerializer().serialize(TEST_FNR));
        }

        WebSession restored = sessionStore().retrieveSession(session.getId()).block();

        assertThat(restored).isNull();
        try (var connection = rawConnection()) {
            assertThat(connection.keyCommands().exists(key)).isFalse();
        }
    }

    private static WebSessionStore sessionStore() {
        return ((DefaultWebSessionManager) context.getBean(WebSessionManager.class)).getSessionStore();
    }

    private static RedisConnection rawConnection() {
        return context.getBean(LettuceConnectionFactory.class).getConnection();
    }

    private static void assertNoForbidden(byte[] bytes, List<String> forbidden) {
        var text = new String(bytes, StandardCharsets.ISO_8859_1);
        assertThat(forbidden).noneMatch(text::contains);
    }

    private static SecurityContext securityContext(String idTokenValue) {
        var claims = Map.<String, Object>of("sub", "pseudonym", "pid", TEST_FNR, "sid", TEST_SID, "iss", "https://test.idporten.no");
        var idToken = new OidcIdToken(idTokenValue, Instant.now(), Instant.now().plusSeconds(120), claims);
        var authorities = List.of(new SimpleGrantedAuthority("OIDC_USER"));
        var user = new DefaultOidcUser(authorities, idToken);
        return new SecurityContextImpl(new OAuth2AuthenticationToken(user, authorities, "idporten"));
    }

    private static Map<String, OAuth2AuthorizedClient> authorizedClients(String accessToken, String refreshToken) {
        var registration = ClientRegistration
                .withRegistrationId("idporten")
                .clientId("client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://test.idporten.no/authorize")
                .tokenUri("https://test.idporten.no/token")
                .build();
        var client = new OAuth2AuthorizedClient(
                registration,
                "pseudonym",
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, Instant.now(), Instant.now().plusSeconds(120)),
                new OAuth2RefreshToken(refreshToken, Instant.now()));
        var clients = new HashMap<String, OAuth2AuthorizedClient>();
        clients.put("idporten", client);
        return clients;
    }

    private static String jwt(Map<String, String> claims) {
        var encoder = Base64.getUrlEncoder().withoutPadding();
        var payload = new StringBuilder("{");
        claims.forEach((name, value) -> payload.append('"').append(name).append("\":\"").append(value).append("\","));
        payload.setCharAt(payload.length() - 1, '}');
        return encoder.encodeToString("{\"alg\":\"RS256\"}".getBytes(StandardCharsets.UTF_8))
                + "." + encoder.encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8))
                + "." + encoder.encodeToString("signatur".getBytes(StandardCharsets.UTF_8));
    }
}
