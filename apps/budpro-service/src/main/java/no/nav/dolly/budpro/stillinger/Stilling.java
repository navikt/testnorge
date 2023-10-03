package no.nav.dolly.budpro.stillinger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Slf4j
public class Stilling {

    private final String number;
    private final String name;

    static Optional<Stilling> of(String[] line) {
        if (lineIsTooShort(line) || lineContainsHeaders(line) || lineIsNotRelevant(line)) {
            return Optional.empty();
        }
        log.info("Accepted line: {}", (Object) line);
        return Optional.of(new Stilling(line[0], line[1]));
    }

    private static boolean lineIsTooShort(String[] line) {
        return line.length < 4;
    }

    private static boolean lineContainsHeaders(String[] line) {
        return line[0].contains("code");
    }

    private static boolean lineIsNotRelevant(String[] line) {
        return !line[3].contains("OFFENTLIG") && !line[3].contains("OFF.");
    }

}
