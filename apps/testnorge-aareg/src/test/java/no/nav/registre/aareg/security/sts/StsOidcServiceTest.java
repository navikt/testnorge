package no.nav.registre.aareg.security.sts;

import static no.nav.registre.aareg.properties.Environment.TEST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
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

import no.nav.registre.aareg.exception.TestnorgeAaregFunctionalException;
import no.nav.registre.aareg.properties.CredentialsProps;

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

    @Mock
    private JsonNode jsonNode;

    @Mock
    private JsonNode jsonNode2;

    @InjectMocks
    private StsOidcService stsOidcService;

    @Test
    public void getIdToken_sikkerhetsTokenKunneIkkeFornyes() {
        expectedException.expect(TestnorgeAaregFunctionalException.class);
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

        var token = stsOidcService.getIdToken(ENV);

        assertThat(token, containsString("Bearer"));
    }
}