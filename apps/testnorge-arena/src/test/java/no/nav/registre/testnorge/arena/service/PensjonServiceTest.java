package no.nav.registre.testnorge.arena.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.testnorge.arena.consumer.rs.PensjonTestdataFacadeConsumer;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataInntekt;
import no.nav.registre.testnorge.arena.consumer.rs.request.pensjon.PensjonTestdataPerson;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.HttpStatus;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponse;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataResponseDetails;
import no.nav.registre.testnorge.arena.consumer.rs.response.pensjon.PensjonTestdataStatus;

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
