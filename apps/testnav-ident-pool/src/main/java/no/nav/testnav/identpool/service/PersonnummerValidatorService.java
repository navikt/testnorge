package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Ident2032;
import no.nav.testnav.identpool.dto.ValideringResponseDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.util.DatoFraIdentUtility;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.util.Objects.nonNull;
import static no.nav.testnav.identpool.domain.Kjoenn.KVINNE;
import static no.nav.testnav.identpool.domain.Kjoenn.MANN;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class PersonnummerValidatorService {

    private static final int[] VEKTER_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] VEKTER_K2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final int[] GYLDIG_REST_K1 = {0, 1, 2, 3};
    private static final int GYLDIG_REST_K2 = 0;
    private static final List<Integer> DNR_SIFFERE = List.of(4, 5, 6, 7);
    private static final List<Integer> FNR_SIFFERE = List.of(0, 1, 4, 5);
    private static final List<Integer> NPID_SIFFERE = List.of(2, 3, 6, 7);
    private static final int[] SYNTETISKE_MAANED_SIFRE = {4, 5, 6, 7, 8, 9};

    private final PersonidentifikatorRepository personidentifikatorRepository;
    private final IdentRepository identRepository;

    /**
     * Validerer et fødsels-eller-d-nummer(1964 og 2032-type) ved å sjekke kontrollsifrene iht.
     * <a href="https://skatteetaten.github.io/folkeregisteret-api-dokumentasjon/nytt-fodselsnummer-fra-2032/">...</a>
     *
     * @param ident 11-siffret FNR, DNR eller NPID som skal valideres.
     * @return true hvis gyldig, ellers false
     */
    private static boolean validerKontrollsiffer(String ident, boolean erStrictFoedselsnummer64) {
        final int[] sifre = konverterTilIntArray(ident);
        final int gittK1 = sifre[9];
        final int gittK2 = sifre[10];

        final int[] grunnlagK1 = Arrays.copyOfRange(sifre, 0, VEKTER_K2.length);
        final int vektetK1 = IntStream.range(0, VEKTER_K1.length)
                .map(i -> grunnlagK1[i] * VEKTER_K1[i])
                .sum();

        final int beregnetRestSifferK1 = (vektetK1 + gittK1) % 11;

        if (erStrictFoedselsnummer64) {
            return beregnetRestSifferK1 == 0;
        }

        if (Arrays.stream(GYLDIG_REST_K1).noneMatch(siffer -> siffer == beregnetRestSifferK1)) {
            return false;
        }

        final int[] grunnlagK2 = Arrays.copyOfRange(sifre, 0, VEKTER_K2.length);
        final int vektetK2 = IntStream.range(0, VEKTER_K2.length)
                .map(i -> grunnlagK2[i] * VEKTER_K2[i])
                .sum();

        final int beregnetRestSifferK2 = (vektetK2 + gittK2) % 11;

        return beregnetRestSifferK2 == GYLDIG_REST_K2;
    }

    /**
     * Konverterer en streng til et array av heltall.
     *
     * @param streng strengen som skal konverteres
     * @return array av heltall
     */
    private static int[] konverterTilIntArray(String streng) {
        int[] ints = new int[streng.length()];

        for (int i = 0; i < streng.length(); i++) {
            ints[i] = parseInt(streng.substring(i, i + 1));
        }
        return ints;
    }

    /**
     * Validerer at gitt ident har gyldig format og dato før den kaller selve valideringen.
     *
     * @param gittNummer  ident-nummer som skal valideres.
     * @return OK ident-nummeret er gyldig, ellers en feilmelding.
     */
    private static String validerInput(String gittNummer) {
        boolean gyldigFormat = gittNummer.matches("^\\d{11}$");

        if (!gyldigFormat) {
            return "Ugyldig format: ident må være 11 sifre";
        }

        String dato = gittNummer.substring(0, 6);

        if (erDnummer(gittNummer)) {
            int dagSiffer = Character.getNumericValue(dato.charAt(0));
            dato = (dagSiffer - 4) + dato.substring(1, 6);
        }

        int maanedSiffer = Character.getNumericValue(dato.charAt(2));
        if (maanedSiffer >= '4' && Arrays.stream(SYNTETISKE_MAANED_SIFRE).noneMatch(siffer -> siffer == maanedSiffer)) {
                return "Ugyldig format: " + gittNummer + " syntetiske nummer må ha 4, 5, 6, 7, 8 eller 9 på indeks 2";
        }
        dato = dato.substring(0, 2) + (maanedSiffer % 2) + dato.substring(3, 6);

        if (!erDatoGyldig(dato)) {
            return "Ugyldig format: " + gittNummer + " har ugyldig dato " + dato + " i formatet ddMMyy";
        }

        if (!validerKontrollsiffer(gittNummer, false)) {
            return "Ugyldig ident: " + gittNummer + " har ugyldige kontrollsifre";
        }
        return "OK";
    }

    /**
     * Sjekker om et gitt nummer er et D-nummer.
     *
     * @param gittNummer Nummeret som skal sjekkes.
     * @return true hvis nummeret er et D-nummer, ellers false.
     */
    private static boolean erDnummer(String gittNummer) {

        return DNR_SIFFERE.contains(Character.getNumericValue(gittNummer.charAt(0)));
    }

    /**
     * Sjekker om en gitt dato finnes på en kalender. Da århundre ikke lengre vil kunne utledes av
     * 2032-fødselsnumre, antas alle datoer å være etter år 2000.
     *
     * @param dato Datoen som skal sjekkes i formatet ddMMyy.
     * @return true hvis datoen er gyldig, ellers false.
     */
    private static boolean erDatoGyldig(String dato) {
        final String aarhundre = "20";
        final String aar = dato.substring(4, 6);
        final String maaned = dato.substring(2, 4);
        final String dag = dato.substring(0, 2);
        final boolean erSkuddag = "2902".equals(dag + maaned);
        if (erSkuddag && !erSkuddaar(aar)) {
            return false;
        }
        try {
            LocalDate.parse(dag + maaned + aarhundre + aar, DateTimeFormatter.ofPattern("ddMMyyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Utleder om et gitt år er et skuddår basert på kun to sifre. Dette medører at man ikke
     * kan vite hvilket århundre det gjelder, så velger å anse '00' som skuddåret 2000.
     * Dette er grunnet i det ikke lengre vil være mulig å utlede århundre av 2032-fødselsnumre.
     *
     * @param aar året som skal sjekkes i formatet 'yy'.
     * @return true hvis året er et skuddår, ellers false.
     */
    private static boolean erSkuddaar(String aar) {
        return parseInt(aar) % 4 == 0;
    }

    public Mono<ValideringResponseDTO> validerFoedselsnummer(String foedselsnummer) {

        boolean erSyntetisk = foedselsnummer.charAt(2) >= '4';
        boolean erTestnorgeIdent = foedselsnummer.charAt(2) == '8' || foedselsnummer.charAt(2) == '9';
        var valideringResultat = validerInput(foedselsnummer);
        boolean erStriktFoedselsnummer64 = validerKontrollsiffer(foedselsnummer, true);
        var erGyldig = "OK".equals(valideringResultat);

        return Mono.zip(identRepository.findByPersonidentifikator(foedselsnummer)
                                .switchIfEmpty(Mono.defer(() -> Mono.just(new Ident()))),
                        personidentifikatorRepository.findByPersonidentifikator(foedselsnummer)
                                .switchIfEmpty(Mono.defer(() -> Mono.just(new Ident2032()))))
                .map(tuple ->
                        new ValideringResponseDTO(
                                foedselsnummer,
                                utledIdenttype(foedselsnummer),
                                erTestnorgeIdent,
                                erSyntetisk,
                                erGyldig,
                                erGyldig ? !erStriktFoedselsnummer64 : null,
                                erGyldig ? utledFoedselsdato(foedselsnummer, tuple.getT1(), tuple.getT2(), erStriktFoedselsnummer64) : null,
                                erGyldig ? utledKjoenn(foedselsnummer, tuple.getT1(), erStriktFoedselsnummer64) : null,
                                erGyldig ? null : valideringResultat,
                                erGyldig ? getKommentar(foedselsnummer, erStriktFoedselsnummer64,
                                        tuple.getT1(), tuple.getT2()) : null));
    }

    private static Identtype utledIdenttype(String ident) {

        if (erDnummer(ident)) {
            return Identtype.DNR;
        }
        if (FNR_SIFFERE.contains(Character.getNumericValue(ident.charAt(2)))) {
            return Identtype.FNR;
        }
        if (NPID_SIFFERE.contains(Character.getNumericValue(ident.charAt(2)))) {
            return Identtype.NPID;
        }
        return null;
    }

    private static LocalDate utledFoedselsdato(String foedselsnummer, Ident ident,
                                               Ident2032 ident2032,
                                               boolean erStriktFoedselsnummer64) {

        if (nonNull(ident.getFoedselsdato())) {
            return ident.getFoedselsdato();
        } else if (nonNull(ident2032.getFoedselsdato())) {
            return ident2032.getFoedselsdato();
        } else if (erStriktFoedselsnummer64) {
            return DatoFraIdentUtility.getFoedselsdato(foedselsnummer);
        } else {
            var foedselsdato = DatoFraIdentUtility.getFoedselsdato(foedselsnummer);
            var foedselsdatoPaa2000Tallet = LocalDate.of(foedselsdato.getYear() % 100 + 2000, foedselsdato.getMonth(), foedselsdato.getDayOfMonth());
            return foedselsdatoPaa2000Tallet.isAfter(LocalDate.now()) ? foedselsdatoPaa2000Tallet.minusYears(100) : foedselsdatoPaa2000Tallet;
        }
    }

    private static Kjoenn utledKjoenn(String foedselsnummer, Ident ident, Boolean erStriktFoedselsnummer64) {

        if (nonNull(ident.getKjoenn())) {
            return ident.getKjoenn();
        } else if (isTrue(erStriktFoedselsnummer64)) {
            return getKjoennFromIdent(foedselsnummer);
        } else {
            return null;
        }
    }

    private static String getKommentar(String foedselsnummer, boolean erStriktFoedselsnummer64,
                                       Ident ident, Ident2032 ident2032) {

        if (nonNull(ident2032.getId())) {
            return "Fødselsdato er hentet fra eksisterende ident i identpool. Århundre kan ikke utledes " +
                    "fra 2032-fødselsnummer, ei heller kjønn.";
        } else if (nonNull(ident.getIdentity())) {
            return "Fødselsdato og kjønn er hentet fra eksisterende ident i identpool." +
                    (getKjoennFromIdent(foedselsnummer) != ident.getKjoenn() ?
                    " Kjønn avledet fra fødselsnummer samsvarer ikke med lagret verdi fra identpool." : "");
        } else if (erStriktFoedselsnummer64) {
            return "Fødselsdato og kjønn er avledet fra fødselsnummer.";
        } else {
            return "2032-fødselsnummer mangler informasjon om århundre og kjønn. Fødselsdato er derfor avledet med antakelse om " +
                    "at personen er født på 1900- eller 2000-tallet basert på dagens dato.";
        }
    }

    private static Kjoenn getKjoennFromIdent(String ident) {

        return parseInt(ident.substring(8, 9)) % 2 == 0 ? KVINNE : MANN;
    }
}