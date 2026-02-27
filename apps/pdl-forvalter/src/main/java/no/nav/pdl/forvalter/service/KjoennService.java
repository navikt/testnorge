package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.utils.KjoennFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class KjoennService implements BiValidation<KjoennDTO, PersonDTO> {

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getKjoenn())
                .filter(kjoenn -> isTrue(kjoenn.getIsNew()))
                .flatMap(kjoenn -> handle(kjoenn, person.getIdent()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .then();
    }

    private Mono<KjoennDTO> handle(KjoennDTO kjoenn, String ident) {

        if (isNull(kjoenn.getKjoenn())) {
            kjoenn.setKjoenn(KjoennFraIdentUtility.getKjoenn(ident));
        }
        return Mono.just(kjoenn);
    }

    @Override
    public Mono<Void> validate(KjoennDTO artifact, PersonDTO person) {

        // Ingen validering
        return Mono.empty();
    }
}
