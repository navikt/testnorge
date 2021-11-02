package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
public class UtenlandsidentifikasjonsnummerService extends PdlArtifactService<UtenlandskIdentifikasjonsnummerDTO> {

    private static final String VALIDATION_ID_NUMMER_MISSING = "Utenlandsk identifikasjonsnummer må oppgis";
    private static final String VALIDATION_OPPHOERT_MISSING = "Er utenlandsk identifikasjonsnummer opphørt? Må angis";
    private static final String VALIDATION_UTSTEDER_LAND_MISSING = "Utsteder land må oppgis";
    private static final String VALIDATION_LANDKODE_ILLEGAL_FORMAT = "Trebokstavers landkode er forventet på utstederland";

    @Override
    public void validate(UtenlandskIdentifikasjonsnummerDTO identifikasjon) {

        if (isBlank(identifikasjon.getIdentifikasjonsnummer())) {
            throw new InvalidRequestException(VALIDATION_ID_NUMMER_MISSING);
        }

        if (isNull(identifikasjon.getOpphoert())) {
            throw new InvalidRequestException(VALIDATION_OPPHOERT_MISSING);
        }

        if (isNull(identifikasjon.getUtstederland())) {
            throw new InvalidRequestException(VALIDATION_UTSTEDER_LAND_MISSING);
        }

        if (!identifikasjon.getUtstederland().matches("[A-Z]{3}")) {
            throw new InvalidRequestException(VALIDATION_LANDKODE_ILLEGAL_FORMAT);
        }
    }

    @Override
    protected void handle(UtenlandskIdentifikasjonsnummerDTO identifikasjon) {

        // Ingen håndtering av data
    }

    @Override
    protected void enforceIntegrity(List<UtenlandskIdentifikasjonsnummerDTO> identifikasjon) {

        // Ingen referanseintegritet å håndtere
    }
}
