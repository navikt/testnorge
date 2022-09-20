package no.nav.registre.aareg.consumer.ws;

import no.nav.registre.aareg.consumer.rs.MiljoerConsumer;
import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AaregBehandleArbeidsforholdTest {

    @Mock
    private MiljoerConsumer miljoerConsumer;

    @InjectMocks
    private AaregBehandleArbeidsforhold behandleArbeidsforhold;

    @Test
    void fetchWsUrslAllEnvironments_OK() {

        when(miljoerConsumer.hentMiljoer()).thenReturn(new MiljoerResponse(List.of("q2", "t2")));

        var fasitConsumers = behandleArbeidsforhold.fetchWsUrlsAllEnvironments();

        var expectedTestUrl = "https://modapp-t2.adeo.no/aareg-core/BehandleArbeidsforholdService/v1";
        var expectedPreprodUrl = "https://modapp-q2.adeo.no/aareg-core/BehandleArbeidsforholdService/v1";

        assertThat(fasitConsumers.get("t2"), is(equalTo(expectedTestUrl)));
        assertThat(fasitConsumers.get("q2"), is(equalTo(expectedPreprodUrl)));
    }
}