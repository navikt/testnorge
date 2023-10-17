package no.nav.dolly.budpro.organisasjonsenhet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Organisasjonsenhet {

    private final String id;
    private final String name;

    static Optional<Organisasjonsenhet> of(String[] line) {
        if (lineIsTooShort(line) || lineIsEmpty(line) || lineContainsHeaders(line)) {
            return Optional.empty();
        }
        return Optional.of(new Organisasjonsenhet(line[6], line[7]));
    }

    private static boolean lineIsTooShort(String[] line) {
        return line.length < 7;
    }

    private static boolean lineIsEmpty(String[] line) {
        for (String s : line) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean lineContainsHeaders(String[] line) {
        return line[0].contains("Orgnivå 1 Kode");
    }

}
