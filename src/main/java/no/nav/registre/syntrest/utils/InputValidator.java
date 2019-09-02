package no.nav.registre.syntrest.utils;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InputValidator {

    enum INPUT_STRING {MELDEGRUPPE, ENDRINGSKODE_NAV, ENDRINGSKODE}

    private final static List<String> meldegrupper = new ArrayList<>(Arrays.asList("ATTF", "DAGP", "INDI", "ARBS", "FY"));

    private final static List<String> endringskoder = new ArrayList<>(Arrays.asList("0110", "0211", "0610", "0710", "1010",
            "1110", "1410", "1810", "2410", "2510", "2610", "2810", "2910", "3210", "3410", "3810", "3910", "4010",
            "4110", "4310", "4410", "4710", "4910", "5110", "5610", "9110"));

    private final static List<String> navEndringskoder = new ArrayList<>(Arrays.asList("Z010", "Z510", "Z310", "ZM10", "Z610",
            "ZV10", "ZD10", "1810", "Z810"));


    public static void validateInput(List<String> fnrs) throws ValidationException {
        HashMap<String, Boolean> rules = new HashMap<>();

        validateFnrs(fnrs, rules);
        checkViolations(rules);
    }

    public static void validateInput(INPUT_STRING type, String value) throws ValidationException {
        HashMap<String, Boolean> rules = new HashMap<>();

        handleInputType(type, value, rules);
        checkViolations(rules);
    }

    public static void validateInput(List<String> fnrs, INPUT_STRING type, String value) throws ValidationException {
        HashMap<String, Boolean> rules = new HashMap<>();

        validateFnrs(fnrs, rules);
        handleInputType(type, value, rules);
        checkViolations(rules);
    }

    private static void handleInputType(INPUT_STRING type, String value, Map<String, Boolean> rules) {
        switch (type) {
            case MELDEGRUPPE:
                rules.put(String.format("Ikke en gyldig meldegruppe. Må være en av %s.", meldegrupper.toString()),
                        meldegrupper.contains(value));
                break;
            case ENDRINGSKODE:
                rules.put(String.format("Ikke en gyldig endringskode (!). Må være en av %s.", endringskoder.toString()),
                        endringskoder.stream().anyMatch(s -> !s.equals(value)));
                break;
            case ENDRINGSKODE_NAV:
                rules.put(String.format("Ikke en gyldig nav endringskode (!). Må være en av %s.", navEndringskoder.toString()),
                        navEndringskoder.stream().anyMatch(s -> !s.equals(value)));
        }
    }

    private static void validateFnrs(List<String> fnrs, Map<String, Boolean> rules) {
        rules.put("Invalid fødselsnummer. Må ha lengde 11.",
                fnrs.stream().anyMatch(s -> s.length() != 11));
    }

    private static void checkViolations(Map<String, Boolean> rules) throws ValidationException {
        List<String> errors = rules.entrySet().stream()
                .filter(s -> s.getValue().equals(true))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
