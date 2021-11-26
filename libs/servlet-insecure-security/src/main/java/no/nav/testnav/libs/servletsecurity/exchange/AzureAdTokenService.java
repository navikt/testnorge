package no.nav.testnav.libs.servletsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.domain.AzureClientCredentials;


@Slf4j
@Service
public class AzureAdTokenService implements GenerateToken {
    private final WebClient webClient;
    private final AzureClientCredentials clientCredentials;

    public AzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            AzureClientCredentials clientCredentials
    ) {
        log.info("Init AzureAd token exchange.");
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(issuerUrl + "/oauth2/v2.0/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Client Credentials", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        this.webClient = builder.build();
        this.clientCredentials = clientCredentials;
    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        return generateClientCredentialAccessToken(serverProperties);
    }

    private Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", toScope(serverProperties));
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("grant_type", "client_credentials");

        log.info("Access token opprettet for OAuth 2.0 Client Credentials flow.");
        return webClient.post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                        .doBeforeRetry(value -> log.warn("Prøver å opprette tilkobling til azure på nytt."))
                ).doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        log.error(
                                "Feil ved henting av access token for {}. Feilmelding: {}.",
                                scope,
                                ((WebClientResponseException) error).getResponseBodyAsString()
                        );
                    } else {
                        log.error("Feil ved henting av access token for {}", scope, error);
                    }
                });

    }

    private String toScope(ServerProperties serverProperties) {
        return "api://" + serverProperties.getCluster() + "." + serverProperties.getNamespace() + "." + serverProperties.getName() + "/.default";
    }
}
