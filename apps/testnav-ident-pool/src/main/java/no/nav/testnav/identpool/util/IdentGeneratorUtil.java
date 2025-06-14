package no.nav.testnav.identpool.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Character.getNumericValue;
import static no.nav.testnav.identpool.util.PersonidentUtil.generateFnr;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@UtilityClass
public final class IdentGeneratorUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
    public static final Map<Identtype, BiFunction<LocalDate, Boolean, List<String>>> generatorMap =
            Map.of(
                    Identtype.FNR, IdentGeneratorUtil::generateFNumbers,
                    Identtype.DNR, IdentGeneratorUtil::generateDNumbers,
                    Identtype.BOST, IdentGeneratorUtil::generateBNumbers);
    public static final Map<Identtype, Function<LocalDate, String>> numberFormatter =
            Map.of(
                    Identtype.FNR, IdentGeneratorUtil::getFnrFormat,
                    Identtype.DNR, IdentGeneratorUtil::getDnrFormat,
                    Identtype.BOST, IdentGeneratorUtil::getBnrFormat);
    private static final SecureRandom random = new SecureRandom();

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

    private static List<String> generateFNumbers(LocalDate birthdate, boolean syntetisk) {
        return generateNumbers(birthdate, amendSyntetisk(getFnrFormat(birthdate), syntetisk));
    }

    private static List<String> generateDNumbers(LocalDate birthdate, boolean syntetisk) {
        return generateNumbers(birthdate, amendSyntetisk(getDnrFormat(birthdate), syntetisk));
    }

    private static List<String> generateBNumbers(LocalDate birthdate, boolean syntetisk) {
        return generateNumbers(birthdate, amendSyntetisk(getBnrFormat(birthdate), syntetisk));
    }

    private static List<String> generateNumbers(LocalDate date, String numberFormat) {
        return new ArrayList<>(getCategoryNumberStreamReverse(date)
                .mapToObj(number -> generateFnr(String.format(numberFormat, number)))
                .filter(Objects::nonNull)
                .toList());
    }

    private static String amendSyntetisk(String format, Boolean syntetiskIdent) {
        return isTrue(syntetiskIdent) ?
                format.substring(0, 2) + getNumericValue(format.charAt(2) + 4) + format.substring(3) :
                format;
    }

    private static String getFnrFormat(LocalDate birthdate) {
        return formatter.format(birthdate) + "%03d";
    }

    private static String getDnrFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return (getNumericValue(format.charAt(0)) + 4) + format.substring(1);
    }

    private static String getBnrFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return format.substring(0, 2) + (getNumericValue(format.charAt(2)) + 2) + format.substring(3);
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
