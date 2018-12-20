package no.nav.identpool.service;

import static java.lang.Math.toIntExact;
import static no.nav.identpool.util.IdentGeneratorUtil.generatorMap;
import static no.nav.identpool.util.IdentGeneratorUtil.getGenderNumber;
import static no.nav.identpool.util.IdentGeneratorUtil.getYearRange;
import static no.nav.identpool.util.IdentGeneratorUtil.getStartIndex;
import static no.nav.identpool.util.IdentGeneratorUtil.numberFormatter;
import static no.nav.identpool.util.PersonidentifikatorUtil.generateFnr;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import no.nav.identpool.util.IdentGeneratorUtil;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class IdentGeneratorService {
    private static SecureRandom random = new SecureRandom();

    public Map<LocalDate, List<String>> genererIdenterMap(LocalDate foedtEtter, LocalDate foedtFoer, Identtype type) {
        validateDates(foedtEtter, foedtFoer);
        int days = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));
        Function<LocalDate, List<String>> numberGenerator = generatorMap.get(type);
        return IntStream.range(0, days)
                .mapToObj(foedtEtter::plusDays)
                .collect(Collectors.toMap(
                        i -> i,
                        numberGenerator));
    }

    public List<String> genererIdenter(HentIdenterRequest request) {
        Assert.notNull(request.getFoedtEtter(), "FOM dato ikke oppgitt");

        LocalDate foedtEtter = request.getFoedtEtter();
        LocalDate foedtFoer = request.getFoedtFoer() == null ? foedtEtter.plusDays(1) : request.getFoedtFoer();
        validateDates(foedtEtter, foedtFoer);

        int antall = request.getAntall();
        Kjoenn kjoenn = request.getKjoenn();
        Identtype identtype = request.getIdenttype();

        Set<String> identer = new HashSet<>();
        int iteratorRange = (kjoenn == null) ? 1 : 2;
        int numberOfDates = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));

        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(identtype, IdentGeneratorUtil::randomFormat);

        while (identer.size() < antall) {
            LocalDate birthdate = foedtEtter.plusDays(random.nextInt(numberOfDates));
            String format = numberFormat.apply(birthdate);

            List<Integer> yearRange = getYearRange(birthdate);
            int originalSize = identer.size();
            int genderNumber = getGenderNumber(yearRange, kjoenn);
            int startIndex = getStartIndex(yearRange.get(0), kjoenn);

            for (int i = startIndex; identer.size() == originalSize && i < genderNumber; i += iteratorRange) {
                String fnr = generateFnr(String.format(format, i));
                if (fnr != null) {
                    identer.add(fnr);
                }
            }

            for (int i = genderNumber; identer.size() == originalSize && i < yearRange.get(1); i += iteratorRange) {
                String fnr = generateFnr(String.format(format, i));
                if (fnr != null) {
                    identer.add(fnr);
                }
            }

            if (identer.size() == originalSize) {
                throw new IllegalArgumentException("Kan ikke finne ønsket antall fødselsnummer med angitte kriterier");
            }
        }
        return new ArrayList<>(identer);
    }

    private void validateDates(LocalDate foedtEtter, LocalDate foedtFoer) {
        if (foedtEtter.isEqual(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til (%s) og fra (%s) dato kan ikke være like", foedtFoer, foedtEtter));
        }
        if (foedtEtter.isAfter(foedtFoer) || foedtEtter.plusDays(1).isAfter(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til dato (%s) kan ikke være etter før dato (%s)", foedtEtter, foedtFoer));
        }
    }
}