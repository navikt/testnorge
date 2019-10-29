package no.nav.registre.aareg.consumer.ws;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceScope;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;

@RunWith(MockitoJUnitRunner.class)
public class AaregBehandleArbeidsforholdFasitConsumerTest {

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private AaregBehandleArbeidsforholdFasitConsumer behandleArbeidsforholdFasitConsumer;

    @Before
    public void setup() {
        Map<String, String> map = new HashMap<>();
        map.put("url", "BaseUrl");

        when(fasitApiConsumer.fetchResources(anyString(), anyString()))
                .thenReturn(new FasitResourceWithUnmappedProperties[] {
                        FasitResourceWithUnmappedProperties.builder()
                                .scope(FasitResourceScope.builder()
                                        .environment("t0")
                                        .zone("fss")
                                        .build())
                                .properties(map)
                                .build()
                });
    }

    @Test
    public void fetchWsUrslAllEnvironments_OK() {
        Map<String, String> fasitConsumers = behandleArbeidsforholdFasitConsumer.fetchWsUrlsAllEnvironments();

        assertThat(fasitConsumers.get("t0"), is(equalTo("BaseUrl/aareg-core/BehandleArbeidsforholdService/v1")));
    }
}