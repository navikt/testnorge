package no.nav.dolly.web.config.session;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
public class SingleFlightRefreshAuthorizedClientProvider implements ReactiveOAuth2AuthorizedClientProvider {

    private static final long MAX_ENTRIES = 10_000;
    private static final int MAX_REFRESH_ATTEMPTS = 2;

    private final ReactiveOAuth2AuthorizedClientProvider delegate;
    private final ReactiveOAuth2AuthorizationSuccessHandler refreshedClientHandler;
    private final Duration clockSkew;
    private final Clock clock;
    private final AsyncCache<RefreshKey, OAuth2AuthorizedClient> latestClients;

    public SingleFlightRefreshAuthorizedClientProvider(
            ReactiveOAuth2AuthorizedClientProvider delegate,
            ReactiveOAuth2AuthorizationSuccessHandler refreshedClientHandler,
            Duration clockSkew,
            Duration retention
    ) {
        this(delegate, refreshedClientHandler, clockSkew, retention, Clock.systemUTC(), Ticker.systemTicker());
    }

    SingleFlightRefreshAuthorizedClientProvider(
            ReactiveOAuth2AuthorizedClientProvider delegate,
            ReactiveOAuth2AuthorizationSuccessHandler refreshedClientHandler,
            Duration clockSkew,
            Duration retention,
            Clock clock,
            Ticker ticker
    ) {
        this.delegate = delegate;
        this.refreshedClientHandler = refreshedClientHandler;
        this.clockSkew = clockSkew;
        this.clock = clock;
        this.latestClients = Caffeine.newBuilder()
                .expireAfterWrite(retention)
                .maximumSize(MAX_ENTRIES)
                .ticker(ticker)
                .buildAsync();
    }

    @Override
    public Mono<OAuth2AuthorizedClient> authorize(OAuth2AuthorizationContext context) {
        var current = context.getAuthorizedClient();
        ServerWebExchange exchange = context.getAttribute(ServerWebExchange.class.getName());
        if (isNull(current) || isNull(current.getRefreshToken()) || isNull(exchange) || !needsRefresh(current)) {
            return delegate.authorize(context);
        }
        return exchange
                .getSession()
                .map(session -> new RefreshKey(
                        session.getId(),
                        current.getClientRegistration().getRegistrationId(),
                        context.getPrincipal().getName()))
                .flatMap(key -> authorizeWithLatest(key, context, current, MAX_REFRESH_ATTEMPTS));
    }

    private Mono<OAuth2AuthorizedClient> authorizeWithLatest(
            RefreshKey key,
            OAuth2AuthorizationContext requestContext,
            OAuth2AuthorizedClient refreshFrom,
            int attemptsLeft
    ) {
        var sessionClient = requestContext.getAuthorizedClient();
        var refreshContext = withAuthorizedClient(requestContext, refreshFrom);
        CompletableFuture<OAuth2AuthorizedClient> latestFuture =
                latestClients.get(key, (ignored, executor) -> delegate.authorize(refreshContext)
                        .flatMap(refreshed -> persist(refreshed, refreshContext))
                        .toFuture());

        return Mono
                .fromFuture(latestFuture, true)
                .flatMap(latest -> {
                    if (!needsRefresh(latest)) {
                        return hasSameAccessToken(latest, sessionClient) ? Mono.empty() : Mono.just(latest);
                    }
                    latestClients.asMap().remove(key, latestFuture);
                    if (attemptsLeft <= 0) {
                        return Mono.empty();
                    }
                    var newest = isIssuedAfter(sessionClient, latest) ? sessionClient : latest;
                    return authorizeWithLatest(key, requestContext, newest, attemptsLeft - 1);
                });
    }

    private Mono<OAuth2AuthorizedClient> persist(OAuth2AuthorizedClient refreshed, OAuth2AuthorizationContext context) {
        return refreshedClientHandler
                .onAuthorizationSuccess(refreshed, context.getPrincipal(), context.getAttributes())
                .onErrorResume(error -> {
                    log.warn("Klarte ikke å lagre fornyet token i sesjonen", error);
                    return Mono.empty();
                })
                .thenReturn(refreshed);
    }

    private static OAuth2AuthorizationContext withAuthorizedClient(
            OAuth2AuthorizationContext context,
            OAuth2AuthorizedClient authorizedClient
    ) {
        if (authorizedClient == context.getAuthorizedClient()) {
            return context;
        }
        return OAuth2AuthorizationContext
                .withAuthorizedClient(authorizedClient)
                .principal(context.getPrincipal())
                .attributes(attributes -> attributes.putAll(context.getAttributes()))
                .build();
    }

    private static boolean isIssuedAfter(OAuth2AuthorizedClient first, OAuth2AuthorizedClient second) {
        var firstIssuedAt = first.getAccessToken().getIssuedAt();
        var secondIssuedAt = second.getAccessToken().getIssuedAt();
        return nonNull(firstIssuedAt) && nonNull(secondIssuedAt) && firstIssuedAt.isAfter(secondIssuedAt);
    }

    private boolean needsRefresh(OAuth2AuthorizedClient authorizedClient) {
        var expiresAt = authorizedClient.getAccessToken().getExpiresAt();
        return nonNull(expiresAt) && clock.instant().isAfter(expiresAt.minus(clockSkew));
    }

    private static boolean hasSameAccessToken(OAuth2AuthorizedClient first, OAuth2AuthorizedClient second) {
        return first.getAccessToken().getTokenValue().equals(second.getAccessToken().getTokenValue());
    }

    private record RefreshKey(String sessionId, String registrationId, String principalName) {
    }
}
