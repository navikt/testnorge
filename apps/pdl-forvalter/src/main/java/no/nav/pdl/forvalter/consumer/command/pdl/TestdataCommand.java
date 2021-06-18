package no.nav.pdl.forvalter.consumer.command.pdl;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PdlStatus.FEIL;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.pdl.forvalter.dto.PdlOrdreResponse;

public abstract class TestdataCommand implements Callable<Mono<PdlOrdreResponse.Hendelse>> {
    static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    static final String TEMA = "Tema";
    PdlOrdreResponse.Hendelse errorHandling(Throwable error, Integer id) {
        if (error instanceof WebClientResponseException) {
            return PdlOrdreResponse.Hendelse.builder()
                    .id(id)
                    .status(FEIL)
                    .error(((WebClientResponseException) error).getResponseBodyAsString())
                    .build();
        } else {
            return PdlOrdreResponse.Hendelse.builder()
                    .id(id)
                    .status(FEIL)
                    .error(error.getMessage())
                    .build();
        }
    }
}
