package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.EgenskaperFraHovedperson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class VergemaalService implements Validation<VergemaalDTO> {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_EMBETE_ERROR = "Embete for vergemål må angis";
    private static final String VALIDATION_TYPE_ERROR = "Sakstype av vergemål må angis";
    private static final String VALIDATION_VERGEMAAL_ERROR = "Vergeperson med ident %s ikke funnet i database";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public List<VergemaalDTO> convert(PersonDTO person) {

        for (var type : person.getVergemaal()) {

            if (isTrue(type.getIsNew())) {

                handle(type, person.getIdent());
                type.setKilde(getKilde(type));
                type.setMaster(getMaster(type, person));
            }
        }
        return person.getVergemaal();
    }

    public void validate(VergemaalDTO vergemaal) {

        if (isNull(vergemaal.getVergemaalEmbete())) {
            throw new InvalidRequestException(VALIDATION_EMBETE_ERROR);
        }

        if (nonNull(vergemaal.getGyldigFraOgMed()) && nonNull(vergemaal.getGyldigTilOgMed()) &&
                !vergemaal.getGyldigFraOgMed().isBefore(vergemaal.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (isNull(vergemaal.getSakType())) {
            throw new InvalidRequestException(VALIDATION_TYPE_ERROR);
        }

        if (isNotBlank(vergemaal.getVergeIdent()) &&
                !personRepository.existsByIdent(vergemaal.getVergeIdent())) {
            throw new InvalidRequestException(format(VALIDATION_VERGEMAAL_ERROR, vergemaal.getVergeIdent()));
        }
    }

    private void handle(VergemaalDTO vergemaal, String ident) {

        vergemaal.setEksisterendePerson(isNotBlank(vergemaal.getVergeIdent()));

        if (isBlank(vergemaal.getVergeIdent())) {

            if (isNull(vergemaal.getNyVergeIdent())) {
                vergemaal.setNyVergeIdent(new PersonRequestDTO());
            }

            if (isNull(vergemaal.getNyVergeIdent().getAlder()) &&
                    isNull(vergemaal.getNyVergeIdent().getFoedtEtter()) &&
                    isNull(vergemaal.getNyVergeIdent().getFoedtFoer())) {

                vergemaal.getNyVergeIdent().setFoedtFoer(LocalDateTime.now().minusYears(18));
                vergemaal.getNyVergeIdent().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            EgenskaperFraHovedperson.kopierData(ident, vergemaal.getNyVergeIdent());

            vergemaal.setVergeIdent(createPersonService.execute(vergemaal.getNyVergeIdent()).getIdent());
        }

        relasjonService.setRelasjoner(ident, RelasjonType.VERGE_MOTTAKER,
                vergemaal.getVergeIdent(), RelasjonType.VERGE);
    }
}