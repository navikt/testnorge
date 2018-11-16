package no.nav.registre.orkestratoren;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringInntektCompTest {

    /**
     * Orkestratoren forventer å få fødselsnumrene i samme rekkefølge fra TPSF avspillergruppen hver gang.
     * Formålet er at de alle fødselsnumre som tidligere har blitt sendt til inntekt-synt,
     * skal sendes dit igjen for å få månedens inntektsmelding.
     * I tillegg kommer nye fødselsnumre ettersom listen med fødselsnumre i TPSF avspillergruppen forventes å øke.
     * <p>
     * Test-scenario: Første halvdel av fødselsnummer-listen fra TPSF avspillergruppen blir sendt til
     * Inntekt-synt "generer syntetisk inntektsmelding"-endepunktet.
     */
    @Test
    public void shouldBestilleinntektsmeldingForFoersteHalvpartAvFnrFraTpsf() {

    }
}
