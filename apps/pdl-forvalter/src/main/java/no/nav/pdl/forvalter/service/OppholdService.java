package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class OppholdService implements Validation<OppholdDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: oppholdFra må være før oppholdTil";
    private static final String VALIDATION_TYPE_ERROR = "Type av opphold må angis";
    private static final String VALIDATION_OPPHOLD_OVELAP_ERROR = "Feil: Overlappende opphold er detektert";

    public Mono<DbPerson> convert(DbPerson person) {

        return Flux.fromIterable(person.getPerson().getOpphold())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person.getPerson()));
                })
                .collectList()
                .flatMap(this::enforceIntegrity)
                .thenReturn(person);
    }

    @Override
    public Mono<Void> validate(OppholdDTO opphold) {

        if (isNull(opphold.getType())) {
            return Mono.error(new InvalidRequestException(VALIDATION_TYPE_ERROR));
        }

        if (nonNull(opphold.getOppholdFra()) && nonNull(opphold.getOppholdTil()) && !opphold.getOppholdFra().isBefore(opphold.getOppholdTil())) {
            return Mono.error(new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR));
        }
        return Mono.empty();
    }

    protected Mono<OppholdDTO> handle(OppholdDTO type) {

        // Ingen håndtering for enkeltpost
        return Mono.just(type);
    }

    protected Mono<List<OppholdDTO>> enforceIntegrity(List<OppholdDTO> opphold) {

        for (var i = 0; i < opphold.size(); i++) {
            if (i + 1 < opphold.size()) {
                if (isNull(opphold.get(i + 1).getOppholdTil()) &&
                        !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdFra().plusDays(1)) ||
                        (nonNull(opphold.get(i + 1).getOppholdTil()) &&
                                !opphold.get(i).getOppholdFra().isAfter(opphold.get(i + 1).getOppholdTil()))) {
                    return Mono.error(new InvalidRequestException(VALIDATION_OPPHOLD_OVELAP_ERROR));
                }
                if (isNull(opphold.get(i + 1).getOppholdTil())) {
                    opphold.get(i + 1).setOppholdTil(opphold.get(i).getOppholdFra().minusDays(1));
                }
            }
        }
        return Mono.just(opphold);
    }
}