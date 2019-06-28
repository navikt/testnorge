package no.nav.dolly.sts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.hamcrest.MatcherAssert;
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
public class StsOidcFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String OIDC_ALIAS = "security-token-service-token";

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsOidcFasitConsumer stsOidcFasitConsumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getStsOidcService_notFound() {

        when(fasitApiConsumer.fetchResources(anyString(), anyString()))
                .thenReturn(new FasitResourceWithUnmappedProperties[] {});

        expectedException.expect(DollyFunctionalException.class);
        expectedException.expectMessage("Ugyldig sts-miljø/sts-miljø ikke funnet.");

        stsOidcFasitConsumer.getStsOidcService(ENV);
    }

    @Test
    public void getStsOidcService_ok() {

        String serviceUrl = "sts oidc service url";
        Map<String, String> properties = new HashMap<>();
        properties.put("url", serviceUrl);

        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[] {
                        FasitResourceWithUnmappedProperties.builder()
                                .alias(OIDC_ALIAS)
                                .scope(FasitResourceScope.builder()
                                        .zone("fss")
                                        .environmentclass("t")
                                        .build())
                                .properties(properties)
                                .build()
                });

        String service = stsOidcFasitConsumer.getStsOidcService(ENV);

        MatcherAssert.assertThat(service, is(equalTo(serviceUrl)));
    }
}