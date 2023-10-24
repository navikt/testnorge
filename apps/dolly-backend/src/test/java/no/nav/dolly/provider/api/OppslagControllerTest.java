package no.nav.dolly.provider.api;

import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OppslagControllerTest {

    private static final String STANDARD_KODEVERK_NAME = "name";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KodeverkMapper kodeverkMapper;

    @InjectMocks
    private OppslagController oppslagController;

    @Mock
    private KodeverkAdjusted kodeverkAdjusted;

    @Mock
    private KodeverkBetydningerResponse kodeverkKoderBetydningerResponse;

    @Test
    void fetchKodeverkByName_happyPath() {

        when(kodeverkConsumer.fetchKodeverkByName(STANDARD_KODEVERK_NAME)).thenReturn(Flux.just(kodeverkKoderBetydningerResponse));

        when(kodeverkMapper.mapBetydningToAdjustedKodeverk(eq(STANDARD_KODEVERK_NAME), any()))
                .thenReturn(Flux.just(kodeverkAdjusted));

        var kodeverkResponse = oppslagController.fetchKodeverkByName(STANDARD_KODEVERK_NAME);

        assertThat(kodeverkResponse, is(kodeverkAdjusted));
    }
}