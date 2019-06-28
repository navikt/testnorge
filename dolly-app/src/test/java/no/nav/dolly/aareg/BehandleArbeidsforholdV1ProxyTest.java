package no.nav.dolly.aareg;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.sts.StsSamlTokenService;
import no.nav.tjeneste.domene.behandlearbeidsforhold.v1.BehandleArbeidsforholdPortType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class BehandleArbeidsforholdV1ProxyTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private AaregBehandleArbeidsforholdFasitConsumer behandleArbeidsforholdFasitConsumer;
    @Mock
    private StsSamlTokenService stsSamlTokenService;
    @InjectMocks
    private BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy;

    @Test
    public void getServiceByEnvironment_OK() {

        Map<String, String> fasitEntry = new HashMap<>();
        fasitEntry.put("t0", "BaseUrl/aareg-core/BehandleArbeidsforholdService/v1");
        when(behandleArbeidsforholdFasitConsumer.fetchWsUrlsAllEnvironments()).thenReturn(fasitEntry);
        Object portType = behandleArbeidsforholdV1Proxy.getServiceByEnvironment("t0");

        assertThat(portType, is(instanceOf(BehandleArbeidsforholdPortType.class)));
    }

    @Test
    public void getServiceByEnvironment_ugyldigMiljoe() {

        expectedException.expect(DollyFunctionalException.class);
        expectedException.expectMessage("Ugyldig miljø/miljø ikke funnet.");
        behandleArbeidsforholdV1Proxy.getServiceByEnvironment("t0");
    }
}