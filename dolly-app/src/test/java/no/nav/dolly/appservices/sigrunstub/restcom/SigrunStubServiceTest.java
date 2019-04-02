package no.nav.dolly.appservices.sigrunstub.restcom;

import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.bestilling.sigrunstub.SigrunStubService;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class SigrunStubServiceTest {

    private static final String standardPrincipal = "brukernavn";
    private static final String standardIdtoken = "idtoken";
    private static final String standardEierHeaderName = "testdataEier";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @InjectMocks
    private SigrunStubService sigrunStubService;

    @Before
    public void setup() {
        ProvidersProps.SigrunStub sigrunStub = new ProvidersProps.SigrunStub();
        sigrunStub.setUrl("https://localhost:8080");
        when(providersProps.getSigrunStub()).thenReturn(sigrunStub);

        SecurityContextHolder.getContext().setAuthentication(
                new OidcTokenAuthentication(standardPrincipal, null, standardIdtoken, null)
        );
    }

    @Test
    public void createSkattegrunnlag() {
        sigrunStubService.createSkattegrunnlag(singletonList(new RsOpprettSkattegrunnlag()));

        ArgumentCaptor<RequestEntity> argCap = ArgumentCaptor.forClass(RequestEntity.class);
        verify(restTemplate).exchange(argCap.capture(), eq(Object.class));

        HttpEntity entity = argCap.getValue();

        assertThat(entity.getHeaders().getFirst(standardEierHeaderName), is(standardPrincipal));
        assertThat(entity.getHeaders().getFirst("Authorization"), is("Bearer " + standardIdtoken));
    }

    @Test
    public void createSkattegrunnlag_kasterSigrunExceptionHvisKallKasterClientException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(Object.class))).thenReturn(ResponseEntity.badRequest().build());
        ResponseEntity entity = sigrunStubService.createSkattegrunnlag(singletonList(new RsOpprettSkattegrunnlag()));

        assertThat(entity.getStatusCode().value(), is(HttpStatus.BAD_REQUEST.value()));
        assertThat(entity.getStatusCode().getReasonPhrase(), is(HttpStatus.BAD_REQUEST.getReasonPhrase()));
    }
}