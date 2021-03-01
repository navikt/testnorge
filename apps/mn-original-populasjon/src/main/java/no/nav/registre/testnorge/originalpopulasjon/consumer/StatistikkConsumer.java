package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnorge.originalpopulasjon.exceptions.StatistikkException;

@Slf4j
@Component
@DependencyOn("statistikk-api")
public class StatistikkConsumer {

    private final WebClient webClient;

    StatistikkConsumer(@Value("${consumer.statistikk.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public StatistikkDTO getStatistikk(StatistikkType statistikkType) {
        log.info("Henter statistikk...");
        var statistikkDTO = webClient.get().uri(builder -> builder
                .path("/api/v1/statistikk/{type}")
                .build(statistikkType))
                .retrieve()
                .bodyToMono(StatistikkDTO.class)
                .block();

        if (statistikkDTO == null) {
            throw new StatistikkException("Noe gikk galt da statistikk ble hentet");
        }
        return statistikkDTO;
    }
}
