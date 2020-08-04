package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.statistikk.v1.StatistikkDTO;

@Slf4j
@Component
@DependencyOn("statistikk-api")
public class StatistikkConsumer {
    private final RestTemplate restTemplate;
    private final String url;

    public StatistikkConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.statistikk.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/statistikk/ANTALL_ARBEIDSTAKERE_SYKEMELDT";
    }

    public double getAntallSykemeldtIProsent() {
        RequestEntity<Void> request = RequestEntity.get(URI.create(this.url)).build();
        log.info("Henteller antall arbeidstakere som er i snitt sykemeldt.");
        ResponseEntity<StatistikkDTO> entity = restTemplate.exchange(request, StatistikkDTO.class);

        if (!entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut antall sykemldete i prossent");
        }
        return entity.getBody().getValue();
    }
}
