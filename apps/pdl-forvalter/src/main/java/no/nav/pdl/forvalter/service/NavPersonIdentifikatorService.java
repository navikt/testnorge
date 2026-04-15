package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class NavPersonIdentifikatorService implements Validation<NavPersonIdentifikatorDTO> {

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getNavPersonIdentifikator())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, dbPerson))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .collectList()
                .thenReturn(dbPerson);
    }

    protected Mono<NavPersonIdentifikatorDTO> handle(NavPersonIdentifikatorDTO navPersonIdentifikator, DbPerson dbPerson) {

        navPersonIdentifikator.setIdentifikator(dbPerson.getIdent());
        return Mono.just(navPersonIdentifikator);
    }

    @Override
    public Mono<Void> validate(NavPersonIdentifikatorDTO artifact) {

        // No validation
        return Mono.empty();
    }
}
