package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.nav.pdl.forvalter.utils.ArtifactUtils.hasSpraak;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class TilrettelagtKommunikasjonService extends PdlArtifactService<TilrettelagtKommunikasjonDTO> {

    private static final String VALIDATION_NO_SPRAAK_ERROR = "Språk for taletolk og/eller tegnspråktolk må oppgis";
    private static final String VALIDATION_TOLKESPRAAK_ERROR = "Språk for taletolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";
    private static final String VALIDATION_TEGNSPRAAK_ERROR = "Språk for tegnspråktolk er ugyldig: forventet 2 tegn i hht kodeverk Språk";

    @Override
    public void validate(TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

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
    }

    @Override
    protected void handle(TilrettelagtKommunikasjonDTO tilrettelagtKommunikasjon) {

        tilrettelagtKommunikasjon.setMaster(DbVersjonDTO.Master.PDL);
    }

    @Override
    protected void enforceIntegrity(List<TilrettelagtKommunikasjonDTO> type) {

        //Ingen validering for liste
    }
}
