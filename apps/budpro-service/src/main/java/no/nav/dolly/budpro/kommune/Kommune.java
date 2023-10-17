package no.nav.dolly.budpro.kommune;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
public class Kommune {

    private final String id;
    private final String navn;

    static Optional<Kommune> of(String[] line) {
        if (lineIsTooShort(line) || lineContainsHeaders(line)) {
            return Optional.empty();
        }
        return Optional.of(new Kommune(line[0], line[3]));
    }

    private static boolean lineIsTooShort(String[] line) {
        return line.length < 4;
    }

    private static boolean lineContainsHeaders(String[] line) {
        return line[0].contains("code");
    }

}
