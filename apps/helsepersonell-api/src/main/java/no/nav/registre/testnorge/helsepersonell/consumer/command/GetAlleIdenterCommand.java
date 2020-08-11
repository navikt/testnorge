package no.nav.registre.testnorge.helsepersonell.consumer.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;


@Slf4j
public class GetAlleIdenterCommand implements Callable<Set<String>> {
    private final Long avspillergruppeId;
    private final WebClient webClient;

    public GetAlleIdenterCommand(Long avspillergruppeId, WebClient webClient) {
        this.webClient = webClient;
        this.avspillergruppeId = avspillergruppeId;
    }

    @Override
    public Set<String> call() {
        try {

            log.info("Henter alle identer fra avspillergruppe {}", avspillergruppeId);
            String[] identer = webClient.get().uri(builder -> builder
                    .path("/v1/alle-identer/{avspillergruppeId}")
                    .build(avspillergruppeId)
            ).retrieve().bodyToMono(String[].class).block();

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
