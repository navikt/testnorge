package no.nav.registre.aareg.security.sts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.aareg.fasit.FasitApiConsumer;
import no.nav.registre.aareg.fasit.FasitResourceScope;
import no.nav.registre.aareg.fasit.FasitResourceWithUnmappedProperties;
import no.nav.registre.aareg.properties.Environment;

@ExtendWith(MockitoExtension.class)
class StsOidcFasitConsumerTest {

    private static final Environment ENV = Environment.TEST;
    private static final String OIDC_ALIAS = "security-token-service-token";


    @Mock
    private FasitApiConsumer fasitApiConsumer;

    @InjectMocks
    private StsOidcFasitConsumer stsOidcFasitConsumer;


    @Test
    void getStsOidcService_ok() {
        var serviceUrl = "sts oidc service url";
        Map<String, String> properties = new HashMap<>();
        properties.put("url", serviceUrl);

        when(fasitApiConsumer.fetchResources(anyString(), anyString())).thenReturn(
                new FasitResourceWithUnmappedProperties[]{
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