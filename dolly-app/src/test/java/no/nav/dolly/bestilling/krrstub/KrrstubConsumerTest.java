package no.nav.dolly.bestilling.krrstub;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class KrrstubConsumerTest {

    private static final String CURRENT_BRUKER_IDENT = "user";
    private static final Long BEST_ID = 1L;
    private static final String EPOST = "morro.pa@landet.no";
    private static final String MOBIL = "11111111";
    private static final boolean RESVERT = true;
    private static final String BASE_URL = "baseUrl";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private KrrstubConsumer krrStubConsumer;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static Authentication authentication;

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

        krrStubConsumer.createDigitalKontaktdata(DigitalKontaktdata.builder()
                .epost(EPOST)
                .mobil(MOBIL)
                .reservert(RESVERT)
                .build());

        verify(providersProps).getKrrStub();
        verify(restTemplate).exchange(any(RequestEntity.class), eq(Object.class));
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
        verify(restTemplate).exchange(any(RequestEntity.class), eq(Object.class));
    }
}