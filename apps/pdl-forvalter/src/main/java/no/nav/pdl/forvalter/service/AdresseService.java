package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.utils.FoedselsdatoUtility;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
    private static final int OUT_OF_BOUND = -1;

    private final GenererNavnServiceConsumer genererNavnServiceConsumer;

    protected AdresseService(GenererNavnServiceConsumer genererNavnServiceConsumer) {
        this.genererNavnServiceConsumer = genererNavnServiceConsumer;
    }

    private static String blankCheck(String value, String defaultValue) {
        return isNotBlank(value) ? value : defaultValue;
    }

    protected static void validateBruksenhet(String bruksenhet) {
        if (!bruksenhet.matches("[HULK]\\d{4}")) {
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
                .append(isNotBlank(coNavn.getMellomnavn()) ? coNavn.getMellomnavn() : "")
                .append(isNotBlank(coNavn.getMellomnavn()) ? ' ' : "")
                .append(coNavn.getEtternavn())
                .toString();
    }


    protected void oppdaterAdressedatoer(List<? extends AdresseDTO> adresser, PersonDTO person) {

        if (!adresser.isEmpty()) {

            if (isNull(adresser.getLast().getGyldigFraOgMed())) {
                adresser.getLast().setGyldigFraOgMed(FoedselsdatoUtility.getFoedselsdato(person));
            }

            setPendingFromDato(adresser);
            var startIndex = adresser.size() - 1;
            setGyldigFromDato(adresser, startIndex);
            setGyldigTomDato(adresser);
        }
    }

    private static void setGyldigTomDato(List<? extends AdresseDTO> adresser) {

        for (int i = adresser.size() - 1; i > 0; i--) {
            if (isNull(adresser.get(i).getGyldigTilOgMed())) {
                adresser.get(i).setGyldigTilOgMed(adresser.get(i - 1).getGyldigFraOgMed().minusDays(1));
            }
        }
    }

    private static void setPendingFromDato(List<? extends AdresseDTO> adresser) {

        for (int i = adresser.size() - 1; i > 0; i--) {
            if (isNull(adresser.get(i - 1).getGyldigFraOgMed()) && nonNull(adresser.get(i).getGyldigTilOgMed())) {
                adresser.get(i - 1).setGyldigFraOgMed(adresser.get(i).getGyldigTilOgMed().plusDays(1));
            }
        }
    }

    private static void setGyldigFromDato(List<? extends AdresseDTO> adresser, int startIndex) {

        if (startIndex == OUT_OF_BOUND) {
            return;
        }
        var lowWaterMark = getIndexOfNextFromDate(adresser, startIndex);
        var daysInterval = getWeightedDaysInterval(adresser, startIndex, lowWaterMark);
        for (int i = startIndex - 1; i > lowWaterMark; i--) {
            adresser.get(i).setGyldigFraOgMed(adresser.get(i + 1).getGyldigFraOgMed().plusDays(daysInterval));
        }
        setGyldigFromDato(adresser, lowWaterMark);
    }


    private static long getWeightedDaysInterval(List<? extends
            AdresseDTO> adresser, int index1, int index2) {

        var interval = Duration.between(adresser.get(index1).getGyldigFraOgMed(),
                index2 == OUT_OF_BOUND ? LocalDateTime.now() :
                        adresser.get(index2).getGyldigFraOgMed()).toDays();

        return (interval - interval / (index1 - index2)) / (index1 - index2);
    }

    private static int getIndexOfNextFromDate(List<? extends
            AdresseDTO> adresser, int index) {

        for (var i = index - 1; i >= 0; i--) {
            if (nonNull(adresser.get(i).getGyldigFraOgMed())) {
                return i;
            }
        }
        return OUT_OF_BOUND;
    }

    protected String getMatrikkelId(AdresseDTO adresse, String ident, String matrikkelId) {

        return isNotNpidIdent(ident) && !isTestnorgeIdent(ident) && !adresse.isPdlMaster() ? matrikkelId : null;
    }
}
