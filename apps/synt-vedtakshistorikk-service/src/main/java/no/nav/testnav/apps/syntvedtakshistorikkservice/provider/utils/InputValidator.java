package no.nav.testnav.apps.syntvedtakshistorikkservice.provider.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@UtilityClass
public class InputValidator {

    private static final int MAX_ANTALL_NYE_IDENTER = 10;
    private static final List<String> VALID_MLJOE = Collections.singletonList("q2");

    public static void validateMiljoe(String miljoe) {
        if (!VALID_MLJOE.contains(miljoe.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Ikke gyldig miljø. Må være en av %s.", VALID_MLJOE));
        }
    }

    public static void validateAntallNyeIdenter(Integer antallNyeIdenter) {
        if (isNull(antallNyeIdenter)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Antall nye identer må være satt.");
        }
        if (antallNyeIdenter < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Antall nye identer kan ikke være mindre enn 1.");
        }
        if (antallNyeIdenter > MAX_ANTALL_NYE_IDENTER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Antall nye identer kan ikke være større enn %d.", MAX_ANTALL_NYE_IDENTER));
        }
    }
}
