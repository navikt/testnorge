package no.nav.identpool.ident.ajourhold.tps.generator;

import static java.lang.Math.toIntExact;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableMap;

import org.springframework.stereotype.Service;

import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Kjoenn;
import no.nav.identpool.ident.rest.v1.HentIdenterRequest;

@Service
public final class IdentGenerator {

    private static final int[] CONTROL_DIGIT_C1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] CONTROL_DIGIT_C2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

    private static Map<Identtype, Function<LocalDate, List<String>>> generatorMap =
            ImmutableMap.of(
                    Identtype.FNR, IdentGenerator::generateIdentNumbers,
                    Identtype.DNR, IdentGenerator::generateDNumbers);

    private static Map<Identtype, Function<LocalDate, String>> numberFormatter =
            ImmutableMap.of(
                    Identtype.FNR, IdentGenerator::getFnrFormat,
                    Identtype.DNR, IdentGenerator::getDnrFormat);

    private static Random random;

    private IdentGenerator() {
    }

    public static Map<LocalDate, List<String>> genererIdenterMap(LocalDate fodtEtter, LocalDate fodtFoer, Identtype type) {
        if (fodtEtter.isAfter(fodtFoer) || fodtEtter.isEqual(fodtFoer)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være eller dato til %s", fodtEtter, fodtFoer));
        }
        int days = toIntExact(ChronoUnit.DAYS.between(fodtEtter, fodtFoer));
        Function<LocalDate, List<String>> numberGenerator = generatorMap.get(type);
        return IntStream.range(0, days)
                .mapToObj(fodtEtter::plusDays)
                .collect(Collectors.toMap(
                        i -> i,
                        numberGenerator));
    }

    private static String getFnrFormat(LocalDate birthdate) {
        return formatter.format(birthdate) + "%03d";
    }

    private static String getDnrFormat(LocalDate birthdate) {
        String format = formatter.format(birthdate) + "%03d";
        return (Character.getNumericValue(format.charAt(0)) + 4) + format.substring(1);
    }

    private static String randomFormat(LocalDate birthdate) {
        String format = formatter.format(birthdate) + "%03d";
        if (random.nextBoolean()) {
            return (Character.getNumericValue(format.charAt(0)) + 4) + format.substring(1);
        }
        return format;
    }

    private static List<String> generateIdentNumbers(LocalDate birthdate) {
        return generateNumbers(birthdate, getFnrFormat(birthdate));
    }

    private static List<String> generateDNumbers(LocalDate birthdate) {
        return generateNumbers(birthdate, getDnrFormat(birthdate));
    }

    private static List<String> generateNumbers(LocalDate date, String numberFormat) {
        return getCategoryNumberStraemReverse(date)
                .mapToObj(number -> generateFnr(String.format(numberFormat, number)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static IntStream getCategoryNumberStraemReverse(LocalDate birthDate) {
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

    public static List<String> genererIdenter(HentIdenterRequest kriterier) {
        Set<String> identer = new HashSet<>(kriterier.getAntall());
        if (kriterier.getFoedtEtter() == null) {
            throw new IllegalArgumentException("Dato fra og med ikke oppgitt");
        }
        LocalDate fodtEtter = kriterier.getFoedtEtter();
        LocalDate fodtFoer = kriterier.getFoedtFoer() == null ? fodtEtter.plusDays(1) : kriterier.getFoedtFoer();
        if (fodtEtter.plusDays(1).isAfter(fodtFoer)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være eller dato til %s", fodtEtter, fodtFoer));
        }
        random = new Random();
        Kjoenn kjoenn = kriterier.getKjoenn();
        int iteratorRange = getIteratorRange(kjoenn);
        int numberOfDates = toIntExact(ChronoUnit.DAYS.between(kriterier.getFoedtEtter(), fodtFoer));
        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(kriterier.getIdenttype(), IdentGenerator::randomFormat);
        while (identer.size() < kriterier.getAntall()) {
            LocalDate birthdate = kriterier.getFoedtEtter().plusDays(random.nextInt(numberOfDates));
            String format = numberFormat.apply(birthdate);
            List<Integer> range = getCategoryRange(birthdate);
            int categoryNumber = getCategoryNumber(range, kjoenn);
            int size = identer.size();
            for (int i = categoryNumber; identer.size() == size && i < range.get(1); i += iteratorRange) {
                addFnr(generateFnr(String.format(format, i)), identer);
            }
            int startIndex = getStartIndex(range.get(0), kjoenn);
            for (int i = startIndex; identer.size() == size && i < categoryNumber; i += iteratorRange) {
                addFnr(generateFnr(String.format(format, i)), identer);
            }
            if (identer.size() == size) {
                throw new IllegalArgumentException("Kan ikke finne antall fødselsnummere med angitte kriterier");
            }
        }
        return new ArrayList<>(identer);
    }

    private static List<Integer> getCategoryRange(LocalDate birthDate) {
        int year = birthDate.getYear();
        if (year < 2000 && year > 1) {
            return Arrays.asList(1, 499);
        } else if (year >= 2000 && year < 2040) {
            return Arrays.asList(500, 999);
        } else {
            throw new IllegalStateException(String.format("Fødelsår må være mellom 1 og 2039, fikk %d", year));
        }
    }

    private static int getCategoryNumber(List<Integer> range, Kjoenn kjoenn) {
        Random random = new Random();
        int number = random.nextInt(range.get(1) - 1) + range.get(0);
        if ((Kjoenn.KVINNE.equals(kjoenn) && number % 2 != 0) || (Kjoenn.MANN.equals(kjoenn) && number % 2 == 0)) {
            number += 1;
        }
        return number;
    }

    private static int getStartIndex(int index, Kjoenn kjoenn) {
        if ((index % 2 != 0 && Kjoenn.KVINNE.equals(kjoenn)) || (index % 2 == 0 && Kjoenn.MANN.equals(kjoenn))) {
            return index + 1;
        }
        return index;
    }

    private static int getIteratorRange(Kjoenn kjoenn) {
        if (kjoenn == null) {
            return 1;
        } else {
            return 2;
        }
    }

    private static void addFnr(String fnr, Set<String> pinSet) {
        if (fnr != null) {
            pinSet.add(fnr);
        }
    }

    private static String generateFnr(String birthdate) {

        int digit1 = getControlDigit(birthdate, CONTROL_DIGIT_C1);
        if (digit1 == 10) {
            return null;
        }

        int digit2 = getControlDigit(birthdate + digit1, CONTROL_DIGIT_C2);
        if (digit2 == 10) {
            return null;
        }
        return birthdate + digit1 + digit2;
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