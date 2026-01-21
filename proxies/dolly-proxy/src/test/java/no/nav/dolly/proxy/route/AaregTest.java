package no.nav.dolly.proxy.route;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
class AaregTest {

    @Autowired
    private Aareg aareg;

    @Test
    void testCorrectNames() {

        // Readable.
        assertThat(aareg.getName(Aareg.SpecialCase.Q1, false))
                .isEqualTo("aareg-services-nais-q1");
        assertThat(aareg.getName(Aareg.SpecialCase.Q2, false))
                .isEqualTo("aareg-services-nais");
        assertThat(aareg.getName(Aareg.SpecialCase.Q4, false))
                .isEqualTo("aareg-services-nais-q4");

        // Writeable.
        assertThat(aareg.getName(Aareg.SpecialCase.Q1, true))
                .isEqualTo("aareg-dolly-api-q1");
        assertThat(aareg.getName(Aareg.SpecialCase.Q2, true))
                .isEqualTo("aareg-dolly-api-q2");
        assertThat(aareg.getName(Aareg.SpecialCase.Q4, true))
                .isEqualTo("aareg-dolly-api-q4");

    }

    @Test
    void testCorrectUrls() {

        // Readable.
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q1, false))
                .isEqualTo("http://aareg-services-q1.arbeidsforhold.svc.nais.local");
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q2, false))
                .isEqualTo("http://aareg-services.arbeidsforhold.svc.nais.local");
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q4, false))
                .isEqualTo("http://aareg-services-q4.arbeidsforhold.svc.nais.local");

        // Writeable.
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q1, true))
                .isEqualTo("http://aareg-dolly-api-q1.arbeidsforhold.svc.nais.local");
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q2, true))
                .isEqualTo("http://aareg-dolly-api-q2.arbeidsforhold.svc.nais.local");
        assertThat(aareg.getUrl(Aareg.SpecialCase.Q4, true))
                .isEqualTo("http://aareg-dolly-api-q4.arbeidsforhold.svc.nais.local");

    }

}
