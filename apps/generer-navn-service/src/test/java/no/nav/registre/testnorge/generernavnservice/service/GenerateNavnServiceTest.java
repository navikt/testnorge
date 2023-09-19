package no.nav.registre.testnorge.generernavnservice.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GenerateNavnServiceTest {

    private final GenerateNavnService service = new GenerateNavnService();

    @Test
    void noSeedShouldGiveDifferentListsOfNames() {
        var names1 = service.getRandomNavn(null, 100);
        var names2 = service.getRandomNavn(null, 100);
        assertThat(names1)
                .isNotEqualTo(names2);
    }

    @Test
    void sameSeedShouldGiveSameListOfNames() {
        var names1 = service.getRandomNavn(1000L, 100);
        var names2 = service.getRandomNavn(1000L, 100);
        assertThat(names1)
                .isEqualTo(names2);
    }

}
