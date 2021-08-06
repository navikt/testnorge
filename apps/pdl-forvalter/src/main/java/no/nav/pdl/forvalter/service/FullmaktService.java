package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class FullmaktService {

    private static final String VALIDATION_GYLDIG_FOM_ERROR = "Fullmakt med gyldigFom må angis";
    private static final String VALIDATION_GYLDIG_TOM_ERROR = "Fullmakt med gyldigTom må angis";
    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_OMRAADER_ERROR = "Omraader for fullmakt må angis";
    private static final String VALIDATION_FULLMEKTIG_ERROR = "Fullmektig: person %s ikke funnet i database";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public List<FullmaktDTO> convert(PersonDTO person) {

        for (var type : person.getFullmakt()) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                handle(type, person.getIdent());
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getFullmakt();
    }

    private void validate(FullmaktDTO fullmakt) {

        if (isNull(fullmakt.getOmraader())) {
            throw new InvalidRequestException(VALIDATION_OMRAADER_ERROR);
        }

        if (isNull(fullmakt.getGyldigFom())) {
            throw new InvalidRequestException(VALIDATION_GYLDIG_FOM_ERROR);

        } else if (isNull(fullmakt.getGyldigTom())) {
            throw new InvalidRequestException(VALIDATION_GYLDIG_TOM_ERROR);

        } else if (!fullmakt.getGyldigFom().isBefore(fullmakt.getGyldigTom())) {
            throw new InvalidRequestException(VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (nonNull(fullmakt.getFullmektig()) &&
                !personRepository.existsByIdent(fullmakt.getFullmektig())) {
            throw new InvalidRequestException(format(VALIDATION_FULLMEKTIG_ERROR, fullmakt.getFullmektig()));
        }
    }

    private void handle(FullmaktDTO fullmakt, String ident) {

        if (isBlank(fullmakt.getFullmektig())) {

            if (isNull(fullmakt.getNyFullmektig())) {
                fullmakt.setNyFullmektig(new PersonRequestDTO());
            }

            if (isNull(fullmakt.getNyFullmektig().getAlder()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtEtter()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtFoer())) {

                fullmakt.getNyFullmektig().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyFullmektig().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            if (fullmakt.getNyFullmektig().getSyntetisk()) {
                fullmakt.getNyFullmektig().setSyntetisk(SyntetiskFraIdentUtility.isSyntetisk(ident));
            }

            fullmakt.setFullmektig(createPersonService.execute(fullmakt.getNyFullmektig()).getIdent());
            relasjonService.setRelasjoner(ident, RelasjonType.FULLMAKTSGIVER,
                    fullmakt.getFullmektig(), RelasjonType.FULLMEKTIG);
            fullmakt.setNyFullmektig(null);
        }
    }
}