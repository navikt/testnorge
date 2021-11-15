package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AdresseService<T extends AdresseDTO, R> implements BiValidation<T, R> {

    public static final String VALIDATION_MASTER_PDL_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi " +
            "hvis master er PDL";
    public static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    public static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    public static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    public static final String VALIDATION_GYLDIGHET_ABSENT_ERROR = "Feltene gyldigFraOgMed og gyldigTilOgMed må ha " +
            "verdi for vegadresse uten matrikkelId";
    protected static final String VALIDATION_ADRESSE_OVELAP_ERROR = "Adresse: Overlappende adressedatoer er ikke lov";

    private static final String NAVN_INVALID_ERROR = "CoAdresseNavn er ikke i liste over gyldige verdier";

    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    protected AdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer) {
        this.genererNavnServiceConsumer = genererNavnServiceConsumer;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

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

    protected void validateCoAdresseNavn(AdresseDTO.CoNavnDTO navn) {

        if ((isNotBlank(navn.getFornavn()) ||
                isNotBlank(navn.getMellomnavn()) ||
                isNotBlank(navn.getEtternavn())) &&
                isFalse(genererNavnServiceConsumer.verifyNavn(NavnDTO.builder()
                        .adjektiv(navn.getFornavn())
                        .adverb(navn.getMellomnavn())
                        .substantiv(navn.getEtternavn())
                        .build()))) {
            throw new InvalidRequestException(NAVN_INVALID_ERROR);
        }
    }

    protected String genererCoNavn(AdresseDTO.CoNavnDTO coNavn) {

        if (nonNull(coNavn)) {
            if (StringUtils.isBlank(coNavn.getFornavn()) || StringUtils.isBlank(coNavn.getEtternavn()) ||
                    (StringUtils.isBlank(coNavn.getMellomnavn()) && isTrue(coNavn.getHasMellomnavn()))) {

                var nyttNavn = genererNavnServiceConsumer.getNavn(1);
                if (nyttNavn.isPresent()) {
                    coNavn.setFornavn(blankCheck(coNavn.getFornavn(), nyttNavn.get().getAdjektiv()));
                    coNavn.setEtternavn(blankCheck(coNavn.getEtternavn(), nyttNavn.get().getSubstantiv()));
                    coNavn.setMellomnavn(blankCheck(coNavn.getMellomnavn(),
                            isTrue(coNavn.getHasMellomnavn()) ? nyttNavn.get().getAdverb() : null));
                }
            }
            return buildNavn(coNavn);
        }
        return null;
    }

    private String buildNavn(AdresseDTO.CoNavnDTO coNavn) {
        return new StringBuilder()
                .append("c/o ")
                .append(coNavn.getFornavn())
                .append(' ')
                .append(isTrue(coNavn.getHasMellomnavn()) ? coNavn.getMellomnavn() : "")
                .append(isTrue(coNavn.getHasMellomnavn()) ? ' ' : "")
                .append(coNavn.getEtternavn())
                .toString();
    }

    protected void populateMiscFields(AdresseDTO adresse, PersonDTO person) {

//        if (isNull(adresse.getGyldigFraOgMed())) {
//            adresse.setGyldigFraOgMed(person.getBostedsadresse().stream()
//                    .reduce((a1, a2) -> a2)
//                    .filter(adr -> isNull(adr.getGyldigFraOgMed()))
//                    .map(adr -> DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay())
//                    .orElse(LocalDateTime.now()));
//        }
        adresse.setKilde(StringUtils.isNotBlank(adresse.getKilde()) ? adresse.getKilde() : "Dolly");
        adresse.setMaster(nonNull(adresse.getMaster()) ? adresse.getMaster() : DbVersjonDTO.Master.FREG);
        adresse.setGjeldende(nonNull(adresse.getGjeldende()) ? adresse.getGjeldende(): true);
    }

    protected void enforceIntegrity(List<T> adresse) {

        for (var i = 0; i < adresse.size(); i++) {
            if (i + 1 < adresse.size()) {
                if (isOverlapGyldigTom(adresse, i) || isOverlapGyldigFom(adresse, i)) {
                    throw new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR);
                }
                if (isNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed())) {
                    adresse.get(i + 1).setGyldigTilOgMed(adresse.get(i).getGyldigFraOgMed().minusDays(1));
                }
            }
        }
    }

    private boolean isOverlapGyldigFom(List<T> adresse, int i) {
        return nonNull(adresse.get(i + 1).getGyldigTilOgMed()) && nonNull(adresse.get(i).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigTilOgMed());
    }

    private boolean isOverlapGyldigTom(List<T> adresse, int i) {
        return isNull(adresse.get(i + 1).getGyldigTilOgMed()) &&
                nonNull(adresse.get(i).getGyldigFraOgMed()) && nonNull(adresse.get(i + 1).getGyldigFraOgMed()) &&
                !adresse.get(i).getGyldigFraOgMed().isAfter(adresse.get(i + 1).getGyldigFraOgMed().plusDays(1));
    }
}
