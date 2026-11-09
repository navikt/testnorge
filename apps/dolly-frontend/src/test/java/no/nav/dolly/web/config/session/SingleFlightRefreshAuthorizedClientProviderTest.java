package no.nav.dolly.web.config.session;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class SingleFlightRefreshAuthorizedClientProviderTest {

    private static final Instant NOW = Instant.parse("2026-11-04T12:00:00Z");
    private static final Duration CLOCK_SKEW = Duration.ofSeconds(30);
    private static final Duration RETENTION = Duration.ofMinutes(10);

    private final ClientRegistration registration = ClientRegistration
            .withRegistrationId("idporten")
            .clientId("dolly-idporten")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://idporten.test/authorize")
            .tokenUri("https://idporten.test/token")
            .build();
    private final AtomicLong nanos = new AtomicLong();
    private final AtomicInteger refreshCount = new AtomicInteger();
    private final AtomicReference<Instant> now = new AtomicReference<>(NOW);
    private final List<String> usedRefreshTokens = new CopyOnWriteArrayList<>();
    private final List<String> persistedTokens = new CopyOnWriteArrayList<>();
    private ReactiveOAuth2AuthorizationSuccessHandler refreshedClientHandler = (client, principal, attributes) ->
            Mono.fromRunnable(() -> persistedTokens.add(client.getAccessToken().getTokenValue()));

    @Test
    void shouldRefreshOnceForConcurrentRequestsInSameSession() {
        var provider = provider(context -> Mono.delay(Duration.ofMillis(100)).map(ignored -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        var first = provider.authorize(context(expiring, session));
        var second = provider.authorize(context(expiring, session));

        StepVerifier.create(Mono.zip(first, second))
                .assertNext(results -> {
                    assertThat(results.getT1().getAccessToken().getTokenValue()).isEqualTo("nytt-token-1");
                    assertThat(results.getT2().getAccessToken().getTokenValue()).isEqualTo("nytt-token-1");
                })
                .verifyComplete();
        assertThat(refreshCount).hasValue(1);
        assertThat(persistedTokens).containsExactly("nytt-token-1");
    }

    @Test
    void shouldPersistRefreshedClientWhenRequestIsCancelled() throws InterruptedException {
        var persisted = new CountDownLatch(1);
        refreshedClientHandler = (client, principal, attributes) -> Mono.fromRunnable(() -> {
            persistedTokens.add(client.getAccessToken().getTokenValue());
            persisted.countDown();
        });
        var provider = provider(context -> Mono.delay(Duration.ofMillis(100)).map(ignored -> refreshed(context)));
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        StepVerifier.create(provider.authorize(context(expiring, new MockWebSession())))
                .thenAwait(Duration.ofMillis(10))
                .thenCancel()
                .verify();

        assertThat(persisted.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(persistedTokens).containsExactly("nytt-token-1");
    }

    @Test
    void shouldReturnRefreshedClientWhenPersistingFails() {
        refreshedClientHandler = (client, principal, attributes) -> Mono.error(new IllegalStateException("Valkey er nede"));
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        StepVerifier.create(provider.authorize(context(expiring, new MockWebSession())))
                .assertNext(client -> assertThat(client.getAccessToken().getTokenValue()).isEqualTo("nytt-token-1"))
                .verifyComplete();
    }

    @Test
    void shouldGiveRefreshedClientToStaleRequestWhileItIsValid() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        var first = provider.authorize(context(expiring, session)).block();
        nanos.addAndGet(Duration.ofSeconds(30).toNanos());
        var second = provider.authorize(context(expiring, session)).block();

        assertThat(second.getAccessToken().getTokenValue()).isEqualTo(first.getAccessToken().getTokenValue());
        assertThat(refreshCount).hasValue(1);
    }

    @Test
    void shouldRefreshFromSessionAfterRetention() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        provider.authorize(context(expiring, session)).block();
        nanos.addAndGet(RETENTION.plusSeconds(1).toNanos());
        var second = provider.authorize(context(expiring, session)).block();

        assertThat(second.getAccessToken().getTokenValue()).isEqualTo("nytt-token-2");
        assertThat(usedRefreshTokens).containsExactly("refresh-gammelt-token", "refresh-gammelt-token");
    }

    @Test
    void shouldRefreshWithLatestRefreshTokenWhenSessionWasNeverUpdated() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        provider.authorize(context(expiring, session)).block();
        now.set(NOW.plus(Duration.ofMinutes(3)));
        var later = provider.authorize(context(expiring, session)).block();

        assertThat(later.getAccessToken().getTokenValue()).isEqualTo("nytt-token-2");
        assertThat(usedRefreshTokens).containsExactly("refresh-gammelt-token", "refresh-nytt-token-1");
    }

    @Test
    void shouldRefreshFromSessionWhenSessionIsNewerThanCache() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        provider.authorize(context(expiring, session)).block();
        now.set(NOW.plus(Duration.ofMinutes(3)));
        var newerInSession = new OAuth2AuthorizedClient(
                registration,
                "bruker-a",
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "annen-pod-token",
                        NOW.plus(Duration.ofMinutes(1)), NOW.plus(Duration.ofMinutes(3)).plusSeconds(10)),
                new OAuth2RefreshToken("refresh-annen-pod-token", NOW.plus(Duration.ofMinutes(1))));
        provider.authorize(context(newerInSession, session)).block();

        assertThat(usedRefreshTokens).containsExactly("refresh-gammelt-token", "refresh-annen-pod-token");
    }

    @Test
    void shouldNotShareRefreshBetweenSessions() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        var first = provider.authorize(context(expiring, new MockWebSession())).block();
        var second = provider.authorize(context(expiring, new MockWebSession())).block();

        assertThat(first.getAccessToken().getTokenValue()).isNotEqualTo(second.getAccessToken().getTokenValue());
        assertThat(refreshCount).hasValue(2);
    }

    @Test
    void shouldNotShareRefreshBetweenPrincipalsInSameSession() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        var first = provider.authorize(context(expiring, session, "bruker-a")).block();
        var second = provider.authorize(context(expiring, session, "bruker-b")).block();

        assertThat(first.getPrincipalName()).isEqualTo("bruker-a");
        assertThat(second.getPrincipalName()).isEqualTo("bruker-b");
        assertThat(refreshCount).hasValue(2);
    }

    @Test
    void shouldNotUseSingleFlightWhenTokenIsStillValid() {
        var delegateCalls = new AtomicInteger();
        var provider = provider(context -> {
            delegateCalls.incrementAndGet();
            return Mono.empty();
        });
        var session = new MockWebSession();
        var valid = client("gyldig-token", NOW.plusSeconds(120));

        StepVerifier.create(provider.authorize(context(valid, session))).verifyComplete();
        StepVerifier.create(provider.authorize(context(valid, session))).verifyComplete();

        assertThat(delegateCalls).hasValue(2);
        assertThat(refreshCount).hasValue(0);
    }

    @Test
    void shouldNotKeepFailedRefresh() {
        var attempts = new AtomicInteger();
        var provider = provider(context -> attempts.incrementAndGet() == 1
                ? Mono.error(new ClientAuthorizationException(new OAuth2Error("invalid_grant"), "idporten"))
                : Mono.fromSupplier(() -> refreshed(context)));
        var session = new MockWebSession();
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        StepVerifier.create(provider.authorize(context(expiring, session)))
                .expectError(ClientAuthorizationException.class)
                .verify();
        StepVerifier.create(provider.authorize(context(expiring, session)))
                .assertNext(client -> assertThat(client.getAccessToken().getTokenValue()).isEqualTo("nytt-token-1"))
                .verifyComplete();
    }

    @Test
    void shouldStopAfterBoundedAttemptsWhenRefreshedTokensExpireImmediately() {
        var provider = provider(context -> Mono.fromSupplier(() -> refreshed(context, NOW.plusSeconds(20))));
        var expiring = client("gammelt-token", NOW.plusSeconds(10));

        StepVerifier.create(provider.authorize(context(expiring, new MockWebSession()))).verifyComplete();

        assertThat(refreshCount).hasValue(3);
    }

    private SingleFlightRefreshAuthorizedClientProvider provider(ReactiveOAuth2AuthorizedClientProvider delegate) {
        return new SingleFlightRefreshAuthorizedClientProvider(
                delegate, refreshedClientHandler, CLOCK_SKEW, RETENTION, clock(), nanos::get);
    }

    private OAuth2AuthorizedClient refreshed(OAuth2AuthorizationContext context) {
        return refreshed(context, NOW.plusSeconds(120));
    }

    private OAuth2AuthorizedClient refreshed(OAuth2AuthorizationContext context, Instant expiresAt) {
        usedRefreshTokens.add(context.getAuthorizedClient().getRefreshToken().getTokenValue());
        var tokenValue = "nytt-token-" + refreshCount.incrementAndGet();
        var issuedAt = now.get();
        return new OAuth2AuthorizedClient(
                registration,
                context.getPrincipal().getName(),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, issuedAt,
                        expiresAt.plus(Duration.between(NOW, issuedAt))),
                new OAuth2RefreshToken("refresh-" + tokenValue, issuedAt));
    }

    private Clock clock() {
        return new Clock() {
            @Override
            public ZoneOffset getZone() {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                return now.get();
            }
        };
    }

    private OAuth2AuthorizedClient client(String tokenValue, Instant expiresAt) {
        return new OAuth2AuthorizedClient(
                registration,
                "bruker-a",
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, NOW.minusSeconds(110), expiresAt),
                new OAuth2RefreshToken("refresh-" + tokenValue, NOW.minusSeconds(110)));
    }

    private OAuth2AuthorizationContext context(OAuth2AuthorizedClient client, WebSession session) {
        return context(client, session, "bruker-a");
    }

    private OAuth2AuthorizationContext context(OAuth2AuthorizedClient client, WebSession session, String principalName) {
        var exchange = MockServerWebExchange.builder(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"))
                .session(session)
                .build();
        return OAuth2AuthorizationContext
                .withAuthorizedClient(client)
                .principal(new TestingAuthenticationToken(principalName, null))
                .attribute(ServerWebExchange.class.getName(), exchange)
                .build();
    }
}
