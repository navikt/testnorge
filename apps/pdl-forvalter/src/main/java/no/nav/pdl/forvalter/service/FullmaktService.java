package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.RsFullmakt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class FullmaktService extends PdlArtifactService<RsFullmakt> {

    private static final String VALIDATION_GYLDIG_FOM_ERROR = "Fullmakt med gyldigFom må angis";
    private static final String VALIDATION_GYLDIG_TOM_ERROR = "Fullmakt med gyldigTom må angis";
    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_OMRAADER_ERROR = "Omraader for fullmakt må angis";
    private static final String VALIDATION_FULLMEKTIG_ERROR = "Fullmektig: person %s ikke funnet i database";

    private final PersonRepository personRepository;

    @Override
    protected void validate(RsFullmakt fullmakt) {

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

        if (nonNull(fullmakt.getFullmektig()) && isNotBlank(fullmakt.getFullmektig().getIdent()) &&
                !personRepository.existsByIdent(fullmakt.getFullmektig().getIdent())) {
            throw new HttpClientErrorException(BAD_REQUEST, format(VALIDATION_FULLMEKTIG_ERROR, fullmakt.getFullmektig().getIdent()));
        }
    }

    @Override
    protected void handle(RsFullmakt type) {

        // Ingen håndtering for enkeltpost
    }

    @Override
    protected void enforceIntegrity(List<RsFullmakt> opphold) {

        // Ingen integritetssjekk
    }
}