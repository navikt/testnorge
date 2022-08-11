package no.nav.testnav.apps.skdservice.service;

import no.nav.testnav.apps.skdservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FasteMeldingerServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private FasteMeldingerService fasteMeldingerService;

    private final Long avspillergruppeId = 10L;
    private final String miljoe = "t1";
    private List<Long> avspillergruppeIder;

    @Before
    public void setUp() {
        avspillergruppeIder = new ArrayList<>(Arrays.asList(123L, 234L));
    }

    @Test
    public void shouldStartAvspillingAvAvspillergruppe() {
        when(tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId)).thenReturn(Mono.just(avspillergruppeIder));
        when(tpsfConsumer.sendSkdmeldingerToTps(any(), any()))
                .thenReturn(Mono.just(SkdMeldingerTilTpsRespons.builder().antallSendte(1).antallFeilet(0).build()));

        var response = fasteMeldingerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe).block();

        verify(tpsfConsumer).getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        verify(tpsfConsumer).sendSkdmeldingerToTps(eq(avspillergruppeId), any());
        assertThat(response).isNotNull();
        assertThat(1).isEqualTo(response.getAntallSendte());
        assertThat(0).isEqualTo(response.getAntallFeilet());
    }
}