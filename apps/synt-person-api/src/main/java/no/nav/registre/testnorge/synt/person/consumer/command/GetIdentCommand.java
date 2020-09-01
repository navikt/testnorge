package no.nav.registre.testnorge.synt.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.synt.person.consumer.command.model.CreateIdentRequest;

@Slf4j
@RequiredArgsConstructor
public class GetIdentCommand implements Callable<String> {
    private static final int MAX_YEARS = 90;

    private final WebClient webClient;
    private final String applicationName;

    @Override
    public String call() {
        log.info("Henter ident fra ident pool");

        var request = CreateIdentRequest
                .builder()
                .antall(1)
                .foedtEtter(LocalDate.now().minusYears(MAX_YEARS))
                .foedtFoer(LocalDate.now())
                .identtype("FNR")
                .rekvirertAv(applicationName)
                .build();

        String[] response = webClient
                .post()
                .uri(builder -> builder.path("/v1/identifikator").queryParam("finnNaermesteLedigeDato", "false").build())
                .body(BodyInserters.fromPublisher(Mono.just(request), CreateIdentRequest.class))
                .retrieve()
                .bodyToMono(String[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Klarete ikke å hente ident fra ident pool");
        }
        String ident = response[0];
        log.info("Hentet {} fra ident pool,", ident);
        return ident;
    }

}
