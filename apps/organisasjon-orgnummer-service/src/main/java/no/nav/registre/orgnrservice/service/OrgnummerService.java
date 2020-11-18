package no.nav.registre.orgnrservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrgnummerService {

    public String generate() {
        String weights = "32765432";
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        String randomString = String.valueOf(random);

        int controlDigit = calculateControlDigit(weights, randomString);
        if (controlDigit == 1 || controlDigit < 0) {
            return generate();
        }

        return randomString + controlDigit;
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

}
