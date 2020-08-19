package no.nav.registre.ereg.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class OrgnummerUtil {

    private OrgnummerUtil() {
    }

    public static String generate(RestTemplate restTemplate) {
        String weights = "32765432";
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        int weightedSum = 0;
        String randomString = String.valueOf(random);

        for (int index = 0; index < randomString.length(); index++) {
            int vekt = Character.getNumericValue(weights.charAt(index));
            int numericValue = Character.getNumericValue(randomString.charAt(index));

            weightedSum += (vekt * numericValue);
        }
        int controlDigit = 11 - (weightedSum % 11);

        if (controlDigit == 1 || controlDigit < 0) {
            return generate(restTemplate);
        }

        String withControl = randomString + controlDigit;

//        try {
//            ResponseEntity<Object> responseEntityq2 = restTemplate.getForEntity("https://modapp-q2.adeo.no/ereg/api/v1/organisasjon/"
//                    + withControl +
//                    "?inkluderHierarki=false&inkluderHistorikk=false", Object.class);
//
//            ResponseEntity<Object> responseEntityq0 = restTemplate.getForEntity("https://modapp-q0.adeo.no/ereg/api/v1/organisasjon/"
//                    + withControl +
//                    "?inkluderHierarki=false&inkluderHistorikk=false", Object.class);
//
//            if (responseEntityq0.getStatusCode() == HttpStatus.OK || responseEntityq2.getStatusCode() == HttpStatus.OK) {
//                return generate(restTemplate);
//            }
//        } catch (HttpClientErrorException e) {
//            return withControl;
//        }
        return withControl;

    }


}
