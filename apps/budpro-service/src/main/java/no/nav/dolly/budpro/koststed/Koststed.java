package no.nav.dolly.budpro.koststed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Koststed {

    private final String id;
    private final String beskrivelse;

    static Optional<Koststed> of(String[] line) {
        if (lineIsTooShort(line) || lineContainsHeaders(line) || lineIsInactive(line)) {
            return Optional.empty();
        }
        return Optional.of(new Koststed(line[10], line[11]));
    }

    private static boolean lineIsTooShort(String[] line) {
        return line.length < 11;
    }

    private static boolean lineContainsHeaders(String[] line) {
        return line[10].equals("Nivå 5");
    }

    private static boolean lineIsInactive(String[] line) {
        return line[11].startsWith("AVSL");
    }

}
