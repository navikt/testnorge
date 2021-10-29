package no.nav.dolly.sts;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.properties.CredentialsProps;
import no.nav.dolly.security.sts.StsOidcService;

@RunWith(MockitoJUnitRunner.class)
public class StsOidcServiceTest {

    private static final String ENV = "u1";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CredentialsProps credentialsProps;

    @InjectMocks
    private StsOidcService stsOidcService;

    @Mock
    private JsonNode jsonNode;

    @Mock
    private JsonNode jsonNode2;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(stsOidcService, "stsTokenProviderTestUrl", "testUrl");
    }

    @Test
    public void getIdToken_sikkerhetsTokenKunneIkkeFornyes() {

        DollyFunctionalException dfe = assertThrows(
                DollyFunctionalException.class, (() -> stsOidcService.getIdToken(ENV)));

        assertThat(dfe.getMessage(), is(equalTo("Sikkerhet-token kunne ikke fornyes")));
    }

    @Test
    public void getIdToken_ok() {

        when(jsonNode.get("expires_in")).thenReturn(jsonNode2);
        when(jsonNode.get("access_token")).thenReturn(jsonNode2);

        when(restTemplate.exchange(any(RequestEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(jsonNode));

        String token = stsOidcService.getIdToken(ENV);

        assertThat(token, containsString("Bearer"));
    }
}