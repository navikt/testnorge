package no.nav.dolly.budpro.ressursnummer;

import java.util.Random;

public class LeaderGenerator {

    private final Leader[] leaders;

    public LeaderGenerator(String[] names, String[] resourceNumbers) {
        if (names.length != resourceNumbers.length) {
            throw new IllegalArgumentException("Names (%d) and resource numbers (%d) must have the same length".formatted(names.length, resourceNumbers.length));
        }

        this.leaders = new Leader[names.length];
        for (int i = 0; i < names.length; i++) {
            leaders[i] = new Leader(
                    null,
                    names[i],
                    resourceNumbers[i]
            );
        }
    }

    public Leader getRandom(Random random) {
        return leaders[random.nextInt(leaders.length)];
    }

    public record Leader(
            String utlaantFra,
            String navn,
            String ressursnummer
    ) {
    }

}
