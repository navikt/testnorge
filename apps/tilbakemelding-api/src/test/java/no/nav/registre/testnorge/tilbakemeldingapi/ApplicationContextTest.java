package no.nav.registre.testnorge.tilbakemeldingapi;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.springframework.context.annotation.Import;

@DollySpringBootTest
@Import(SecurityTestConfig.class)
class ApplicationContextTest extends DollyApplicationContextTest {
}
