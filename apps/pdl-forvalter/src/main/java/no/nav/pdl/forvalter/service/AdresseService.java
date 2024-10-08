package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getKilde;
import static no.nav.pdl.forvalter.utils.ArtifactUtils.getMaster;
import static no.nav.pdl.forvalter.utils.IdenttypeUtility.isNotNpidIdent;
import static no.nav.pdl.forvalter.utils.TestnorgeIdentUtility.isTestnorgeIdent;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class AdresseService<T extends AdresseDTO, R> implements BiValidation<T, R> {

    public static final String VALIDATION_BRUKSENHET_ERROR = "Bruksenhetsnummer identifiserer en boligenhet innenfor et " +
            "bygg eller en bygningsdel. Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre";
    public static final String VALIDATION_POSTBOKS_ERROR = "Alfanumerisk identifikator av postboks. Kan ikke være tom";
    public static final String VALIDATION_POSTNUMMER_ERROR = "Postnummer består av fire sifre";
    protected static final String VALIDATION_ADRESSE_OVELAP_ERROR = "Adresse: Overlappende adressedatoer er ikke lov";

    private static final String NAVN_INVALID_ERROR = "CoAdresseNavn er ikke i liste over gyldige verdier";

    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    protected AdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer) {
        this.genererNavnServiceConsumer = genererNavnServiceConsumer;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    protected static void validateBruksenhet(String bruksenhet) {
        if (!bruksenhet.matches("[HULK][0-9]{4}")) {
            throw new InvalidRequestException(VALIDATION_BRUKSENHET_ERROR);
        }
    }

    private static LocalDateTime getDateOrFuture(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime : LocalDateTime.now().plusYears(100);
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
                .append(isNotBlank(coNavn.getMellomnavn()) ? coNavn.getMellomnavn() : "")
                .append(isNotBlank(coNavn.getMellomnavn()) ? ' ' : "")
                .append(coNavn.getEtternavn())
                .toString();
    }

    protected void populateMiscFields(AdresseDTO adresse, PersonDTO person) {

        if (isNull(adresse.getGyldigFraOgMed())) {
            adresse.setGyldigFraOgMed(person.getBostedsadresse().stream()
                    .reduce((a1, a2) -> a2)
                    .map(BostedadresseDTO::getGyldigFraOgMed)
                    .filter(Objects::nonNull)
                    .orElse(FoedselsdatoUtility.getFoedselsdato(person)));
        }

        adresse.setKilde(getKilde(adresse));
        adresse.setMaster(getMaster(adresse, person));
    }

    protected void enforceIntegrity(List<T> adresser) {

        sortAdresser(adresser);
        setPendingTilOgMedDato(adresser);
        checkOverlappendeDatoer(adresser);
    }

    private void setPendingTilOgMedDato(List<T> adresser) {

        for (var i = 0; i < adresser.size(); i++) {

            if (i + 1 < adresser.size() &&
                    (isNull(adresser.get(i + 1).getGyldigTilOgMed()) &&
                            nonNull(adresser.get(i).getGyldigFraOgMed()) ||
                            adresser.get(i + 1).getGyldigTilOgMed()
                                    .isAfter(adresser.get(i).getGyldigFraOgMed()))) {

                if (adresser.get(i + 1).getGyldigFraOgMed().toLocalDate().isEqual(
                        adresser.get(i).getGyldigFraOgMed().toLocalDate())) {

                    var time = LocalDateTime.now();
                    adresser.get(i).setGyldigFraOgMed(adresser.get(i).getGyldigFraOgMed().toLocalDate()
                            .atTime(time.getHour(), time.getMinute(), time.getSecond()));
                }

                adresser.get(i + 1).setGyldigTilOgMed(getGyldigTilDato(adresser.get(i + 1), adresser.get(i)));
            }
        }
    }

    private LocalDateTime getGyldigTilDato(AdresseDTO adresse1, AdresseDTO adresse2) {

        if (adresse1.getGyldigFraOgMed().toLocalDate()
                .isEqual(adresse2.getGyldigFraOgMed().toLocalDate()) ||
                adresse1.getGyldigFraOgMed().toLocalDate()
                        .isEqual(adresse2.getGyldigFraOgMed().toLocalDate().minusDays(1))) {

            var time = adresse2.getGyldigFraOgMed().minusSeconds(1);
            return adresse2.getGyldigFraOgMed().toLocalDate()
                    .atTime(time.getHour(), time.getMinute(), time.getSecond());

        } else if (adresse1.getGyldigFraOgMed().toLocalDate()
                .isBefore(adresse2.getGyldigFraOgMed().toLocalDate())) {

            return adresse2.getGyldigFraOgMed().minusDays(1).toLocalDate().atStartOfDay();

        } else {

            throw new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR);
        }
    }

    private void checkOverlappendeDatoer(List<T> adresser) {

        // https://stackoverflow.com/questions/13513932/algorithm-to-detect-overlapping-periods
        for (var i = 0; i < adresser.size(); i++) {
            for (var j = 0; j < adresser.size(); j++) {
                if (i != j &&
                        (adresser.get(i).getGyldigFraOgMed()
                                .isEqual(adresser.get(j).getGyldigFraOgMed()) ||
                                getDateOrFuture(adresser.get(i).getGyldigTilOgMed())
                                        .isEqual(getDateOrFuture(adresser.get(j).getGyldigTilOgMed())) ||
                                isAdresseOverlapp(adresser.get(i), adresser.get(j)))) {
                    throw new InvalidRequestException(VALIDATION_ADRESSE_OVELAP_ERROR);
                }
            }
        }
    }

    private boolean isAdresseOverlapp(T adresse1, T adresse2) {

        return isOverlappTilfelle1(adresse1, adresse2) ||
                isOverlappTilfelle2(adresse1, adresse2) ||
                isOverlappTilfelle3(adresse1, adresse2);
    }

    private boolean isOverlappTilfelle1(T adresse1, T adresse2) {

        //        |<---- Intervall A ----->|
        //             |<---- Intervall B ----->|
        return getDateOrFuture(adresse1.getGyldigFraOgMed()).isBefore(adresse2.getGyldigFraOgMed()) &&
                adresse2.getGyldigFraOgMed().isBefore(getDateOrFuture(adresse1.getGyldigTilOgMed()));
    }

    private boolean isOverlappTilfelle2(T adresse1, T adresse2) {

        //             |<---- Intervall A ----->|
        //        |<---- Intervall B ----->|
        return adresse1.getGyldigFraOgMed().isBefore(getDateOrFuture(adresse2.getGyldigTilOgMed())) &&
                getDateOrFuture(adresse2.getGyldigTilOgMed()).isBefore(getDateOrFuture(adresse1.getGyldigTilOgMed()));
    }

    private boolean isOverlappTilfelle3(T adresse1, T adresse2) {

        //          |<--- Intervall A --->|
        //      |<-------- Intervall B ------->|
        return adresse2.getGyldigFraOgMed().isBefore(adresse1.getGyldigFraOgMed()) &&
                getDateOrFuture(adresse2.getGyldigTilOgMed()).isAfter(getDateOrFuture(adresse1.getGyldigTilOgMed()));
    }

    private void sortAdresser(List<T> adresser) {

        if (adresser.isEmpty()) {
            return;
        }
        adresser.stream()
                .filter(adresse -> isNull(adresse.getGyldigFraOgMed()))
                .forEach(adresse -> adresse.setGyldigFraOgMed(LocalDateTime.now()));
        adresser.sort(Comparator.comparing(AdresseDTO::getGyldigFraOgMed, Comparator.reverseOrder()));
        for (var i = adresser.size(); i > 0; i--) {
            adresser.get(i - 1).setId(adresser.size() - i + 1);
        }
    }
    protected boolean isIdSupported(AdresseDTO adresse, String ident) {

        return isNotNpidIdent(ident) && !isTestnorgeIdent(ident) && !adresse.isPdlMaster();
    }
}
