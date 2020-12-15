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

    public List<String> generateOrgnrs (Integer antall) {
        List<String> identListe = new ArrayList<>();
        for (int i = 0; i < antall; i++) {
            identListe.add(generateOrgnr());
        }
        return identListe;
    }

    private String generateOrgnr() {
        String weights = "32765432";
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        String randomString = String.valueOf(random);

        int controlDigit = calculateControlDigit(weights, randomString);
        String orgnr = randomString + controlDigit;
        boolean b = finnesOrgnr(orgnr);
        if ((controlDigit == 1 || controlDigit < 0) && b) {
//        if ((controlDigit == 1 || controlDigit < 0)) {
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
        return 11 - (weightedSum % 11);
    }

    private boolean finnesOrgnr (String orgnummer) {
        return organisasjonApiConsumer.finnesOrgnrIEreg(orgnummer);
    }
}
