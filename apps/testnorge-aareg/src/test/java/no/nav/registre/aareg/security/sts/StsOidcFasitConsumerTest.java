package no.nav.registre.aareg.security.sts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceScope;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;
import no.nav.registre.aareg.properties.Environment;

@RunWith(MockitoJUnitRunner.class)
public class StsOidcFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String OIDC_ALIAS = "security-token-service-token";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsOidcFasitConsumer stsOidcFasitConsumer;

    @Test
    public void getStsOidcService_notFound() {
        when(fasitApiConsumer.fetchResources(anyString(), anyString()))
                .thenReturn(new FasitResourceWithUnmappedProperties[] {});

        expectedException.expect(TestnorgeAaregFunctionalException.class);
        expectedException.expectMessage("Ugyldig sts-miljø/sts-miljø ikke funnet.");

        stsOidcFasitConsumer.getStsOidcService(ENV);
    }

    @Test
    public void getStsOidcService_ok() {
        var serviceUrl = "sts oidc service url";
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

        var service = stsOidcFasitConsumer.getStsOidcService(ENV);

        MatcherAssert.assertThat(service, is(equalTo(serviceUrl)));
    }
}