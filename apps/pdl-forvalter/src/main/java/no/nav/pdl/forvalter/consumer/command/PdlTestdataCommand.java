package no.nav.pdl.forvalter.consumer.command;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

public abstract class PdlTestdataCommand implements Callable<Mono<OrdreResponseDTO.HendelseDTO>> {

    static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    static final String TEMA = "Tema";

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException &&
                StringUtils.isNotBlank(webClientResponseException.getResponseBodyAsString()) ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    OrdreResponseDTO.HendelseDTO errorHandling(Throwable error, Integer id) {

        return OrdreResponseDTO.HendelseDTO.builder()
                .id(id)
                .status(PdlStatus.FEIL)
                .error(getMessage(error))
                .build();
    }
}