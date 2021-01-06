package no.nav.registre.testnorge.miljoerservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.miljoerservice.response.MiljoerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.isNull;

@Service
@DependencyOn(value = "tps-forvalteren", external = true)
@Slf4j
public class TpsfConsumer {

    private final WebClient webClient;

    public TpsfConsumer(@Value("${tps-forvalteren.rest-api.url}") String tpsfServerUrl) {
        this.webClient = WebClient.builder().baseUrl(tpsfServerUrl).build();
    }

    public MiljoerResponse getAktiveMiljoer() {
        log.info("Henter aktive miljøer fra TPSF.");
        ResponseEntity<MiljoerResponse> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v1/environments").build())
                .retrieve()
                .toEntity(MiljoerResponse.class)
                .block();

        if (response == null || isNull(response.getBody()) || isNull(response.getBody().getEnvironments())) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fant ingen miljøer");
        }

        response.getBody().getEnvironments().sort(String::compareTo);

        return response.getBody();
    }
}
