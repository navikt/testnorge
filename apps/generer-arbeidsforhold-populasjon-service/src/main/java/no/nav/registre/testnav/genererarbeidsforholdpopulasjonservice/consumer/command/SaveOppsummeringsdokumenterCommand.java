package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;

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
                .flatMap(response -> {
                    var id = response.headers().header("ID").stream().findFirst();
                    if (id.isEmpty()) {
                        return Mono.error(
                                new RuntimeException(
                                        String.format("Klarer ikke å finne iden fra opplysningspliktigsdokument %s den %s .",
                                                dto.getOpplysningspliktigOrganisajonsnummer(),
                                                dto.getKalendermaaned())
                                )
                        );
                    }
                    log.info(
                            "Opplysningspliktig {} sendt inn den {} med id {}",
                            dto.getOpplysningspliktigOrganisajonsnummer(),
                            dto.getKalendermaaned(),
                            id
                    );
                    return Mono.just(id.get());
                });
    }
}