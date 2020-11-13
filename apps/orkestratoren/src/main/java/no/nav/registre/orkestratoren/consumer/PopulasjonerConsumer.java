package no.nav.registre.orkestratoren.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn("testnorge-populasjoner-api")
public class PopulasjonerConsumer {

    private final RestTemplate restTemplate;
    private final String url;

    public PopulasjonerConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.populasjoner.url}") String url
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url + "/api/v1/populasjoner/MINI_NORGE/identer";
    }

    public Set<String> getPopulasjon() {
        log.info("Henter mini-norge populasjonen");
        ResponseEntity<String[]> entity = restTemplate.getForEntity(url, String[].class);
        if (!entity.getStatusCode().is2xxSuccessful() || entity.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut populasjonen.");
        }

        if (entity.getBody().length == 0) {
            log.warn("Fikk tilbake en tom populasjon");
        }
        return Set.of(entity.getBody());
    }
}