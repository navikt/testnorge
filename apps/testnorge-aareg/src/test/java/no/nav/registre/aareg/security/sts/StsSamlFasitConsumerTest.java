package no.nav.registre.aareg.security.sts;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceScope;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;
import no.nav.registre.aareg.properties.Environment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StsSamlFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String SAML_ALIAS = "securityTokenService";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsSamlFasitConsumer stsSamlFasitConsumer;

    @Test
    public void getStsSamlService_ugyldigMijoe() {
        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[]{});

        expectedException.expect(TestnorgeAaregFunctionalException.class);
        expectedException.expectMessage("Ugyldig sts-miljø/sts-miljø ikke funnet.");

        stsSamlFasitConsumer.getStsSamlService(ENV);
    }

    @Test
    public void getStsSamlService_ok() {
        var stsService = "sts saml service";
        Map<String, String> properties = new HashMap<>();
        properties.put("url", stsService);

        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[]{
                        FasitResourceWithUnmappedProperties.builder()
                                .alias(SAML_ALIAS)
                                .scope(FasitResourceScope.builder()
                                        .zone("fss")
                                        .environment("t1")
                                        .build())
                                .properties(properties)
                                .build()
                });

        var service = stsSamlFasitConsumer.getStsSamlService(ENV);
        assertThat(service, is(equalTo(stsService)));
    }
}