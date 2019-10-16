package no.nav.dolly.sts;

import static no.nav.dolly.properties.Environment.TEST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.properties.CredentialsProps;
import no.nav.dolly.security.sts.StsOidcFasitConsumer;
import no.nav.dolly.security.sts.StsOidcService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class StsOidcServiceTest {

    private static final String ENV = "u1";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private StsOidcFasitConsumer stsOidcFasitConsumer;
    @Mock
    private CredentialsProps credentialsProps;
    @InjectMocks
    private StsOidcService stsOidcService;
    @Mock
    private JsonNode jsonNode;

    @Mock
    private JsonNode jsonNode2;

    @Test
    public void getIdToken_sikkerhetsTokenKunneIkkeFornyes() {

        expectedException.expect(DollyFunctionalException.class);
        expectedException.expectMessage("Sikkerhet-token kunne ikke fornyes");

        stsOidcService.getIdToken(ENV);
    }

    @Test
    public void getIdToken_ok() {

        when(jsonNode.get("expires_in")).thenReturn(jsonNode2);
        when(jsonNode.get("access_token")).thenReturn(jsonNode2);

        when(restTemplate.exchange(any(RequestEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(jsonNode));

        when(stsOidcFasitConsumer.getStsOidcService(TEST)).thenReturn("baseUrl");

        String token = stsOidcService.getIdToken(ENV);

        assertThat(token, containsString("Bearer"));
    }
}