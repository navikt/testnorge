package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.OppsummeringsdokumentDTO;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;

@Slf4j
@RequiredArgsConstructor
public class SaveOppsummeringsdokumenterCommand implements Callable<String> {
    private final WebClient webClient;
    private final String accessToken;
    private final OppsummeringsdokumentDTO opplysningspliktigDTO;
    private final String miljo;
    private final String origin;
    private final Populasjon populasjon;

    @Override
    public String call() {
        log.info(
                "Sender inn opplysningspliktig {} den {}.",
                opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer(),
                opplysningspliktigDTO.getKalendermaaned()
        );
        var responseId = webClient
                .put()
                .uri(builder -> builder.path("/api/v1/oppsummeringsdokumenter").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", miljo)
                .header("origin", origin)
                .header("populasjon", populasjon.toString())
                .body(BodyInserters.fromPublisher(Mono.just(opplysningspliktigDTO), OppsummeringsdokumentDTO.class))
                .exchange()
                .flatMap(response -> {
                    var id = response.headers().header("ID").stream().findFirst();
                    if (id.isEmpty()) {
                        return Mono.error(
                                new RuntimeException("Klarer ikke å finne iden fra opplysningspliktigsdokument "  + opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer() + " den " + opplysningspliktigDTO.getKalendermaaned() + ".")
                        );
                    }
                    return Mono.just(id.get());
                })
                .block();

        log.info(
                "Opplysningspliktig {} sendt inn den {} med id {}",
                opplysningspliktigDTO.getOpplysningspliktigOrganisajonsnummer(),
                opplysningspliktigDTO.getKalendermaaned(),
                responseId
        );
        return responseId;
    }
}