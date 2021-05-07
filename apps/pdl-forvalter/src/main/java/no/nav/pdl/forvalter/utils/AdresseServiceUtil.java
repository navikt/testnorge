package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.pdl.forvalter.domain.PdlAdresse;
import org.springframework.web.client.HttpClientErrorException;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@UtilityClass
public class AdresseServiceUtil {

    public static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";
    public static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    public static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    public static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    public static final String VALIDATION_GYLDIGHET_ABSENT_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha " +
            "verdi for vegadresse uten matrikkelId";

    public static void validateMasterPdl(PdlAdresse adresse) {
        if (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed())) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_GYLDIGHET_ABSENT_ERROR);
        }
    }

    public static void validateBruksenhet(String bruksenhet) {
        if (isNotBlank(bruksenhet.replaceAll("[HULK][0-9]{4}", ""))) {
            throw new HttpClientErrorException(BAD_REQUEST, VALIDATION_BRUKSENHET_ERROR);
        }
    }
}
