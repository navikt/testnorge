package no.nav.dolly.budpro.ressursnummer;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ResourceNumberGeneratorTest {

    @Test
    void noSeedShouldGenerateDifferentResults() {
        var generator = new ResourceNumberGenerator(new Random());
        var first = generator.next();
        var second = generator.next();
        assertThat(first)
                .hasSize(6);
        assertThat(second)
                .hasSize(6);
        assertThat(first)
                .isNotEqualTo(second);
    }

    @Test
    void sameSeedShouldGenerateSameResults() {
        var generator1 = new ResourceNumberGenerator(new Random(1L));
        var generator2 = new ResourceNumberGenerator(new Random(1L));
        var generatedFrom1 = generator1.get(100);
        var generatorFrom2 = generator2.get(100);
        assertThat(generatedFrom1)
                .isEqualTo(generatorFrom2);
    }

}
