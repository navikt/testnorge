package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.NavSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@RunWith(MockitoJUnitRunner.class)
public class TpsSyntPakkenServiceTest {

    @Mock
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Mock
    private NavSyntConsumer navSyntConsumer;

    @InjectMocks
    private TpsSyntPakkenService tpsSyntPakkenService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private Map<String, Integer> antallMeldingerPerEndringskodeSkd = new HashMap<>();
    private Map<String, Integer> antallMeldingerPerEndringskodeNav = new HashMap<>();
    private SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons;
    private ResponseEntity expectedResponseSkd;
    private ResponseEntity expectedResponseNav;

    @Before
    public void setUp() {
        skdMeldingerTilTpsRespons = new SkdMeldingerTilTpsRespons();
        skdMeldingerTilTpsRespons.setAntallSendte(2);
        skdMeldingerTilTpsRespons.setAntallFeilet(0);

        expectedResponseSkd = ResponseEntity.status(HttpStatus.CREATED).body(skdMeldingerTilTpsRespons);
        expectedResponseNav = ResponseEntity.ok().build();
    }

    @Test
    public void shouldGenerereSkdmeldinger() {
        when(testnorgeSkdConsumer.startSyntetisering(any(SyntetiserSkdmeldingerRequest.class))).thenReturn(expectedResponseSkd);

        SkdMeldingerTilTpsRespons response = (SkdMeldingerTilTpsRespons) tpsSyntPakkenService.genererSkdmeldinger(avspillergruppeId, miljoe, antallMeldingerPerEndringskodeSkd).getBody();

        assert response != null;
        assertThat(response.getAntallSendte(), equalTo(2));
        assertThat(response.getAntallFeilet(), equalTo(0));
        verify(testnorgeSkdConsumer).startSyntetisering(any(SyntetiserSkdmeldingerRequest.class));
    }

    @Test
    public void shouldGenerereNavmeldinger() {
        SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest = new SyntetiserNavmeldingerRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskodeNav);

        when(navSyntConsumer.startSyntetisering(syntetiserNavmeldingerRequest)).thenReturn(expectedResponseNav);

        tpsSyntPakkenService.genererNavmeldinger(syntetiserNavmeldingerRequest);

        verify(navSyntConsumer).startSyntetisering(syntetiserNavmeldingerRequest);
    }
}