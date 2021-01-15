package no.nav.identpool.service;

import static java.lang.Math.toIntExact;
import static no.nav.identpool.util.IdentGeneratorUtil.generatorMap;
import static no.nav.identpool.util.IdentGeneratorUtil.getGenderNumber;
import static no.nav.identpool.util.IdentGeneratorUtil.getStartIndex;
import static no.nav.identpool.util.IdentGeneratorUtil.getYearRange;
import static no.nav.identpool.util.IdentGeneratorUtil.numberFormatter;
import static no.nav.identpool.util.PersonidentUtil.generateFnr;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.util.IdentGeneratorUtil;

@Service
public class IdentGeneratorService {

    private static final int SYNTETISK = 4;
    private static SecureRandom random = new SecureRandom();

    public Map<LocalDate, List<String>> genererIdenterMap(
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Identtype type
    ) {
        validateDates(foedtEtter, foedtFoer);
        int days = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));
        Function<LocalDate, List<String>> numberGenerator = generatorMap.get(type);
        return IntStream.range(0, days)
                .mapToObj(foedtEtter::plusDays)
                .collect(Collectors.toMap(
                        i -> i,
                        numberGenerator));
    }

    public Set<String> genererIdenterFdat(
            HentIdenterRequest request,
            Set<String> identerIIdentPool
    ) {
        Assert.notNull(request.getFoedtEtter(), "FOM dato ikke oppgitt");

        validateDates(request.getFoedtEtter(), request.getFoedtFoer());
        if (request.getFoedtFoer().isEqual(request.getFoedtEtter())) {
            request.setFoedtFoer(request.getFoedtEtter().plusDays(1));
        }

        Set<String> identer = identerIIdentPool;
        int antall = request.getAntall() + identer.size();
        int numberOfDates = toIntExact(ChronoUnit.DAYS.between(request.getFoedtEtter(), request.getFoedtFoer()));

        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(Identtype.FDAT, IdentGeneratorUtil::randomFormat);

        while (identer.size() < antall) {
            LocalDate birthdate = request.getFoedtEtter().plusDays(random.nextInt(numberOfDates));
            String format = numberFormat.apply(birthdate);
            if (isTrue(request.getSyntetisk())) {
                format = addSyntetiskToken(format);
            }

            int originalSize = identer.size();

            for (int i = 1; i <= 9; i++) {
                identer.add(String.format(format, i));
            }

            if (identer.size() == originalSize) {
                throw new IllegalArgumentException("Kan ikke finne ønsket antall fødselsnummer med angitte kriterier");
            }
        }
        return identer;
    }

    public Set<String> genererIdenter(
            HentIdenterRequest request,
            Set<String> identerIIdentPool
    ) {
        Assert.notNull(request.getFoedtEtter(), "FOM dato ikke oppgitt");

        validateDates(request.getFoedtEtter(), request.getFoedtFoer());
        if (request.getFoedtFoer().isEqual(request.getFoedtEtter())) {
            request.setFoedtFoer(request.getFoedtEtter().plusDays(1));
        }

        Kjoenn kjoenn = request.getKjoenn();
        Identtype identtype = request.getIdenttype();

        Set<String> identer = identerIIdentPool;
        int antall = request.getAntall() + identer.size();
        int iteratorRange = (kjoenn == null) ? 1 : 2;
        int numberOfDates = toIntExact(ChronoUnit.DAYS.between(request.getFoedtEtter(), request.getFoedtFoer()));

        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(identtype, IdentGeneratorUtil::randomFormat);

        while (identer.size() < antall) {
            LocalDate birthdate = request.getFoedtEtter().plusDays(random.nextInt(numberOfDates));
            String format = numberFormat.apply(birthdate);
            if (isTrue(request.getSyntetisk())) {
                format = addSyntetiskToken(format);
            }

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
        return identer;
    }

    private void validateDates(
            LocalDate foedtEtter,
            LocalDate foedtFoer
    ) {
        if (foedtEtter.isAfter(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til dato (%s) kan ikke være etter før dato (%s)", foedtEtter, foedtFoer));
        }
    }

    private static String addSyntetiskToken(String format){
        return String.format("%s%1d%s", format.substring(0, 2), Integer.parseInt(format.substring(2,3)) + SYNTETISK, format.substring(3));
    }
}