package no.nav.registre.testnorge.helsepersonell.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


@Slf4j
public class GetAlleIdenterCommand implements Callable<Set<String>> {

    private final Long avspillergruppeId;
    private final UriTemplate uriTemplate;
    private final RestTemplate restTemplate;

    public GetAlleIdenterCommand(String url, Long avspillergruppeId, RestTemplate restTemplate) {
        this.uriTemplate = new UriTemplate(url + "/v1/alle-identer/{avspillergruppeId}");
        this.avspillergruppeId = avspillergruppeId;
        this.restTemplate = restTemplate;
    }

    @Override
    public Set<String> call() {
        try {
            log.info("Henter alle identer fra avspillergruppe {}", avspillergruppeId);
            String[] identer = restTemplate.getForObject(uriTemplate.expand(avspillergruppeId), String[].class);

            if (identer == null || identer.length == 0) {
                log.warn("Fant ingen identer for avspillergruppe {}", avspillergruppeId);
                return new HashSet<>();
            }
            log.info("Fant {} identer i avspillergruppe {}", identer.length, avspillergruppeId);
            return new HashSet<>(Arrays.asList(identer));
        } catch (Exception e) {
            log.error("Klarte ikke hente ut identer fra hodejegerern for avspillergruppe {}", avspillergruppeId, e);
            throw e;
        }
    }
}
