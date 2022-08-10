package no.nav.testnav.apps.skdservice.provider;

import no.nav.testnav.apps.skdservice.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.apps.skdservice.service.FasteMeldingerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SkdControllerTest {

    @Mock
    private FasteMeldingerService fasteMeldingerService;

    @InjectMocks
    private SkdController skdController;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";

    @Test
    public void shouldStartAvspilling() {
        when(fasteMeldingerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe))
                .thenReturn(SkdMeldingerTilTpsRespons.builder()
                        .antallSendte(1)
                        .antallFeilet(0)
                        .build());
        var response = skdController.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);

        verify(fasteMeldingerService).startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);
        assertThat(response.getAntallSendte()).isEqualTo(1);
        assertThat(response.getAntallFeilet()).isEqualTo(0);
    }
}