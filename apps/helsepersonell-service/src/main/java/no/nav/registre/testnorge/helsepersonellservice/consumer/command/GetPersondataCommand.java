package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.util.WebClientFilter;
import no.nav.testnav.libs.dto.hodejegeren.v1.PersondataDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetPersondataCommand implements Callable<PersondataDTO> {

    private final String ident;
    private final String miljoe;
    private final WebClient webClient;
    private final String token;


    @Override
    public PersondataDTO call() {
        try {
            log.info("Henter persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe);
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/persondata")
                                    .queryParam("ident", ident)
                                    .queryParam("miljoe", miljoe)
                                    .build()
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(PersondataDTO.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente ut persondata fra hodejegerern for ident {} i miljø {}", ident, miljoe, e);
            throw e;
        }
    }
}
