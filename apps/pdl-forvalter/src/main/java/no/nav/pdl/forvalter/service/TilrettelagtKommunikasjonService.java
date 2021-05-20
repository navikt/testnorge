package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.dto.RsTilrettelagtKommunikasjon;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.isSpraak;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class TilrettelagtKommunikasjonService extends PdlArtifactService<RsTilrettelagtKommunikasjon> {

    private static final String VALIDATION_NO_SPRAAK_ERROR = "Språk for taletolk og/eller tegnspråktolk må oppgis";
    private static final String VALIDATION_TOLKESPRAAK_ERROR = "Språk for taletolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";
    private static final String VALIDATION_TEGNSPRAAK_ERROR = "Språk for tegnspråktolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";

    @Override
    protected void validate(RsTilrettelagtKommunikasjon tilrettelagtKommunikasjon) {

        if (isBlank(tilrettelagtKommunikasjon.getSpraakForTaletolk()) &&
                isBlank(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_NO_SPRAAK_ERROR);
        }

        if (isNotBlank(tilrettelagtKommunikasjon.getSpraakForTaletolk()) &&
                (!isSpraak(tilrettelagtKommunikasjon.getSpraakForTaletolk()))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_TOLKESPRAAK_ERROR);
        }

        if (isNotBlank(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk()) &&
                !isSpraak(tilrettelagtKommunikasjon.getSpraakForTegnspraakTolk())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_TEGNSPRAAK_ERROR);
        }
    }

    @Override
    protected void handle(RsTilrettelagtKommunikasjon tilrettelagtKommunikasjon) {

        //Ingen håndtering for enkeltpost
    }

    @Override
    protected void enforceIntegrity(List<RsTilrettelagtKommunikasjon> type) {

        //Ingen validering for liste
    }
}
