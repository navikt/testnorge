package no.nav.dolly.appservices.sigrunstub.restcom;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.exceptions.SigrunnStubException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubApiServiceTest {

    private static final String standardPrincipal = "brukernavn";
    private static final String standardIdtoken = "idtoken";
    private static final String standardEierHeaderName = "testdataEier";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private SigrunStubApiService sigrunStubApiService;

    @Before
    public void setup() {
        ProvidersProps.Sigrun sigrun = new ProvidersProps().new Sigrun();
        sigrun.setUrl("https://localhost:8080");
        when(providersProps.getSigrun()).thenReturn(sigrun);

        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(standardPrincipal, null, standardIdtoken, null)
        );
    }

    @Test
    public void createSkattegrunnlag() {
        sigrunStubApiService.createSkattegrunnlag(Arrays.asList(new RsSigrunnOpprettSkattegrunnlag()));

        ArgumentCaptor<HttpEntity> argCap = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argCap.capture(), eq(String.class));

        HttpEntity entity = argCap.getValue();

        assertThat(entity.getHeaders().getFirst(standardEierHeaderName), is(standardPrincipal));
        assertThat(entity.getHeaders().getFirst("Authorization"), is("Bearer " + standardIdtoken));
    }

    @Test(expected = SigrunnStubException.class)
    public void createSkattegrunnlag_kasterSigrunExceptionHvisKallKasterClientException() {
        HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "OK");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenThrow(clientErrorException);
        sigrunStubApiService.createSkattegrunnlag(Arrays.asList(new RsSigrunnOpprettSkattegrunnlag()));
    }
}