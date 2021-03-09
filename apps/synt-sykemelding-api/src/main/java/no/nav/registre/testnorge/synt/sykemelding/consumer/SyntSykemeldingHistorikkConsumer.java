package no.nav.registre.testnorge.synt.sykemelding.consumer;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.synt.sykemelding.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.registre.testnorge.synt.sykemelding.exception.GenererSykemeldingerException;

@Slf4j
@Component
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
    public SyntSykemeldingHistorikkDTO genererSykemeldinger(String ident, LocalDate startDato) {
        log.info("Generererer sykemelding for {} fom {}", ident, startDato.toString());

        ResponseEntity<HashMap<String, SyntSykemeldingHistorikkDTO>> response = restTemplate.exchange(
                RequestEntity.post(new URI(url)).body(Map.of(ident, startDato)),
                // IKKE bruk bare <> skriv <HashMap<String, SyntSykemeldingHistorikkDTO>>. Uten dette blir det kompelerings feil i Java 11
                new ParameterizedTypeReference<HashMap<String, SyntSykemeldingHistorikkDTO>>() {
                });
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new GenererSykemeldingerException("Klarer ikke å generere sykemeldinger (http kode: " + response.getStatusCodeValue() + ")");
        }
        var body = response.getBody();
        if (body == null) {
            throw new GenererSykemeldingerException("Klarer ikke å generere sykemeldinger. Response objectet er null.");
        }
        log.info("Sykemelding generert.");
        return body.get(ident);
    }
}