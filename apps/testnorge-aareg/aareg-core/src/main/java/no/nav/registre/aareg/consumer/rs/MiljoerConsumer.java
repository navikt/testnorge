package no.nav.registre.aareg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.config.credentials.MiljoeServiceProperties;
import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@Slf4j
@DependencyOn(value = "testnorge-miljoer-service")
public class MiljoerConsumer {

    private final WebClient webClient;
    private final MiljoeServiceProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public MiljoerConsumer(
                           MiljoeServiceProperties serverProperties,
                           AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.serverProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public MiljoerResponse hentMiljoer() {

        log.info("Genererer AccessToken for {}", serverProperties.getName());
        AccessToken accessToken = accessTokenService.generateToken(serverProperties);
        List<String> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v1/miljoer").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();

        if (response == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Fant ingen miljøer");
        }
        return new MiljoerResponse(response);
    }
}
