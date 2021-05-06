package no.nav.registre.syntrest.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class InputValidator {

    public enum INPUT_STRING_TYPE {MELDEGRUPPE, ENDRINGSKODE_NAV, ENDRINGSKODE, ARBEIDSFORHOLD_TYPE}

    private static final List<String> meldegrupper = new ArrayList<>(Arrays.asList("ATTF", "DAGP", "INDIV", "ARBS", "FY"));

    private static final List<String> endringskoder = new ArrayList<>(Arrays.asList("0110", "0211", "0610", "0710", "1010",
            "1110", "1410", "1810", "2410", "2510", "2610", "2810", "2910", "3210", "3410", "3810", "3910", "4010",
            "4110", "4310", "4410", "4710", "4910", "5110", "5610", "9110"));

    private static final List<String> navEndringskoder = new ArrayList<>(Arrays.asList("Z010", "Z510", "Z310", "ZM10", "Z610",
            "ZV10", "ZD10", "1810", "Z810"));

    private static final List<String> arbeidsforholdTyper = new ArrayList<>(Arrays.asList("ordinaert", "maritimt", "forenklet"));

    public static void validateInput(Integer numToGenerate) {
        if (Objects.isNull(numToGenerate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input \'null\' ikke tillatt");
        }
    }

    public static void validateInput(List<String> fnrs) {
        if (fnrs.stream().anyMatch(s -> s.length() != 11)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Et eller flere fødselsnummer var ugyldig. Må ha lengde 11.");
        }
    }

    public static void validateInput(INPUT_STRING_TYPE type, String value) {
        switch (type) {
        case MELDEGRUPPE:
            if (!meldegrupper.contains(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Ikke en gyldig meldegruppe. Må være en av %s.", meldegrupper.toString()));
            }
            break;
        case ENDRINGSKODE:
            if (!endringskoder.contains(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Ikke en gyldig endringskode (!). Må være en av %s.", endringskoder.toString()));
            }
            break;
        case ENDRINGSKODE_NAV:
            if (!navEndringskoder.contains(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Ikke en gyldig nav endringskode (!). Må være en av %s.", navEndringskoder.toString()));
            }
            break;
        case ARBEIDSFORHOLD_TYPE:
            if(!arbeidsforholdTyper.contains(value)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Ikke en gyldig arbeidsforhold type (!). Må være en av %s.", arbeidsforholdTyper.toString()));
            }
        }
    }
}
