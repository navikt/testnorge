package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
public class KjoennService implements BiValidation<KjoennDTO, PersonDTO> {

    private static final String INVALID_IDENT = "Ident må være på 11 tegn og numerisk";

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getKjoenn())
                .filter(kjoenn -> isTrue(kjoenn.getIsNew()))
                .flatMap(kjoenn -> handle(kjoenn, person.getIdent()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(kjoenn -> person.setKjoenn(new ArrayList<>(kjoenn)))
                .then();
    }

    private Mono<KjoennDTO> handle(KjoennDTO kjoenn, String ident) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(KjoennFraIdentUtility.getKjoenn(ident));
        }
        return Mono.just(kjoenn);
    }

    @Override
    public void validate(KjoennDTO artifact, PersonDTO person) {

        if (isNull(artifact) &&
            !isNumeric(person.getIdent()) ||
            person.getIdent().length() != 11) {

            throw new InvalidRequestException(INVALID_IDENT);
        }
    }
}
