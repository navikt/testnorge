package no.nav.identpool.ident.ajourhold.tps.generator;

import static java.lang.Math.toIntExact;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class FnrGenerator {

    private static final int[] CONTROL_DIGIT_C1 = { 3, 7, 6, 1, 8, 9, 4, 5, 2 };
    private static final int[] CONTROL_DIGIT_C2 = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 };

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

    public static List<String> genererIdenter(LocalDate date) {
        return genererIdenter(date, date.plusDays(1));
    }

    public static List<String> genererIdenter(final LocalDate fom, final LocalDate to) {
        if (fom.isAfter(to) || fom.isEqual(to)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være eller dato til %s", fom.toString(), to.toString()));
        }
        int days = toIntExact(ChronoUnit.DAYS.between(fom, to));
        ArrayList<String> list = new ArrayList<>(toIntExact(ChronoUnit.DAYS.between(fom, to) * 400));
        IntStream.range(0, days)
                .mapToObj(fom::plusDays)
                .map(FnrGenerator::generateNumbers)
                .forEach(list::addAll);
        return list;
    }

    public static Map<LocalDate, List<String>> genererIdenterMap(final LocalDate fom, final LocalDate to) {
        if (fom.isAfter(to)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være før eller lik dato til og med %s", fom.toString(), to.toString()));
        }
        int days = toIntExact(ChronoUnit.DAYS.between(fom, to));
        return IntStream.range(0, days)
                .mapToObj(fom::plusDays)
                .collect(Collectors.toMap(i -> i, FnrGenerator::generateNumbers));
    }

    private static List<String> generateNumbers(LocalDate birthdate) {
        String date = formatter.format(birthdate) + "%03d";
        return getCategoryRange(birthdate)
                .mapToObj(number -> generateFnr(String.format(date, number)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static IntStream getCategoryRange(LocalDate birthDate) {
        int year = birthDate.getYear();
        if (year < 2000 && year > 1) {
            return IntStream.range(1, 500)
                    .map(i -> 1 + (500 - 1 - i));
        } else if (year >= 2000 && year < 2040) {
            return IntStream.range(500, 1000)
                    .map(i -> 500 + (1000 - 1 - i));
        } else {
            throw new IllegalStateException(String.format("Fødelsår må være mellom 1 og 2039, fikk %d", year));
        }
    }

    private static String generateFnr(String number) {
        int digit1 = getControlDigit(number, CONTROL_DIGIT_C1);
        if (digit1 == 10) {
            return null;
        }
        number += Integer.toString(digit1);
        int digit2 = getControlDigit(number, CONTROL_DIGIT_C2);
        if (digit2 == 10) {
            return null;
        }
        return number + Integer.toString(digit2);
    }

    private static int getControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            digitsum += (fnr.charAt(i) - 48) * sequence[i];
        }
        digitsum = 11 - (digitsum % 11);
        return digitsum == 11 ? 0 : digitsum;
    }
}
