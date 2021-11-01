package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
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
                type.setKilde(isNotBlank(type.getKilde()) ? type.getKilde() : "Dolly");
                type.setMaster(nonNull(type.getMaster()) ? type.getMaster() : DbVersjonDTO.Master.FREG);
                type.setGjeldende(nonNull(type.getGjeldende()) ? type.getGjeldende() : true);
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

    private void handle(VergemaalDTO fullmakt, String ident) {

        if (isBlank(fullmakt.getVergeIdent())) {

            if (isNull(fullmakt.getNyVergeIdent())) {
                fullmakt.setNyVergeIdent(new PersonRequestDTO());
            }

            if (isNull(fullmakt.getNyVergeIdent().getAlder()) &&
                    isNull(fullmakt.getNyVergeIdent().getFoedtEtter()) &&
                    isNull(fullmakt.getNyVergeIdent().getFoedtFoer())) {

                fullmakt.getNyVergeIdent().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyVergeIdent().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            if (isNull(fullmakt.getNyVergeIdent().getSyntetisk())) {
                fullmakt.getNyVergeIdent().setSyntetisk(SyntetiskFraIdentUtility.isSyntetisk(ident));
            }

            fullmakt.setVergeIdent(createPersonService.execute(fullmakt.getNyVergeIdent()).getIdent());

        } else {

            fullmakt.setIsIdentExternal(true);
        }

        relasjonService.setRelasjoner(ident, RelasjonType.VERGE_MOTTAKER,
                fullmakt.getVergeIdent(), RelasjonType.VERGE);
    }
}