package no.nav.pdl.forvalter.service;

import lombok.NoArgsConstructor;
import no.nav.pdl.forvalter.domain.PdlAdresse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@NoArgsConstructor
public abstract class AdresseService<T extends PdlAdresse> {

    protected static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";
    protected static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    protected static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    protected static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    protected static final String VALIDATION_GYLDIGHET_ABSENT_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha " +
            "verdi for vegadresse uten matrikkelId";

    protected static void validateMasterPdl(PdlAdresse adresse) {
        if (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_GYLDIGHET_ABSENT_ERROR);
        }
    }

    protected static void validateBruksenhet(String bruksenhet) {
        if (isNotBlank(bruksenhet.replaceAll("[HULK][0-9]{4}", ""))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_BRUKSENHET_ERROR);
        }
    }

    protected abstract void validateAdresse(T adresse);

    public abstract List<T> resolve(List<T> request);
}
