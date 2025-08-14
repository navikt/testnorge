package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.util.IdentGeneratorUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.toIntExact;
import static no.nav.testnav.identpool.util.IdentGeneratorUtil.generatorMap;
import static no.nav.testnav.identpool.util.IdentGeneratorUtil.getGenderNumber;
import static no.nav.testnav.identpool.util.IdentGeneratorUtil.getStartIndex;
import static no.nav.testnav.identpool.util.IdentGeneratorUtil.getYearRange;
import static no.nav.testnav.identpool.util.IdentGeneratorUtil.numberFormatter;
import static no.nav.testnav.identpool.util.PersonidentUtil.generateFnr;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class IdentGeneratorService {

    private static final int SYNTETISK = 4;
    private static final Random random = new SecureRandom();

    private static String addSyntetiskIdentifier(String format) {
        return String.format("%s%1d%s", format.substring(0, 2), Integer.parseInt(format.substring(2, 3)) + SYNTETISK, format.substring(3));
    }

    public Map<LocalDate, List<String>> genererIdenterMap(
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Identtype type,
            boolean syntetiskIdent) {

        validateDates(foedtEtter, foedtFoer);
        int days = toIntExact(ChronoUnit.DAYS.between(foedtEtter, foedtFoer));
        BiFunction<LocalDate, Boolean, List<String>> numberGenerator = generatorMap.get(type);
        return IntStream.range(0, days)
                .mapToObj(foedtEtter::plusDays)
                .collect(Collectors.toMap(
                        date -> date,
                        date -> {
                            List<String> identer = numberGenerator.apply(date, syntetiskIdent);
                            Collections.shuffle(identer);
                            return identer;
                        }
                ));
    }

    @SuppressWarnings("java:S3776")
    public Set<String> genererIdenter(HentIdenterRequest request) {

        var identerIIdentPool = new HashSet<String>();

        Assert.notNull(request.getFoedtEtter(), "FOM dato ikke oppgitt");

        validateDates(request.getFoedtEtter(), request.getFoedtFoer());
        if (request.getFoedtFoer().isEqual(request.getFoedtEtter())) {
            request.setFoedtFoer(request.getFoedtEtter().plusDays(1));
        }

        var antall = request.getAntall();
        var iteratorRange = (request.getKjoenn() == null) ? 1 : 2;
        var numberOfDates = toIntExact(ChronoUnit.DAYS.between(request.getFoedtEtter(), request.getFoedtFoer()));

        Function<LocalDate, String> numberFormat =
                numberFormatter.getOrDefault(request.getIdenttype(), IdentGeneratorUtil::randomFormat);

        while (identerIIdentPool.size() < antall) {
            var birthdate = request.getFoedtEtter().plusDays(random.nextInt(numberOfDates));
            var format = numberFormat.apply(birthdate);
            if (isTrue(request.getSyntetisk())) {
                format = addSyntetiskIdentifier(format);
            }

            var yearRange = getYearRange(birthdate);
            var originalSize = identerIIdentPool.size();
            var genderNumber = getGenderNumber(yearRange, request.getKjoenn());
            var startIndex = getStartIndex(yearRange.get(0), request.getKjoenn());

            for (int i = startIndex; identerIIdentPool.size() == originalSize && i < genderNumber; i += iteratorRange) {
                String fnr = generateFnr(String.format(format, i));
                if (fnr != null) {
                    identerIIdentPool.add(fnr);
                }
            }

            for (int i = genderNumber; identerIIdentPool.size() == originalSize && i < yearRange.get(1); i += iteratorRange) {
                var fnr = generateFnr(String.format(format, i));
                if (fnr != null) {
                    identerIIdentPool.add(fnr);
                }
            }

            if (identerIIdentPool.size() == originalSize) {
                break;
            }
        }
        if (identerIIdentPool.isEmpty()) {
            throw new IllegalArgumentException("Finner ingen fødselsnummer med angitte kriterier");
        }
        return identerIIdentPool;
    }

    private static void validateDates(LocalDate foedtEtter, LocalDate foedtFoer) {

        if (foedtEtter.isAfter(foedtFoer)) {
            throw new IllegalArgumentException(String.format("Til dato (%s) kan ikke være etter før dato (%s)", foedtEtter, foedtFoer));
        }
    }
}