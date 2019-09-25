package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.AaregSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@RunWith(MockitoJUnitRunner.class)
public class AaregSyntPakkenServiceTest {

    @Mock
    private AaregSyntConsumer aaregSyntConsumer;

    @InjectMocks
    private AaregSyntPakkenService aaregSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private boolean lagreIAareg = false;

    @Test
    public void shouldGenerereArbeidsforhold() {
        SyntetiserAaregRequest syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(aaregSyntConsumer.startSyntetisering(syntetiserAaregRequest, lagreIAareg)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity response = aaregSyntPakkenService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, lagreIAareg);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.OK));
        verify(aaregSyntConsumer).startSyntetisering(syntetiserAaregRequest, lagreIAareg);

    }
}