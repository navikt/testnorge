package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;

@Service
public class Identpool32Service {

    private static final int[] WEIGHTS_K1 = {3, 7, 6, 1, 8, 9, 4, 5, 2};
    private static final int[] WEIGHTS_K2 = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static final LocalDate START_DATO = LocalDate.of(2000, 1, 1);
    private static final LocalDate SLUTT_DATO = LocalDate.of(2099, 12, 31);
    private static final Random RANDOM = new SecureRandom();

    public List<String> generateIdent(RekvirerIdentRequest request) {

        var foedselsdato = generateRandomLocalDate(request.getFoedtEtter(), request.getFoedtFoer());

        var ident = String.format("%02d", foedselsdato.getDayOfMonth()) +
                String.format("%02d", foedselsdato.getMonthValue()) +
                String.format("%02d", foedselsdato.getYear() % 100) +
                String.format("%03d", RANDOM.nextInt(0, 1000));

        return Arrays.stream(calculateK1(ident))
                .filter(k1 -> k1 != 10)
                .mapToObj(k1 -> ident + String.format("%1d", k1))
                .map(identMedK1 -> identMedK1 + String.format("%1d", calculateK2(identMedK1)))
                .toList();
    }

    private static LocalDate generateRandomLocalDate(LocalDate startDate, LocalDate endDate) {

        long startEpochDay = (nonNull(startDate) ? startDate : START_DATO).toEpochDay();
        long endEpochDay = (nonNull(endDate) ? endDate : SLUTT_DATO).toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1); // +1 to include endDate

        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private static int[] calculateK1(String ident) {

        var weightedK1 = IntStream.range(0, WEIGHTS_K1.length)
                .map(i -> parseInt(valueOf(ident.charAt(i))) * WEIGHTS_K1[i])
                .sum();

        int remainder = weightedK1 % 11;
        return new int[]{
                alternateK1(remainder, 14),
                alternateK1(remainder, 13),
                alternateK1(remainder, 12),
                alternateK1(remainder, 11)};
    }

    private static int alternateK1(int reminder, int controlDigit) {

        return (controlDigit - reminder) % 11;
    }

    private static int calculateK2(String ident) {

        var weightedK2 = IntStream.range(0, WEIGHTS_K2.length)
                .map(i -> parseInt(valueOf(ident.charAt(i))) * WEIGHTS_K2[i])
                .sum();

        int remainder = 11 - weightedK2 % 11;

        if (remainder == 10) {
            throw new IllegalArgumentException("Invalid ident: cannot calculate K2");
        } else {
            return remainder % 11;
        }
    }
}
