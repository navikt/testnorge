package no.nav.dolly.bestilling.krrstub;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;

import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(SpringRunner.class)
@RestClientTest(KrrstubConsumer.class)
public class KrrstubConsumerTest {

    private static final String CURRENT_BRUKER_IDENT = "user";
    private static final Long BEST_ID = 1L;
    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final boolean RESVERT = true;
    private static final String BASE_URL = "baseUrl";
    private static Authentication authentication;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private MockRestServiceServer server;

    @MockBean
    private ProvidersProps providersProps;

    @Autowired
    private KrrstubConsumer krrStubConsumer;

    @BeforeClass
    public static void beforeClass() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(CURRENT_BRUKER_IDENT, null, null, null));
    }

    @AfterClass
    public static void afterClass() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void createDigitalKontaktdata_Ok() {

        when(providersProps.getKrrStub()).thenReturn(ProvidersProps.KrrStub.builder()
                .url(BASE_URL).build());

        server.expect(requestTo("baseUrl/api/v1/kontaktinformasjon"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESVERT)
                .build());

        verify(providersProps).getKrrStub();
    }

    @Test
    public void createDigitalKontaktdata_Feilet() {

        when(providersProps.getKrrStub()).thenThrow(HttpClientErrorException.class);

        expectedException.expect(RuntimeException.class);

        krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESVERT)
                .build());

        verify(providersProps).getKrrStub();
    }
}