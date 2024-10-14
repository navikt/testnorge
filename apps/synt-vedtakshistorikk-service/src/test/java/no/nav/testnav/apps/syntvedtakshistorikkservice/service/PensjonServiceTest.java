package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PensjonTestdataFacadeConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataInntekt;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.HttpStatus;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponse;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponseDetails;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataStatus;
import no.nav.testnav.libs.dto.personsearchservice.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PensjonServiceTest {

    @Mock
    private PensjonTestdataFacadeConsumer pensjonTestdataFacadeConsumer;

    @InjectMocks
    private PensjonService pensjonService;

    @Test
    public void shouldOpprettePersonOgInntektIPopp() {
        var miljoe = "TEST";
        var person = PersonDTO.builder()
                .ident("01016412345")
                .foedselsdato(FoedselsdatoDTO.builder()
                        .foedselsdato(LocalDate.of(1964, 1, 1))
                        .build())
                .build();

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

        var result = pensjonService.opprettetPersonOgInntektIPopp(person, miljoe, LocalDate.now());

        assertThat(result).isTrue();
    }

    @Test
    public void shouldAcceptPensjonTimestampString() {
        var miljoe = "TEST";
        var person = PersonDTO.builder()
                .ident("01016412345")
                .foedselsdato(FoedselsdatoDTO.builder()
                        .foedselsdato(LocalDate.of(1964, 1, 1))
                        .build())
                .build();

        var pensjonResponseDetails = new PensjonTestdataResponseDetails(HttpStatus.builder()
                .reasonPhrase("OK")
                .status(200)
                .build(), "Ok", "path", LocalDateTime.now());

        var pensjonResponse = PensjonTestdataResponse.builder()
                .status(Collections.singletonList(PensjonTestdataStatus.builder()
                        .miljo(miljoe)
                        .response(pensjonResponseDetails)
                        .build()))
                .build();

        when(pensjonTestdataFacadeConsumer.opprettPerson(any(PensjonTestdataPerson.class))).thenReturn(pensjonResponse);
        when(pensjonTestdataFacadeConsumer.opprettInntekt(any(PensjonTestdataInntekt.class))).thenReturn(pensjonResponse);

        var result = pensjonService.opprettetPersonOgInntektIPopp(person, miljoe, LocalDate.now());

        assertThat(result).isTrue();
    }
}
