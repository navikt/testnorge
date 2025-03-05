package no.nav.testnav.libs.reactivesecurity.exchange.azuread;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.azuread.ClientCredentialExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.azuread.AzureTrygdeetatenClientCredential;
import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
public class AzureTrygdeetatenTokenService implements TokenService {

    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final ObjectMapper objectMapper;
    private final Map<String, AccessToken> tokenCache;
    private final GetAuthenticatedUserId getAuthenticatedUserId;

    public AzureTrygdeetatenTokenService(
            WebClient webClient,
            String proxyHost,
            AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper) {
        log.info("Init AzureAd Trygdeetaten token service.");

        this.clientCredential = azureTrygdeetatenClientCredential;
        this.getAuthenticatedUserId = getAuthenticatedUserId;
        this.objectMapper = objectMapper;
        tokenCache = new HashMap<>();

        var builder = webClient
                .mutate()
                .baseUrl(azureTrygdeetatenClientCredential.getTokenEndpoint())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        if (nonNull(proxyHost)) {
            log.trace("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);
            builder.clientConnector(new ReactorClientHttpConnector(
                    HttpClient
                            .create()
                            .proxy(proxy -> proxy
                                    .type(ProxyProvider.Proxy.HTTP)
                                    .host(uri.getHost())
                                    .port(uri.getPort()))
            ));
        }
        this.webClient = builder.build();
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {

        return getAuthenticatedUserId.call()
                .map(user -> String.format("%s:%s", user, serverProperties.toAzureAdScope()))
                .flatMap(key -> {
                    if (!tokenCache.containsKey(key) ||
                            expires(tokenCache.get(key))) {

                        synchronized (this) {
                            if (!tokenCache.containsKey(key) ||
                                    expires(tokenCache.get(key))) {

                                return new ClientCredentialExchangeCommand(
                                        webClient,
                                        clientCredential,
                                        serverProperties.toAzureAdScope())
                                        .call()
                                        .doOnNext(token -> tokenCache.put(key, token));
                            } else {

                                return Mono.just(tokenCache.get(key));
                            }
                        }
                    } else {

                        return Mono.just(tokenCache.get(key));
                    }
                });
    }

    @SneakyThrows
    private boolean expires(AccessToken accessToken) {

        var chunks = accessToken.getTokenValue().split("\\.");
        var body = Base64.getDecoder().decode(chunks[1]);

        return Instant.ofEpochSecond(objectMapper.readTree(body).get("exp").asInt())
                .minusSeconds(300)
                .isBefore(Instant.now());
    }

    public static class Test extends AzureTrygdeetatenTokenService {

        public Test(WebClient webClient, String proxyHost, AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential, GetAuthenticatedUserId getAuthenticatedUserId, ObjectMapper objectMapper) {
            super(webClient, proxyHost, azureTrygdeetatenClientCredential, getAuthenticatedUserId, objectMapper);
        }

        @Override
        public Mono<AccessToken> exchange(ServerProperties serverProperties) {
            return Mono.empty();
        }

    }

}
