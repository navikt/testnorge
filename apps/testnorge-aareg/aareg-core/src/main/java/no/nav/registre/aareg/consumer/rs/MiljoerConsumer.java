package no.nav.registre.aareg.consumer.rs;

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

        AccessToken accessToken = accessTokenService.generateToken(serverProperties);
        List<String> response = webClient
                .get()
                .uri("/v1/miljoer")
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
