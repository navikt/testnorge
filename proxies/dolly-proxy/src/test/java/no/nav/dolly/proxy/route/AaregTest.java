package no.nav.dolly.proxy.route;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class AaregTest {

    @Autowired
    private Aareg aareg;

    @Test
    void shouldUseReadableNamesPerEnvironment() {
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q1, false).getName())
                .isEqualTo("aareg-services-nais-q1");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q2, false).getName())
                .isEqualTo("aareg-services-nais");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q4, false).getName())
                .isEqualTo("aareg-services-nais-q4");
    }

    @Test
    void shouldUseWriteableNamesPerEnvironment() {
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q1, true).getName())
                .isEqualTo("aareg-dolly-api-q1");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q2, true).getName())
                .isEqualTo("aareg-dolly-api-q2");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q4, true).getName())
                .isEqualTo("aareg-dolly-api-q4");
    }

    @Test
    void shouldUseReadableUrlsPerEnvironment() {
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q1, false).getUrl())
                .contains("://aareg-services-q1.");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q2, false).getUrl())
                .contains("://aareg-services.");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q4, false).getUrl())
                .contains("://aareg-services-q4.");
    }

    @Test
    void shouldUseWriteableUrlsPerEnvironment() {
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q1, true).getUrl())
                .contains("://aareg-dolly-api-q1.");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q2, true).getUrl())
                .contains("://aareg-dolly-api-q2.");
        assertThat(aareg.getServerProperties(Aareg.SpecialCase.Q4, true).getUrl())
                .contains("://aareg-dolly-api-q4.");
    }

}
