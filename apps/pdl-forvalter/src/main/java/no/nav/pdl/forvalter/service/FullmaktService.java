package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.model.RelasjonType;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlFullmakt;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.RsPersonRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    public List<PdlFullmakt> convert(PdlPerson person) {

        for (var type : person.getFullmakt()) {

            if (type.isNew()) {
                validate(type);

                handle(type, person.getIdent());
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getFullmakt();
    }

    private void validate(PdlFullmakt fullmakt) {

        if (isNull(fullmakt.getOmraader())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OMRAADER_ERROR);
        }

        if (isNull(fullmakt.getGyldigFom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_GYLDIG_FOM_ERROR);

        } else if (isNull(fullmakt.getGyldigTom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_GYLDIG_TOM_ERROR);

        } else if (!fullmakt.getGyldigFom().isBefore(fullmakt.getGyldigTom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (nonNull(fullmakt.getFullmektig()) &&
                !personRepository.existsByIdent(fullmakt.getFullmektig())) {
            throw new HttpClientErrorException(BAD_REQUEST, format(VALIDATION_FULLMEKTIG_ERROR, fullmakt.getFullmektig()));
        }
    }

    private void handle(PdlFullmakt fullmakt, String ident) {

        if (isBlank(fullmakt.getFullmektig())) {

            if (isNull(fullmakt.getNyFullmektig())) {
                fullmakt.setNyFullmektig(new RsPersonRequest());
            }

            if (isNull(fullmakt.getNyFullmektig().getAlder()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtEtter()) &&
                    isNull(fullmakt.getNyFullmektig().getFoedtFoer())) {

                fullmakt.getNyFullmektig().setFoedtFoer(LocalDateTime.now().minusYears(18));
                fullmakt.getNyFullmektig().setFoedtEtter(LocalDateTime.now().minusYears(75));
            }

            fullmakt.setFullmektig(createPersonService.execute(fullmakt.getNyFullmektig()).getIdent());
            relasjonService.setRelasjoner(ident, RelasjonType.FULLMAKTSGIVER,
                    fullmakt.getFullmektig(), RelasjonType.FULLMEKTIG);
            fullmakt.setNyFullmektig(null);
        }
    }
}