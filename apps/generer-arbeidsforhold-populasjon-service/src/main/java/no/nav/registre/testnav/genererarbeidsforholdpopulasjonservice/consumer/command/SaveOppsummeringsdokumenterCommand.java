package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SaveOppsummeringsdokumenterCommand implements Callable<Mono<String>> {
    private final WebClient webClient;
    private final String accessToken;
    private final OppsummeringsdokumentDTO dto;
    private final String miljo;
    private final String origin;
    private final Populasjon populasjon;

    @Override
    public Mono<String> call() {
        log.info(
                "Sender inn opplysningspliktig {} den {}.",
                dto.getOpplysningspliktigOrganisajonsnummer(),
                dto.getKalendermaaned()
        );
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/oppsummeringsdokumenter").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .header("origin", origin)
                .header("populasjon", populasjon.toString())
                .body(BodyInserters.fromPublisher(Mono.just(dto), OppsummeringsdokumentDTO.class))
                .exchange()
                .flatMap(response -> response.toEntity(String.class))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(response -> {


                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new RuntimeException(String.format(
                                "Feil med opprettelse av opplysningspliktig %s. Error: %s Status code: %s.",
                                dto.getOpplysningspliktigOrganisajonsnummer(),
                                response.getBody(),
                                response.getStatusCodeValue()
                        ));
                    }

                    var id = response.getHeaders().get("ID").stream().findFirst().orElseThrow();
                    log.info(
                            "Opplysningspliktig {} sendt inn den {} med id {}",
                            dto.getOpplysningspliktigOrganisajonsnummer(),
                            dto.getKalendermaaned(),
                            id
                    );
                    return id;
                });
    }
}