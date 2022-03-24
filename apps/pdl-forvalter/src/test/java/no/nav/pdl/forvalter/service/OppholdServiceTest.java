package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO.OppholdType.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO.OppholdType.OPPLYSNING_MANGLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OppholdServiceTest {

    @InjectMocks
    private OppholdService oppholdService;

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .oppholdTil(LocalDate.of(2018, 1, 1).atStartOfDay())
                .type(MIDLERTIDIG)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdService.validate(request));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: oppholdFra må være før oppholdTil"));
    }

    @Test
    void whenTypeIsEmpty_thenThrowExecption() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdService.validate(request));

        assertThat(exception.getMessage(), containsString("Type av opphold må angis"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput_thenThrowExecption() {

        var request = List.of(OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 2).atStartOfDay())
                        .type(OPPLYSNING_MANGLER)
                        .isNew(true)
                        .build(),
                OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende opphold er detektert"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput2_thenThrowExecption() {

        var request = List.of(OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .type(OPPLYSNING_MANGLER)
                        .isNew(true)
                        .build(),
                OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .oppholdTil(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende opphold er detektert"));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        var target = oppholdService.convert(List.of(OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .type(OPPLYSNING_MANGLER)
                .isNew(true)
                .build())).get(0);

        assertThat(target.getOppholdFra(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        var target = oppholdService.convert(List.of(OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 2, 4).atStartOfDay())
                        .type(OPPLYSNING_MANGLER)
                        .isNew(true)
                        .build(),
                OppholdDTO.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build()));

        assertThat(target.get(1).getOppholdTil(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }
}