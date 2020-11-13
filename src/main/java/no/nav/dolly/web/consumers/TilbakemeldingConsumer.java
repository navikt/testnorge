package no.nav.dolly.web.consumers;

import no.nav.dolly.web.config.RemoteApplicationsProperties;
import no.nav.dolly.web.security.TokenService;
import no.nav.dolly.web.security.domain.AccessScopes;
import no.nav.dolly.web.security.domain.AccessToken;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.TilbakemeldingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@DependencyOn("testnorge-tilbakemelding-api")
public class TilbakemeldingConsumer {
    private final WebClient webClient;
    private final RemoteApplicationsProperties properties;
    private final TokenService tokenService;

    public TilbakemeldingConsumer(
            @Value("${consumers.testnorge-tilbakemelding-api.baseUrl}") String baseUrl,
            RemoteApplicationsProperties properties,
            TokenService tokenService
    ) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.properties = properties;
        this.tokenService = tokenService;
    }

    public void send(TilbakemeldingDTO dto) {
        AccessToken accessToken = tokenService.getAccessToken(
                new AccessScopes(properties.get("testnorge-tilbakemelding-api"))
        );
        webClient
                .post()
                .uri("/api/v1/tilbakemelding")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(dto), TilbakemeldingDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
