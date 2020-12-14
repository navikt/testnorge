package no.nav.registre.aareg.consumer.ws;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AaregBehandleArbeidsforholdTest {

    @InjectMocks
    private AaregBehandleArbeidsforhold behandleArbeidsforholdFasitConsumer;

    @Test
    public void fetchWsUrslAllEnvironments_OK() {
        var fasitConsumers = behandleArbeidsforholdFasitConsumer.fetchWsUrlsAllEnvironments();

        var expectedUrl = "https://modapp-t2.adeo.no/aareg-services/BehandleArbeidsforholdService/v1";

        assertThat(fasitConsumers.get("t2"), is(equalTo(expectedUrl)));
    }
}