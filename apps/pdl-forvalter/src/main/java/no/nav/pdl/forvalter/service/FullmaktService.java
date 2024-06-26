package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class FullmaktService implements BiValidation<FullmaktDTO, PersonDTO> {

    private static final String VALIDATION_GYLDIG_FOM_ERROR = "Fullmakt med gyldigFom må angis";
    private static final String VALIDATION_GYLDIG_TOM_ERROR = "Fullmakt med gyldigTom må angis";
    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_OMRAADER_ERROR = "Områder for fullmakt må angis";
    private static final String VALIDATION_FULLMEKTIG_ERROR = "Fullmektig: person %s ikke funnet i database";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public List<FullmaktDTO> convert(PersonDTO person) {

        for (var type : person.getFullmakt()) {

            if (isTrue(type.getIsNew())) {

                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
                handle(type, person.getIdent());
            }
        }
        return person.getFullmakt();
    }

    @Override
    public void validate(FullmaktDTO fullmakt, PersonDTO person) {

        if (isNull(fullmakt.getOmraader())) {
            throw new InvalidRequestException(VALIDATION_OMRAADER_ERROR);
        }

        if (isNull(fullmakt.getGyldigFraOgMed())) {
            throw new InvalidRequestException(VALIDATION_GYLDIG_FOM_ERROR);

        } else if (isNull(fullmakt.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_GYLDIG_TOM_ERROR);

        } else if (!fullmakt.getGyldigFraOgMed().isBefore(fullmakt.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (!isTestnorgeIdent(person.getIdent()) && isNotBlank(fullmakt.getMotpartsPersonident()) &&
                !personRepository.existsByIdent(fullmakt.getMotpartsPersonident())) {
            throw new InvalidRequestException(format(VALIDATION_FULLMEKTIG_ERROR, fullmakt.getMotpartsPersonident()));
        }
    }

    private void handle(FullmaktDTO fullmakt, String ident) {

        fullmakt.setEksisterendePerson(isNotBlank(fullmakt.getMotpartsPersonident()));

        if (isBlank(fullmakt.getMotpartsPersonident())) {

            if (isNull(fullmakt.getNyFullmektig())) {
                fullmakt.setNyFullmektig(new PersonRequestDTO());
            }

            if (isNull(fullmakt.getNyFullmektig().getAlder()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtEtter()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtFoer())) {

                fullmakt.getNyFullmektig().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyFullmektig().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            if (isNull(fullmakt.getNyFullmektig().getSyntetisk())) {
                fullmakt.getNyFullmektig().setSyntetisk(SyntetiskFraIdentUtility.isSyntetisk(ident));
            }

            fullmakt.setMotpartsPersonident(createPersonService.execute(fullmakt.getNyFullmektig()).getIdent());

        } else {

            var motpartPerson = new AtomicReference<DbPerson>();
            personRepository.findByIdent(fullmakt.getMotpartsPersonident())
                    .ifPresentOrElse(motpartPerson::set,
                            () -> motpartPerson.set(personRepository.save(DbPerson.builder()
                                            .ident(fullmakt.getMotpartsPersonident())
                                            .person(PersonDTO.builder()
                                                    .ident(fullmakt.getMotpartsPersonident())
                                                    .build())
                                            .sistOppdatert(LocalDateTime.now())
                                    .build())));
        }

        relasjonService.setRelasjoner(ident, RelasjonType.FULLMAKTSGIVER,
                fullmakt.getMotpartsPersonident(), RelasjonType.FULLMEKTIG);

        fullmakt.setMaster(Master.PDL);
    }
}