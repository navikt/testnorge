package no.nav.identpool.util;

import static java.lang.Character.getNumericValue;
import static no.nav.identpool.util.PersonidentUtil.generateFnr;
import static no.nav.identpool.util.PersonidentUtil.getKjonn;
import static no.nav.identpool.util.PersonidentUtil.toBirthdate;

import com.google.common.collect.ImmutableMap;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IdentGeneratorUtil {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
    private static SecureRandom random = new SecureRandom();

    public static final Map<Identtype, Function<LocalDate, List<String>>> generatorMap =
            ImmutableMap.of(
                    Identtype.FNR, IdentGeneratorUtil::generateFNumbers,
                    Identtype.DNR, IdentGeneratorUtil::generateDNumbers);

    public static final Map<Identtype, Function<LocalDate, String>> numberFormatter =
            ImmutableMap.of(
                    Identtype.FNR, IdentGeneratorUtil::getFnrFormat,
                    Identtype.DNR, IdentGeneratorUtil::getDnrFormat);

    private IdentGeneratorUtil() {}

    public static String randomFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return random.nextBoolean() ? (getNumericValue(format.charAt(0)) + 4) + format.substring(1) : format;
    }

    public static List<Integer> getYearRange(LocalDate birthDate) {
        int year = birthDate.getYear();
        if (year < 2000 && year > 1) {
            return Arrays.asList(1, 499);
        } else if (year >= 2000 && year < 2040) {
            return Arrays.asList(500, 999);
        } else {
            throw new IllegalStateException(String.format("Fødelsår må være mellom 1 og 2039, fikk %d", year));
        }
    }

    public static int getGenderNumber(List<Integer> range, Kjoenn kjoenn) {
        int number = random.nextInt(range.get(1) - 1) + range.get(0);
        if ((Kjoenn.KVINNE.equals(kjoenn) && number % 2 != 0) || (Kjoenn.MANN.equals(kjoenn) && number % 2 == 0)) {
            number += 1;
        }
        return number;
    }

    public static int getStartIndex(int index, Kjoenn kjoenn) {
        if ((index % 2 != 0 && Kjoenn.KVINNE.equals(kjoenn)) || (index % 2 == 0 && Kjoenn.MANN.equals(kjoenn))) {
            return index + 1;
        }
        return index;
    }

    public static Ident createIdent(String fnr, Rekvireringsstatus status, String rekvirertAv) {
        Identtype identtype = PersonidentUtil.getIdentType(fnr);
        return Ident.builder()
                .finnesHosSkatt(false)
                .personidentifikator(fnr)
                .foedselsdato(toBirthdate(fnr))
                .kjoenn(getKjonn(fnr))
                .rekvireringsstatus(status)
                .rekvirertAv(rekvirertAv)
                .identtype(identtype)
                .build();
    }

    private static List<String> generateFNumbers(LocalDate birthdate) {
        return generateNumbers(birthdate, getFnrFormat(birthdate));
    }

    private static List<String> generateDNumbers(LocalDate birthdate) {
        return generateNumbers(birthdate, getDnrFormat(birthdate));
    }

    private static List<String> generateNumbers(LocalDate date, String numberFormat) {
        return getCategoryNumberStreamReverse(date)
                .mapToObj(number -> generateFnr(String.format(numberFormat, number)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static String getFnrFormat(LocalDate birthdate) {
        return formatter.format(birthdate) + "%03d";
    }

    private static String getDnrFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return (getNumericValue(format.charAt(0)) + 4) + format.substring(1);
    }

    private static IntStream getCategoryNumberStreamReverse(LocalDate birthDate) {
        int year = birthDate.getYear();
        if (year < 2000 && year > 1) {
            return IntStream.range(1, 500)
                    .map(i -> 1 + (500 - 1 - i));
        } else if (year >= 2000 && year < 2040) {
            return IntStream.range(500, 1000)
                    .map(i -> 500 + (1000 - 1 - i));
        } else {
            throw new IllegalStateException(String.format("Fødelsår må være mellom 2 og 2039, fikk %d", year));
        }
    }

}
