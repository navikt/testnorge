package no.nav.testnav.apps.generersyntmeldekortservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputValidator {

    private InputValidator() {
        throw new IllegalStateException("Utility class");
    }

    private static final List<String> meldegrupper = new ArrayList<>(Arrays.asList("ATTF", "DAGP", "INDIV", "ARBS", "FY"));

    public static Mono<Void> validateInput(String value) {
        return Mono.defer(() -> {
            if (!meldegrupper.contains(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Ikke en gyldig meldegruppe. Må være en av %s.", meldegrupper));
            }
            return Mono.empty();
        });
    }

}
