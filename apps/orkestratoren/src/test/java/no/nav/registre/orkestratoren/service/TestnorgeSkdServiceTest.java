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

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeNavEndringsmeldingerConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@RunWith(MockitoJUnitRunner.class)
public class TestnorgeSkdServiceTest {

    @Mock
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Mock
    private TestnorgeNavEndringsmeldingerConsumer testnorgeNavEndringsmeldingerConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private TestnorgeSkdService testnorgeSkdService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private Map<String, Integer> antallMeldingerPerEndringskodeSkd = new HashMap<>();
    private Map<String, Integer> antallMeldingerPerEndringskodeNav = new HashMap<>();
    private ResponseEntity expectedResponseSkd;
    private ResponseEntity expectedResponseNav;

    @Before
    public void setUp() {
        var skdMeldingerTilTpsRespons = new SkdMeldingerTilTpsRespons();
        skdMeldingerTilTpsRespons.setAntallSendte(2);
        skdMeldingerTilTpsRespons.setAntallFeilet(0);

        expectedResponseSkd = ResponseEntity.status(HttpStatus.CREATED).body(skdMeldingerTilTpsRespons);
        expectedResponseNav = ResponseEntity.ok().build();
    }

    @Test
    public void shouldGenerereSkdmeldinger() {
        when(testnorgeSkdConsumer.startSyntetisering(any(SyntetiserSkdmeldingerRequest.class))).thenReturn(expectedResponseSkd);

        var response = (SkdMeldingerTilTpsRespons) testnorgeSkdService.genererSkdmeldinger(avspillergruppeId, miljoe, antallMeldingerPerEndringskodeSkd).getBody();

        assert response != null;
        assertThat(response.getAntallSendte(), equalTo(2));
        assertThat(response.getAntallFeilet(), equalTo(0));
        verify(testnorgeSkdConsumer).startSyntetisering(any(SyntetiserSkdmeldingerRequest.class));
        verify(hodejegerenHistorikkConsumer).oppdaterHodejegerenCache(avspillergruppeId);
    }

    @Test
    public void shouldGenerereNavmeldinger() {
        var syntetiserNavmeldingerRequest = new SyntetiserNavmeldingerRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskodeNav);

        when(testnorgeNavEndringsmeldingerConsumer.startSyntetisering(syntetiserNavmeldingerRequest)).thenReturn(expectedResponseNav);

        testnorgeSkdService.genererNavmeldinger(syntetiserNavmeldingerRequest);

        verify(testnorgeNavEndringsmeldingerConsumer).startSyntetisering(syntetiserNavmeldingerRequest);
    }
}