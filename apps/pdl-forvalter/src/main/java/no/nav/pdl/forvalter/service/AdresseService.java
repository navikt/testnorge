package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.logging.log4j.util.Strings.isBlank;

public abstract class AdresseService<T extends AdresseDTO> {

    public static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";
    public static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    public static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    public static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    public static final String VALIDATION_GYLDIGHET_ABSENT_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha " +
            "verdi for vegadresse uten matrikkelId";
    protected static final String VALIDATION_ADRESSE_OVELAP_ERROR = "Feil: Overlappende adressedatoer er ikke lov";

    protected static void validateMasterPdl(AdresseDTO adresse) {
        if (isNull(adresse.getGyldigFraOgMed()) || isNull(adresse.getGyldigTilOgMed())) {
            throw new InvalidRequestException(VALIDATION_GYLDIGHET_ABSENT_ERROR);
        }
    }

    protected static void validateBruksenhet(String bruksenhet) {
        if (!bruksenhet.matches("[HULK][0-9]{4}")) {
            throw new InvalidRequestException(VALIDATION_BRUKSENHET_ERROR);
        }
    }

    protected static <T> int count(T artifact) {
        return nonNull(artifact) ? 1 : 0;
    }

    public List<T> convert(List<T> request) {

        for (var type : request) {

            if (isTrue(type.getIsNew())) {
                validate(type);

                handle(type);
                if (isBlank(type.getKilde())) {
                    type.setKilde("Dolly");
                }
            }
        }
        enforceIntegrity(request);
        return request;
    }

    protected abstract void validate(T type);

    protected abstract void handle(T type);


    protected void enforceIntegrity(List<T> adresse) {

        for (var i = 0; i < adresse.size(); i++) {
            if (i + 1 < adresse.size()) {
                if (isNull(adresse.get(i + 1).getGyldigTilOgMed()) &&
                        nonNull(adresse.get(i).getGyldigFraOgMed()) && nonNull(adresse.get(i + 1).getGyldigFraOgMed()) &&
                        !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigFraOgMed().plusDays(1)) ||
                        nonNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed()) &&
                                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigTilOgMed())) {
                    throw new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR);
                }
                if (isNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed())) {
                    adresse.get(i + 1).setGyldigTilOgMed(adresse.get(i).getGyldigFraOgMed().minusDays(1));
                }
            }
        }
    }
}
