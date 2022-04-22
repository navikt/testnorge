package no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class InputValidator {

    private static final List<String> VALID_MLJOE = Collections.singletonList("q2");

    public static void validateMiljoe(String miljoe) {
        if (!VALID_MLJOE.contains(miljoe.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Ikke gyldig miljø. Må være en av %s.", VALID_MLJOE));
        }
    }

}
