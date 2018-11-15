package no.nav.identpool.ajourhold.tps.generator;

import static java.lang.Character.getNumericValue;
import static java.lang.Math.toIntExact;
import static no.nav.identpool.util.PersonidentifikatorUtil.generateFnr;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableMap;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.HentIdenterRequest;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

public final class IdentGenerator {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
    private static SecureRandom random = new SecureRandom();

    private static Map<Identtype, Function<LocalDate, List<String>>> generatorMap =
            ImmutableMap.of(
                    Identtype.FNR, IdentGenerator::generateFNumbers,
                    Identtype.DNR, IdentGenerator::generateDNumbers);

    private static Map<Identtype, Function<LocalDate, String>> numberFormatter =
            ImmutableMap.of(
                    Identtype.FNR, IdentGenerator::getFnrFormat,
                    Identtype.DNR, IdentGenerator::getDnrFormat);

    public static Map<LocalDate, List<String>> genererIdenterMap(LocalDate foedtEtter, LocalDate foedtFoer, Identtype type) {
        validateDates(foedtEtter, foedtFoer);
        int days = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));
        Function<LocalDate, List<String>> numberGenerator = generatorMap.get(type);
        return IntStream.range(0, days)
                .mapToObj(foedtEtter::plusDays)
                .collect(Collectors.toMap(
                        i -> i,
                        numberGenerator));
    }

    //FIXME Del opp og gjør mer lesbar
    public static List<String> genererIdenter(HentIdenterRequest request) {
        Assert.notNull(request.getFoedtEtter(), "Fra og med dato ikke oppgitt");

        Set<String> identer = new HashSet<>(request.getAntall());
        LocalDate foedtEtter = request.getFoedtEtter();
        LocalDate foedtFoer = request.getFoedtFoer() == null ? foedtEtter.plusDays(1) : request.getFoedtFoer();
        Kjoenn kjoenn = request.getKjoenn();
        Identtype identtype = request.getIdenttype();
        @NotNull int antall = request.getAntall();

        //TODO Denne er ikke helt logisk, spesielt med tanke på feilmeldingen
        if (foedtEtter.plusDays(1).isAfter(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Dato fra og med %s, må være eller dato til %s", foedtEtter, foedtFoer));
        }

        int iteratorRange = getIteratorRange(kjoenn);
        int numberOfDates = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));
        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(identtype, IdentGenerator::randomFormat);
        while (identer.size() < antall) {
            LocalDate birthdate = foedtEtter.plusDays(random.nextInt(numberOfDates));
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
                throw new IllegalArgumentException("Kan ikke finne ønsket antall fødselsnummer med angitte kriterier");
            }
        }
        return new ArrayList<>(identer);
    }

    private static void validateDates(LocalDate foedtEtter, LocalDate foedtFoer) {
        if (foedtEtter.isAfter(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til dato (%s) kan ikke være etter før dato (%s)", foedtEtter, foedtFoer));
        }
        if (foedtEtter.isEqual(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til (%s) og fra (%s) dato kan ikke være like", foedtFoer, foedtEtter));
        }
    }

    private static String getFnrFormat(LocalDate birthdate) {
        return formatter.format(birthdate) + "%03d";
    }

    private static String getDnrFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return (getNumericValue(format.charAt(0)) + 4) + format.substring(1);
    }

    private static String randomFormat(LocalDate birthdate) {
        String format = getFnrFormat(birthdate);
        return random.nextBoolean() ? (getNumericValue(format.charAt(0)) + 4) + format.substring(1) : format;
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

    //TODO Category brukes om flere ting, gi mer forklarende navn
    //TODO Magic numbers
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

    //TODO Er denne metoden egentlig nødvendig?
    private static int getIteratorRange(Kjoenn kjoenn) {
        return kjoenn == null ? 1 : 2;
    }

    //TODO Vurder om denne metoden kan fjernes
    private static void addFnr(String fnr, Set<String> pinSet) {
        if (fnr != null) {
            pinSet.add(fnr);
        }
    }

    //TODO Remove Bruker PersonidentifikatorUtil sin calculateControlDigit
    @Deprecated
    private static int getControlDigit(String fnr, int... sequence) {
        int digitsum = 0;
        for (int i = 0; i < sequence.length; ++i) {
            //FIXME Hvorfor minus 48? Ikke hentet ut numeric, derfor.. Dette er ikke pent!
            digitsum += (fnr.charAt(i) - 48) * sequence[i];
        }
        digitsum = 11 - (digitsum % 11);
        return digitsum == 11 ? 0 : digitsum;
    }
}