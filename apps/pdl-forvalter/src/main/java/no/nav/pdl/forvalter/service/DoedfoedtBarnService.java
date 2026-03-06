package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedfoedtBarnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class DoedfoedtBarnService implements Validation<DoedfoedtBarnDTO> {

    private static final String INVALID_DATO_MISSING = "DødfødtBarn: dato må oppgis";

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getDoedfoedtBarn())
                .filter(doedfoedtBarn -> isTrue(doedfoedtBarn.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(dfb -> {
                    dfb.setKilde(getKilde(dfb));
                    dfb.setMaster(getMaster(dfb, person));
                })
                .collectList()
                .then();
    }

    @Override
    public Mono<Void> validate(DoedfoedtBarnDTO type) {

        if (isNull(type.getDato())) {

            throw new InvalidRequestException(INVALID_DATO_MISSING);
        }
        return Mono.empty();
    }

    protected Mono<DoedfoedtBarnDTO> handle(DoedfoedtBarnDTO type) {

        // Ingen håndtering
        return Mono.just(type);
    }
}
