package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.hasSpraak;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class TilrettelagtKommunikasjonService implements Validation<TilrettelagtKommunikasjonDTO> {

    private static final String VALIDATION_NO_SPRAAK_ERROR = "Språk for taletolk og/eller tegnspråktolk må oppgis";
    private static final String VALIDATION_TOLKESPRAAK_ERROR = "Språk for taletolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";
    private static final String VALIDATION_TEGNSPRAAK_ERROR = "Språk for tegnspråktolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";

    public Mono<Void> convert(PersonDTO person) {
        return Flux.fromIterable(person.getTilrettelagtKommunikasjon())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(this::handle)
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, person));
                })
                .collectList()
                .doOnNext(kommunikasjon -> person.setTilrettelagtKommunikasjon(new ArrayList<>(kommunikasjon)))
                .then();
    }

    @Override
    public Mono<Void> validate(TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        if (isBlank(tilrettelagtKommunikasjon.getSpraakForTaletolk()) &&
                isBlank(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk())) {
            throw new InvalidRequestException(VALIDATION_NO_SPRAAK_ERROR);
        }

        if (isNotBlank(tilrettelagtKommunikasjon.getSpraakForTaletolk()) &&
                (!hasSpraak(tilrettelagtKommunikasjon.getSpraakForTaletolk()))) {
            throw new InvalidRequestException(VALIDATION_TOLKESPRAAK_ERROR);
        }

        if (isNotBlank(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk()) &&
                !hasSpraak(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk())) {
            throw new InvalidRequestException(VALIDATION_TEGNSPRAAK_ERROR);
        }
        return Mono.empty();
    }

    protected Mono<TilrettelagtKommunikasjonDTO> handle(TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        tilrettelagtKommunikasjon.setMaster(DbVersjonDTO.Master.PDL);
        return Mono.just(tilrettelagtKommunikasjon);
    }
}
