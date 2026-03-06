package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class NavPersonIdentifikatorService implements Validation<NavPersonIdentifikatorDTO> {

    public Mono<Void> convert(PersonDTO person) {

        return Flux.fromIterable(person.getNavPersonIdentifikator())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(identifikator -> person.setNavPersonIdentifikator(new ArrayList<>(identifikator)))
                .then();
    }

    protected Mono<NavPersonIdentifikatorDTO> handle(NavPersonIdentifikatorDTO type) {
        return Mono.just(type);
    }

    @Override
    public Mono<Void> validate(NavPersonIdentifikatorDTO artifact) {

        // No validation
        return Mono.empty();
    }
}
