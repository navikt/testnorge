package no.nav.registre.populasjoner.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Slf4j
@Component
@DependencyOn("testnorge-hodejegeren")
public class HodejegerenConsumer {

    private final RestTemplate restTemplate;
    private final String url;
    private final String avspillerGruppe;

    public HodejegerenConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.hodejegren.url}") String url,
            @Value("${tpsf.avspiller.gruppe}") String avspillerGruppe
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.avspillerGruppe = avspillerGruppe;
    }


    public Set<String> getAllIdenter() {
        String[] response = restTemplate.getForObject(url + "/api/v1/alle-identer/" + avspillerGruppe, String[].class);
        if (response == null) {
            log.warn("Fant ingen identer for avspiller gruppe {}", avspillerGruppe);
            return Collections.emptySet();
        }
        Set<String> identer = Arrays.stream(response).collect(Collectors.toSet());
        log.info("Fant {} identer i avspiller gruppen {}", identer.size(), avspillerGruppe);
        return identer;
    }

}
