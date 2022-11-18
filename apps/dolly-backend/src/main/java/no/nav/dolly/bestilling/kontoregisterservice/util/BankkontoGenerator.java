package no.nav.dolly.bestilling.kontoregisterservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tpsmessagingservice.KontoregisterLandkode;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@UtilityClass
public class BankkontoGenerator {

    private static final int IBAN_COUNTRY_LENGTH = 2;
    private static final int DEFAULT_ACCOUNT_LENGTH = 15;
    private static final int NORSK_ACCOUNT_LENGTH = 11;

    private static final Random random = new SecureRandom();

    private static int getWeightNumber(int i) {
        return 7 - (i + 2) % 6;
    }

    public static char getCheckDigit(String kontonummer) {
        int lastIndex = kontonummer.length() - 1;
        int sum = 0;

        for (int i = lastIndex; i >= 0; i--) {
            sum += Character.getNumericValue(kontonummer.charAt(i)) * getWeightNumber(i);
        }

        int remainder = sum % 11;

        if (remainder == 0) {
            return '0';
        }
        if (remainder == 1) {
            return '-';
        }
        return Character.forDigit(11 - remainder, 10);
    }

    public static String tilfeldigNorskBankkonto() {
        var kontonummerLengde = NORSK_ACCOUNT_LENGTH - 1;

        var kontonummer = random.ints(kontonummerLengde, 0, 9)
                .boxed()
                .map(Integer::toUnsignedString)
                .collect(Collectors.joining());

        var checkDigit = BankkontoGenerator.getCheckDigit(kontonummer);

        if (checkDigit == '-') {
            kontonummer = random.ints(kontonummerLengde, 0, 9)
                    .boxed()
                    .map(Integer::toUnsignedString)
                    .collect(Collectors.joining());
        }

        return kontonummer + BankkontoGenerator.getCheckDigit(kontonummer);
    }

    public static String tilfeldigUtlandskBankkonto(String landkode) {
        if (nonNull(landkode) && landkode.length() == 3) {
            landkode = KontoregisterLandkode.getIso2FromIso(landkode);
        }
        if (nonNull(landkode) && landkode.length() > 2) {
            landkode = landkode.substring(0, 2);
        }

        var kontonummerLengde = DEFAULT_ACCOUNT_LENGTH;

        try {
            var kontoregisterLandkode = KontoregisterLandkode.valueOf(landkode);
            if (nonNull(kontoregisterLandkode.getIbanLengde()) && kontoregisterLandkode.getIbanLengde() > 2) {
                kontonummerLengde = kontoregisterLandkode.getIbanLengde() - IBAN_COUNTRY_LENGTH;
            }
        } catch (Exception e) {
            log.warn("bruker ukjent 'landkode' {} for å generere kontonummer", landkode);
        }

        var kontonummer = random.ints(kontonummerLengde, 0, 10)
                .boxed()
                .map(Integer::toUnsignedString)
                .collect(Collectors.joining());

        return landkode + kontonummer;
    }
}