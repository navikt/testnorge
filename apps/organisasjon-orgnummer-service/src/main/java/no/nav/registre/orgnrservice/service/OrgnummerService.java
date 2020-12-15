package no.nav.registre.orgnrservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import no.nav.registre.orgnrservice.consumer.OrganisasjonApiConsumer;

@Service
@AllArgsConstructor
public class OrgnummerService {

    private final OrganisasjonApiConsumer organisasjonApiConsumer;
//    private final OrgnummerAdapter orgnummerAdapter;

//    public List<String> genererOrgnrsTilDb (Integer antall) {
//        return orgnummerAdapter.saveAll(generateOrgnrs(antall));
//    }

    public List<String> generateOrgnrs(Integer antall) {
        List<String> orgnrListe = new ArrayList<>();
        for (int i = 0; i < antall; i++) {
            orgnrListe.add(generateOrgnr());
        }
        return orgnrListe;
    }

    private String generateOrgnr() {
        String weights = "32765432";
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        String randomString = String.valueOf(random);

        int controlDigit = calculateControlDigit(weights, randomString);

        if (controlDigit < 0 || controlDigit > 9) {
            return generateOrgnr();
        }

        String orgnr = randomString + controlDigit;
        if (finnesOrgnr(orgnr)) {
            return generateOrgnr();
        }
        return orgnr;
    }

    private static int calculateControlDigit(String weights, String randomString) {
        int weightedSum = 0;
        for (int index = 0; index < randomString.length(); index++) {
            int vekt = Character.getNumericValue(weights.charAt(index));
            int numericValue = Character.getNumericValue(randomString.charAt(index));

            weightedSum += (vekt * numericValue);
        }

        int rest = weightedSum % 11;
        if (rest == 0) {
            return 0;
        } else if (rest == 1) {
            return -1;
        }
        return 11 - rest;
    }

    private boolean finnesOrgnr(String orgnummer) {
        return organisasjonApiConsumer.getOrgnr(orgnummer) != null;
//        return organisasjonApiConsumer.finnesOrgnrIEreg(orgnummer);
    }
}
