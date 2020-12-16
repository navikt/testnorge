package no.nav.organisasjonforvalter.consumer;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Service
public class OrganisasjonBestillingStatusConsumer {

    public enum ItemStatus {RUNNING, COMPLETED, ERROR, FAILED}

    private static final String STATUS_URL = "/api/v1/order/{uuid}/items";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonBestillingStatusConsumer(
            @Value("${organisasjon.bestilling.url}") String baseUrl,
            @Value("${organisasjon.bestilling.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.accessTokenService = accessTokenService;
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public List<ItemDto> getBestillingStatus(String uuid) {

        AccessToken accessToken = accessTokenService.generateToken(accessScopes);
        ResponseEntity<ItemDto[]> response = webClient.get()
                .uri(STATUS_URL.replace("{uuid}", uuid))
                .header("Nav-Consumer-Id", "Testnorge")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                        accessToken.getTokenValue())
                .retrieve()
                .toEntity(ItemDto[].class)
                .block();

        return response.hasBody() ? List.of(response.getBody()) : Collections.emptyList();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {

        private Integer id;
        private ItemStatus status;
    }
}
