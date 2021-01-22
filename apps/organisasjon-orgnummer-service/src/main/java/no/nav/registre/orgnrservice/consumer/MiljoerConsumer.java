package no.nav.registre.orgnrservice.consumer;

import no.nav.registre.orgnrservice.consumer.response.MiljoerResponse;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@DependencyOn(value = "testnorge-miljoer-service")
public class MiljoerConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;

    public MiljoerConsumer(@Value("${consumers.miljoer-service.url}") String miljoeUrl,
                           @Value("${consumers.miljoer-service.client_id}") String clientId,
                           AccessTokenService accessTokenService
    ) {
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(miljoeUrl)
                .build();
    }

    public MiljoerResponse hentMiljoer() {

        AccessToken accessToken = accessTokenService.generateToken(accessScopes);
        List<String> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/miljoer")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();

        if (response == null) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fant ingen miljøer");
        }
        return new MiljoerResponse(response);
    }
}
