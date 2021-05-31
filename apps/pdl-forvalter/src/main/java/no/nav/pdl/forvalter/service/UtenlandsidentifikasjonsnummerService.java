package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UtenlandsidentifikasjonsnummerService extends PdlArtifactService<PdlUtenlandskIdentifikasjonsnummer> {

    private static final String VALIDATION_ID_NUMMER_MISSING = "Utenlandsk identifikasjonsnummer må oppgis";
    private static final String VALIDATION_OPPHOERT_MISSING = "Er utenlandsk identifikasjonsnummer opphørt? Må angis";
    private static final String VALIDATION_UTSTEDER_LAND_MISSING = "Utsteder land må oppgis";
    private static final String VALIDATION_LANDKODE_ILLEGAL_FORMAT = "Trebokstavers landkode er forventet på utstederland";

    @Override
    protected void validate(PdlUtenlandskIdentifikasjonsnummer identifikasjon) {

        if (isBlank(identifikasjon.getIdentifikasjonsnummer())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_ID_NUMMER_MISSING);
        }

        if (isNull(identifikasjon.getOpphoert())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_OPPHOERT_MISSING);
        }

        if (isNull(identifikasjon.getUtstederland())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_UTSTEDER_LAND_MISSING);
        }

        if (!identifikasjon.getUtstederland().matches("[0-9]{3}")) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_LANDKODE_ILLEGAL_FORMAT);
        }
    }

    @Override
    protected void handle(PdlUtenlandskIdentifikasjonsnummer innflytting) {

        // Ingen håndtering av data
    }

    @Override
    protected void enforceIntegrity(List<PdlUtenlandskIdentifikasjonsnummer> innflytting) {

        // Ingen referanseintegritet å håndtere
    }
}
