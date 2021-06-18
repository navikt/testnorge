package no.nav.pdl.forvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class VergemaalService {

    private static final String VALIDATION_UGYLDIG_INTERVAL_ERROR = "Ugyldig datointervall: gyldigFom må være før gyldigTom";
    private static final String VALIDATION_EMBETE_ERROR = "Embete for vergemål må angis";
    private static final String VALIDATION_TYPE_ERROR = "Sakstype av vergemål må angis";
    private static final String VALIDATION_VERGEMAAL_ERROR = "Vergeperson med ident %s ikke funnet i database";

    private final PersonRepository personRepository;
    private final CreatePersonService createPersonService;
    private final RelasjonService relasjonService;

    public List<VergemaalDTO> convert(PersonDTO person) {

        for (var type : person.getVergemaal()) {

            if (type.isNew()) {
                validate(type);

                handle(type, person.getIdent());
                if (Strings.isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        return person.getVergemaal();
    }

    private void validate(VergemaalDTO vergemaal) {

        if (isNull(vergemaal.getEmbete())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_EMBETE_ERROR);
        }

        if (nonNull(vergemaal.getGyldigFom()) && nonNull(vergemaal.getGyldigTom()) &&
                !vergemaal.getGyldigFom().isBefore(vergemaal.getGyldigTom())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UGYLDIG_INTERVAL_ERROR);
        }

        if (isNull(vergemaal.getSakType())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_TYPE_ERROR);
        }

        if (isNotBlank(vergemaal.getVergeIdent()) &&
                !personRepository.existsByIdent(vergemaal.getVergeIdent())) {
            throw new HttpClientErrorException(BAD_REQUEST, format(VALIDATION_VERGEMAAL_ERROR, vergemaal.getVergeIdent()));
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

            fullmakt.setVergeIdent(createPersonService.execute(fullmakt.getNyVergeIdent()).getIdent());
            relasjonService.setRelasjoner(ident, RelasjonType.VERGE_MOTTAKER,
                    fullmakt.getVergeIdent(), RelasjonType.VERGE);
            fullmakt.setNyVergeIdent(null);
        }
    }
}