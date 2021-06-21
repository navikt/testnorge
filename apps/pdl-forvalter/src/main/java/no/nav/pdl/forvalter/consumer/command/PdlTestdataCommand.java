package no.nav.pdl.forvalter.consumer.command;

import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PdlStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

public abstract class PdlTestdataCommand implements Callable<Mono<OrdreResponseDTO.HendelseDTO>> {

    static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    static final String TEMA = "Tema";

    OrdreResponseDTO.HendelseDTO errorHandling(Throwable error, Integer id) {

        if (error instanceof WebClientResponseException) {
            return OrdreResponseDTO.HendelseDTO.builder()
                    .id(id)
                    .status(PdlStatus.FEIL)
                    .error(((WebClientResponseException) error).getResponseBodyAsString())
                    .build();
        } else {
            return OrdreResponseDTO.HendelseDTO.builder()
                    .id(id)
                    .status(PdlStatus.FEIL)
                    .error(error.getMessage())
                    .build();
        }
    }
}