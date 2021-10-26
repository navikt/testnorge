package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@ExtendWith(MockitoExtension.class)

public class TestnorgeAaregServiceTest {

    @Mock
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @InjectMocks
    private TestnorgeAaregService testnorgeAaregService;

    @Test
    public void shouldGenerereArbeidsforhold() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;
        var sendAlleEksisterende = false;

        var syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(testnorgeAaregConsumer.startSyntetisering(syntetiserAaregRequest, sendAlleEksisterende)).thenReturn(ResponseEntity.ok().build());

        var response = testnorgeAaregService.genererArbeidsforholdsmeldinger(syntetiserAaregRequest, sendAlleEksisterende);

        assertThat(response.getStatusCode(), Matchers.is(HttpStatus.OK));
        verify(testnorgeAaregConsumer).startSyntetisering(syntetiserAaregRequest, sendAlleEksisterende);

    }
}