package no.nav.registre.aareg.consumer.ws;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.security.sts.StsSamlTokenService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BehandleArbeidsforholdV1ProxyTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private AaregBehandleArbeidsforhold behandleArbeidsforholdFasitConsumer;

    @Mock
    private StsSamlTokenService stsSamlTokenService;

    @InjectMocks
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Test
    public void getServiceByEnvironment_ugyldigMiljoe() {
        expectedException.expect(TestnorgeAaregFunctionalException.class);
        expectedException.expectMessage("Ugyldig miljø/miljø ikke funnet.");
        behandleArbeidsforholdV1Proxy.getServiceByEnvironment("t0");
    }
}