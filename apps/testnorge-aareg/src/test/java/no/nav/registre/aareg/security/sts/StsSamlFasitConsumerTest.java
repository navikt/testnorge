package no.nav.registre.aareg.security.sts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceScope;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;
import no.nav.registre.aareg.properties.Environment;

@ExtendWith(MockitoExtension.class)
public class StsSamlFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String SAML_ALIAS = "securityTokenService";


    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsSamlFasitConsumer stsSamlFasitConsumer;

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