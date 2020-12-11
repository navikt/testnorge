package no.nav.organisasjonforvalter.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class OrgNameConsumer {

    private static final String NAME_URL = "/api/v1/navn";

    private final WebClient webClient;

    public OrgNameConsumer(
            @Value("${organisasjon.name.url}") String baseUrl) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public String getOrgName() {

        ResponseEntity<NavnDto> response = webClient.get()
                .uri(NAME_URL)
                .header("Nav-Consumer-Id", "Testnorge")
                .header("Nav-Call-Id", UUID.randomUUID().toString())
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
