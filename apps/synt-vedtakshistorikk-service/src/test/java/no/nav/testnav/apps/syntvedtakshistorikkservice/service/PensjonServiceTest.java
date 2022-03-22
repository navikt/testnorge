package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PensjonTestdataFacadeConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.HttpStatus;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponse;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponseDetails;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PensjonServiceTest {

    @Mock
    private PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;

    @InjectMocks
    private PensjonService pensjonService;

    @Test
    public void shouldOpprettePersonOgInntektIPopp() {
        var miljoe = "TEST";
        var ident = "01016412345";

        var pensjonResponse = PensjonTestdataResponse.builder()
                .status(Collections.singletonList(PensjonTestdataStatus.builder()
                        .miljo(miljoe)
                        .response(PensjonTestdataResponseDetails.builder()
                                .httpStatus(HttpStatus.builder()
                                        .status(200)
                                        .build())
                                .build())
                        .build()))
                .build();

        when(pensjonTestdataFacadeConsumer.opprettPerson(any(PensjonTestdataPerson.class))).thenReturn(pensjonResponse);
        when(pensjonTestdataFacadeConsumer.opprettInntekt(any(PensjonTestdataInntekt.class))).thenReturn(pensjonResponse);

        var result = pensjonService.opprettetPersonOgInntektIPopp(ident, miljoe, LocalDate.now());

        assertThat(result).isTrue();
    }
}
