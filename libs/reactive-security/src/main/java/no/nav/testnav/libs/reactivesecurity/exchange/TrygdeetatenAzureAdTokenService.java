package no.nav.testnav.libs.reactivesecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

import no.nav.testnav.libs.reactivesecurity.config.TrygdeetatenConfig;
import no.nav.testnav.libs.reactivesecurity.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Slf4j
@Service
public class TrygdeetatenAzureAdTokenService implements GenerateToken {

    private final WebClient webClient;
    private final TrygdeetatenConfig trygdeetatenConfig;

    public TrygdeetatenAzureAdTokenService(
            @Value("${http.proxy:#{null}}") String proxyHost,
            TrygdeetatenConfig trygdeetatenConfig
    ) {
        this.trygdeetatenConfig = trygdeetatenConfig;
        log.info("Init AzureAd Trygdeetaten token service.");
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(trygdeetatenConfig.getTokenEndpoint())
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

    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", toScope(serverProperties));
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", trygdeetatenConfig.getClientId())
                .with("client_secret", trygdeetatenConfig.getClientSecret())
                .with("grant_type", "client_credentials");

        log.trace("Access token opprettet for OAuth 2.0 Client Credentials flow.");
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
