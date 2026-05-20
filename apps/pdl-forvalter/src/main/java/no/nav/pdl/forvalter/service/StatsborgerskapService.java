package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.pdl.forvalter.utils.IdenttypeUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.NORGE;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.hasLandkode;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.FNR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype.NPID;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class StatsborgerskapService implements Validation<StatsborgerskapDTO> {

    private static final String VALIDATION_LANDKODE_ERROR = "Ugyldig landkode, må være i hht ISO-3 Landkoder";
    private static final String VALIDATION_DATOINTERVALL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";

    private final KodeverkConsumer kodeverkConsumer;

    public Mono<DbPerson> convert(DbPerson dbPerson) {

        return Flux.fromIterable(dbPerson.getPerson().getStatsborgerskap())
                .filter(type -> isTrue(type.getIsNew()))
                .flatMap(type -> handle(type, dbPerson.getPerson()))
                .doOnNext(type -> {
                    type.setKilde(getKilde(type));
                    type.setMaster(getMaster(type, dbPerson.getPerson()));
                })
                .then(Mono.just(dbPerson));
    }

    @Override
    public Mono<Void> validate(StatsborgerskapDTO statsborgerskap) {

        if (nonNull(statsborgerskap.getLandkode()) && !hasLandkode(statsborgerskap.getLandkode())) {
            return Mono.error(new InvalidRequestException(VALIDATION_LANDKODE_ERROR));
        }

        if (nonNull(statsborgerskap.getGyldigFraOgMed()) && nonNull(statsborgerskap.getGyldigTilOgMed()) &&
            !statsborgerskap.getGyldigFraOgMed().isBefore(statsborgerskap.getGyldigTilOgMed())) {
            return Mono.error(new InvalidRequestException(VALIDATION_DATOINTERVALL_ERROR));
        }
        return Mono.empty();
    }

    private Mono<StatsborgerskapDTO> handle(StatsborgerskapDTO statsborgerskap, PersonDTO person) {

        return setLandkode(statsborgerskap, person)
                .doOnNext(type -> {
                    if (isNull(type.getGyldigFraOgMed()) &&
                        isNull(type.getBekreftelsesdato()) &&
                        (type.getMaster() == DbVersjonDTO.Master.PDL ||
                        IdenttypeUtility.getIdenttype(person.getIdent()) == NPID)) {
                        type.setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
                    }
                });
    }

    private Mono<StatsborgerskapDTO> setLandkode(StatsborgerskapDTO statsborgerskap, PersonDTO person) {

        if (isBlank(statsborgerskap.getLandkode())) {
            if (!person.getInnflytting().isEmpty() &&
                isNotBlank(person.getInnflytting().getFirst().getFraflyttingsland())) {
                statsborgerskap.setLandkode(person.getInnflytting().getFirst().getFraflyttingsland());
                return Mono.just(statsborgerskap);
            } else if (FNR.equals(IdenttypeUtility.getIdenttype(person.getIdent()))) {
                statsborgerskap.setLandkode(NORGE);
                return Mono.just(statsborgerskap);
            } else {
                return kodeverkConsumer.getTilfeldigLand()
                        .doOnNext(statsborgerskap::setLandkode)
                        .thenReturn(statsborgerskap);
            }
        }
        return Mono.just(statsborgerskap);
    }
}