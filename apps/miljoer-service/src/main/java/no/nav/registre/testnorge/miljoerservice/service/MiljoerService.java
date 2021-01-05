package no.nav.registre.testnorge.miljoerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.miljoerservice.response.MiljoerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

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
        return webClient
                .get()
                .uri(URI.create(tpsfServerUrl + TPSF_GET_ENVIRONMENTS))
                .retrieve()
                .bodyToMono(MiljoerResponse.class)
                .block();
    }
}
