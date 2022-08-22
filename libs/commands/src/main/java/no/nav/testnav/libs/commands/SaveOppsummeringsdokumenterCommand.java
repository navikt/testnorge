package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;
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
    private final OppsummeringsdokumentDTO opplysningspliktigDTO;
    private final String miljo;
    private final String origin;
    private final Populasjon populasjon;

    @Override
    public Mono<String> call() {
        log.info(
                "Sender inn opplysningspliktig {} den {} i {}.",
                opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer(),
                opplysningspliktigDTO.getKalendermaaned(),
                miljo
        );
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/oppsummeringsdokumenter").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .header("origin", origin)
                .header("populasjon", populasjon.toString())
                .body(BodyInserters.fromPublisher(Mono.just(opplysningspliktigDTO), OppsummeringsdokumentDTO.class))
                .exchange()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .flatMap(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    var id = response.headers().header("ID").stream().findFirst();
                    if (id.isEmpty()) {
                        return Mono.error(
                                new RuntimeException(
                                        "Klarer ikke å finne iden fra opplysningspliktigsdokument "
                                                + opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer()
                                                + " den " + opplysningspliktigDTO.getKalendermaaned() + "."
                                )
                        );
                    }
                    log.info(
                            "Opplysningspliktig {} sendt inn den {} med id {}",
                            opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer(),
                            opplysningspliktigDTO.getKalendermaaned(),
                            id.get()
                    );
                    return Mono.just(id.get());
                });
    }
}