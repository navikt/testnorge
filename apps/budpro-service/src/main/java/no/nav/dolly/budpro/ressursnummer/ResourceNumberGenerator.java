package no.nav.dolly.budpro.ressursnummer;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ResourceNumberGenerator {

    private final Random random;

    private final Set<String> assigned = new HashSet<>();

    public String next() {
        String generated;
        do {
            var a = random.nextInt(99);
            var b = random.nextInt(99);
            var c = random.nextInt(99);
            generated = "%02d%02d%02d".formatted(a, b, c);
        } while (assigned.contains(generated));
        assigned.add(generated);
        return generated;
    }

    public String[] get(int limit) {
        return Stream
                .generate(this::next)
                .limit(limit)
                .distinct()
                .toArray(String[]::new);
    }

}
