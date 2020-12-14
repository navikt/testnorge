package no.nav.organisasjonforvalter.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class OrganisasjonNavnConsumer {

    private static final String NAME_URL = "/api/v1/navn";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonNavnConsumer(
            @Value("${organisasjon.navn.url}") String baseUrl,
            @Value("${organisasjon.navn.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public String getOrgName() {

        AccessToken accessToken = accessTokenService.generateToken(accessScopes);
        ResponseEntity<NavnDto> response = webClient.get()
                .uri(NAME_URL)
                .header("Nav-Consumer-Id", "Testnorge")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                        accessToken.getTokenValue())
                .retrieve()
                .toEntity(NavnDto.class)
                .block();
        return response.hasBody() ? format("%s %s",
                response.getBody().getAdjectiv(),
                response.getBody().getSubstantiv()) : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NavnDto{

        private String adjectiv;
        private String substantiv;
    }
}
