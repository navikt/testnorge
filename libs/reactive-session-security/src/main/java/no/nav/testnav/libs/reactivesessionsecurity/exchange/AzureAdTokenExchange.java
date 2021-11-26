package no.nav.testnav.libs.reactivesessionsecurity.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import no.nav.testnav.libs.reactivesessionsecurity.domain.AccessToken;
import no.nav.testnav.libs.reactivesessionsecurity.domain.AzureClientCredentials;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesessionsecurity.domain.Token;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.TokenResolver;

@Slf4j
@Service
@Import({
        AzureClientCredentials.class
})
public class AzureAdTokenExchange implements GenerateTokenExchange {
    private final WebClient webClient;
    private final TokenResolver tokenService;
    private final AzureClientCredentials clientCredentials;

    public AzureAdTokenExchange(
            @Value("${AAD_ISSUER_URI}") String issuerUrl,
            TokenResolver tokenService,
            AzureClientCredentials clientCredentials
    ) {
        WebClient.Builder builder = WebClient
                .builder()
                .baseUrl(issuerUrl + "/oauth2/v2.0/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);


        this.tokenService = tokenService;
        this.webClient = builder.build();
        this.clientCredentials = clientCredentials;
    }

    @Override
    public Mono<AccessToken> generateToken(ServerProperties serverProperties, ServerWebExchange exchange) {
        return tokenService
                .getToken(exchange)
                .flatMap(token -> generateOnBehalfOfAccessToken(token, toScope(serverProperties)));
    }

    @Deprecated
    public Mono<AccessToken> generateToken(String scope, ServerWebExchange exchange) {
        return tokenService
                .getToken(exchange)
                .flatMap(token -> generateOnBehalfOfAccessToken(token, scope));
    }


    private String toScope(ServerProperties serverProperties) {
        return "api://" + serverProperties.getCluster() + "." + serverProperties.getNamespace() + "." + serverProperties.getName() + "/.default";
    }

    public Mono<AccessToken> generateClientCredentialAccessToken(ServerProperties serverProperties) {
        log.trace("Henter OAuth2 access token fra client credential...");

        var scope = String.join(" ", toScope(serverProperties));
        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
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

    private Mono<AccessToken> generateOnBehalfOfAccessToken(Token token, String scope) {
        if (clientCredentials.getClientSecret() == null) {
            log.error("Client secret er null.");
        }

        var body = BodyInserters
                .fromFormData("scope", scope)
                .with("client_id", clientCredentials.getClientId())
                .with("client_secret", clientCredentials.getClientSecret())
                .with("assertion", token.getValue())
                .with("requested_token_use", "on_behalf_of")
                .with("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

        log.info("Access token opprettet for OAuth 2.0 On-Behalf-Of Flow. Scope: {}.", scope);
        return webClient
                .post()
                .body(body)
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnError(error -> {
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

}
