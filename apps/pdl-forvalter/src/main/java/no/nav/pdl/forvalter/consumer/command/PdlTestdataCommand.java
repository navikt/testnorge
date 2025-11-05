package no.nav.pdl.forvalter.consumer.command;

import no.nav.testnav.libs.data.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PdlStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.Callable;

public abstract class PdlTestdataCommand implements Callable<Flux<OrdreResponseDTO.HendelseDTO>> {

    static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    static final String TEMA = "Tema";
    static final Duration TIMEOUT = Duration.ofSeconds(10); // Seconds.

    OrdreResponseDTO.HendelseDTO errorHandling(Throwable error, Integer id) {
        return OrdreResponseDTO.HendelseDTO
                .builder()
                .id(id)
                .status(PdlStatus.FEIL)
                .error(WebClientError.describe(error).getMessage())
                .build();
    }

}
