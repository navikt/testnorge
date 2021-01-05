package no.nav.registre.testnorge.miljoerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.miljoerservice.response.MiljoerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@DependencyOn(value = "tps-forvalteren", external = true)
@Slf4j
public class MiljoerService {

    private static final String TPSF_GET_ENVIRONMENTS = "/v1/environments";

    private final WebClient webClient = WebClient.builder().build();

    @Value("${tps-forvalteren.rest-api.url}")
    private String tpsfServerUrl;

    public MiljoerResponse getAktiveMiljoer() {
        log.info("Henter aktive miljøer fra TPSF..");
        MiljoerResponse response = webClient
                .get()
                .uri(URI.create(tpsfServerUrl + TPSF_GET_ENVIRONMENTS))
                .retrieve()
                .bodyToMono(MiljoerResponse.class)
                .block();

        if (isNull(response)) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fant ingen miljøer");

        }
        response.getEnvironments().sort(String::compareTo);

        return response;
    }
}
