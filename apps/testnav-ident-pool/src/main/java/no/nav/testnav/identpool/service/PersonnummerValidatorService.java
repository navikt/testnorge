package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Personidentifikator;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
     * @param erSyntetisk Angir om ident-nummeret er syntetisk.
     * @return true hvis ident-nummeret er gyldig, ellers kaster en IllegalArgumentException.
     * @throws IllegalArgumentException hvis ident-nummeret har ugyldig format eller ikke er gyldig bygget opp.
     */
    private static boolean validerInput(String gittNummer, boolean erSyntetisk) {
        boolean gyldigFormat = gittNummer.matches("^\\d{11}$");

        if (!gyldigFormat) {
            throw new IllegalArgumentException("Ugyldig format: ident må være 11 sifre");
        }

        String dato = gittNummer.substring(0, 6);

        if (erDnummer(gittNummer)) {
            int dagSiffer = Character.getNumericValue(dato.charAt(0));
            dato = (dagSiffer - 4) + dato.substring(1, 6);
        }

        if (erSyntetisk) {
            int maanedSiffer = Character.getNumericValue(dato.charAt(2));
            if (Arrays.stream(SYNTETISKE_MAANED_SIFRE).noneMatch(siffer -> siffer == maanedSiffer)) {
                throw new IllegalArgumentException("Ugyldig format: " + gittNummer + " syntetiske nummer må ha 4, 5, 6, 7, 8 eller 9 på indeks 2");
            }
            // utled kalenderdato fra syntetisk nummer
            dato = dato.substring(0, 2) + (maanedSiffer % 2) + dato.substring(3, 6);
        }

        if (!erDatoGyldig(dato)) {
            throw new IllegalArgumentException(
                    "Ugyldig format: " + gittNummer + " har ugyldig dato " + dato + " i formatet ddMMyy");
        }

        if (!validerKontrollsiffer(gittNummer, false)) {
            throw new IllegalArgumentException(
                    "Ugyldig ident: " + gittNummer + " har ugyldige kontrollsifre");
        }
        return true;
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
        var feilmelding = new AtomicReference<String>();
        var erGyldig = new AtomicBoolean();
        boolean erStriktFoedselsnummer64 = validerKontrollsiffer(foedselsnummer, true);
        var kjoenn = parseInt(foedselsnummer.substring(8, 9)) % 2 == 0 ? KVINNE : MANN;
        try {
            erGyldig.set(validerInput(foedselsnummer, erSyntetisk));
        } catch (IllegalArgumentException e) {
            erGyldig.set(false);
            feilmelding.set(e.getMessage());
        }

        return Mono.zip(identRepository.findByPersonidentifikator(foedselsnummer)
                                .switchIfEmpty(Mono.defer(() -> Mono.just(new Ident()))),
                        personidentifikatorRepository.findByPersonidentifikator(foedselsnummer)
                                .switchIfEmpty(Mono.defer(() -> Mono.just(new Personidentifikator()))))
                .map(tuple ->
                        new ValideringResponseDTO(
                                foedselsnummer,
                                utledIdenttype(foedselsnummer),
                                erTestnorgeIdent,
                                erSyntetisk,
                                erGyldig.get(),
                                !erGyldig.get() ? null : !erStriktFoedselsnummer64,
                                utledFoedselsdato(foedselsnummer, tuple.getT1(), tuple.getT2(), erStriktFoedselsnummer64),
                                erGyldig.get() && erStriktFoedselsnummer64 ? kjoenn : null,
                                !erGyldig.get() ? null : nonNull(tuple.getT1().getIdentity()) || nonNull(tuple.getT2().getId()),
                                feilmelding.get()));
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

    private static LocalDate utledFoedselsdato(String foedselsnummer, Ident ident, Personidentifikator personidentifikator, Boolean erStriktFoedselsnummer64) {

        if (nonNull(ident.getFoedselsdato())) {
            return ident.getFoedselsdato();
        } else if (nonNull(personidentifikator.getFoedselsdato())) {
            return personidentifikator.getFoedselsdato();
        } else if (isTrue(erStriktFoedselsnummer64)) {
            return DatoFraIdentUtility.getFoedselsdato(foedselsnummer);
        } else {
            return null;
        }
    }

    private static Kjoenn utledKjoenn(String foedselsnummer, Ident ident, Boolean erStriktFoedselsnummer64) {

        if (nonNull(ident.getKjoenn())) {
            return ident.getKjoenn();
        } else if (isTrue(erStriktFoedselsnummer64)) {
            return parseInt(foedselsnummer.substring(8, 9)) % 2 == 0 ? KVINNE : MANN;
        } else {
            return null;
        }
    }
}