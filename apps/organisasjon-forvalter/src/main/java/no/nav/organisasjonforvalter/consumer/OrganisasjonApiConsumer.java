package no.nav.organisasjonforvalter.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.ws.rs.ClientErrorException;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

@Service
public class OrganisasjonApiConsumer {

    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}?miljo=";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonApiConsumer(
            @Value("${organisasjon.bestilling.url}") String baseUrl,
            @Value("${organisasjon.bestilling.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public Response getStatus(String orgnummer, String miljoe) {

        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<Response> response = webClient.get()
                    .uri(STATUS_URL.replace("{orgnummer}", orgnummer) + miljoe)
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .retrieve()
                    .toEntity(Response.class)
                    .block();

            return response.hasBody() ? response.getBody() : new Response();

        } catch (WebClientResponseException e) {

            return new Response();
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private String orgnummer;
        private List<String> driverVirksomheter;
    }
}
