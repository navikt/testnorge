package no.nav.registre.testnorge.synt.sykemelding.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import no.nav.registre.testnorge.common.headers.NavHeaders;
import no.nav.registre.testnorge.common.session.NavSession;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;

@Slf4j
@Component
@DependencyOn(value = "nais-synthdata-elsam", external = true)
public class SyntSykemeldingHistorikkConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public SyntSykemeldingHistorikkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.synt-sykemelding.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/generate_sykmeldings_history_json";
    }

    @SneakyThrows
    public Map<String, SyntSykemeldingHistorikkDTO> genererSykemeldinger(Map<String, LocalDate> historikkMap, NavSession navSession) {
        log.info("Generererer sykemelding for {}...", String.join(",", historikkMap.keySet()));

        ResponseEntity<HashMap<String, SyntSykemeldingHistorikkDTO>> response = restTemplate.exchange(
                RequestEntity.post(new URI(url)).header(NavHeaders.UUID, navSession.getUuid()).body(historikkMap),
                new ParameterizedTypeReference<>() {
                });
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Klarer ikke å generere sykemeldinger (http kode: " + response.getStatusCodeValue() + ")");
        }
        var body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Klarer ikke å generere sykemeldinger. Response objectet er null.");
        }
        log.info("Sykemelding generert.");
        return body;
    }
}