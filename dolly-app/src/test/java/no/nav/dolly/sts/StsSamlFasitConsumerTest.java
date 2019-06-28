package no.nav.dolly.sts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.fasit.FasitApiConsumer;
import no.nav.dolly.fasit.FasitResourceScope;
import no.nav.dolly.fasit.FasitResourceWithUnmappedProperties;
import no.nav.dolly.properties.Environment;

@RunWith(MockitoJUnitRunner.class)
public class StsSamlFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String SAML_ALIAS = "securityTokenService";

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsSamlFasitConsumer stsSamlFasitConsumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getStsSamlService_ugyldigMijoe() {

        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[] {});

        expectedException.expect(DollyFunctionalException.class);
        expectedException.expectMessage("Ugyldig sts-miljø/sts-miljø ikke funnet.");

        stsSamlFasitConsumer.getStsSamlService(ENV);
    }

    @Test
    public void getStsSamlService_ok() {

        String stsService = "sts saml service";
        Map<String, String> properties = new HashMap<>();
        properties.put("url", stsService);

        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[] {
                        FasitResourceWithUnmappedProperties.builder()
                                .alias(SAML_ALIAS)
                                .scope(FasitResourceScope.builder()
                                        .zone("fss")
                                        .environment("t1")
                                        .build())
                                .properties(properties)
                                .build()
                });

        String service = stsSamlFasitConsumer.getStsSamlService(ENV);
        assertThat(service, is(equalTo(stsService)));
    }
}