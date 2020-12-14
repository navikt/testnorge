package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@Service
public class OrganisasjonNummerConsumer {

    private static final String NAME_URL = "/api/v1/orgnummer";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonNummerConsumer(
            @Value("${organisasjon.nummer.url}") String baseUrl,
            @Value("${organisasjon.nummer.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public List<String> getOrgnummer(Integer antall) {

            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<List> response = webClient.get()
                    .uri(NAME_URL)
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .header("antall", antall.toString())
                    .retrieve()
                    .toEntity(List.class)
                    .block();

            return response.hasBody() ? response.getBody(): Collections.emptyList();
    }
}
