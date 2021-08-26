package no.nav.registre.aareg.consumer.rs;

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
public class AaregArbeidsforholdFasitConsumerTest {

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;

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
        var urlResult = aaregArbeidsforholdFasitConsumer.getUrlForEnv("t0");

        assertThat(urlResult, is(equalTo("BaseUrl/v1/arbeidstaker/arbeidsforhold")));
    }
}