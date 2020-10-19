package no.nav.registre.testnorge.originalpopulasjon.consumer.command;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.originalpopulasjon.consumer.command.model.GetIdenterRequest;
import no.nav.registre.testnorge.originalpopulasjon.domain.Alderskategori;

@Slf4j
@RequiredArgsConstructor
public class GetIdenterCommand implements Callable<List<String>> {

    private final WebClient webClient;
    private final String applicationName;
    private final Alderskategori alderskategori;

    @Override
    public List<String> call() {
        log.info("Henter identer fra ident pool");

        var request = GetIdenterRequest
                .builder()
                .antall(alderskategori.getAntall())
                .foedtEtter(LocalDate.now().minusYears(alderskategori.getMaxAlder()))
                .foedtFoer(LocalDate.now().minusYears(alderskategori.getMinAlder()))
                .identtype("FNR")
                .rekvirertAv(applicationName)
                .build();

        String[] response = webClient
                .post()
                .uri(builder -> builder.path("/api/v1/identifikator").queryParam("finnNaermesteLedigeDato", "false").build())
                .body(BodyInserters.fromPublisher(Mono.just(request), GetIdenterRequest.class))
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Klarte ikke å hente identer fra ident pool");
        }

        log.info("Hentet {} av {} fra ident pool,", response.length, alderskategori.getAntall());

        return Arrays.asList(response.clone());
    }
}
