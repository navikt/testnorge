package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlOpphold;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlOpphold.OppholdType.MIDLERTIDIG;
import static no.nav.pdl.forvalter.domain.PdlOpphold.OppholdType.OPPLYSNING_MANGLER;
import static no.nav.pdl.forvalter.domain.PdlOpphold.OppholdType.PERMANENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OppholdCommandTest {

    @Test
    void whenGyldigFraIsMissing_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                        .type(PERMANENT)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Opphold med oppholdFra må angis"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .oppholdTil(LocalDate.of(2018, 1, 1).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: oppholdFra må være før oppholdTil"));
    }

    @Test
    void whenDatesAreEqual_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .oppholdTil(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: oppholdTil må være etter oppholdFra"));
    }

    @Test
    void whenTypeIsEmpty_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Type av opphold må angis"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                                .oppholdFra(LocalDate.of(2020, 1, 2).atStartOfDay())
                                .type(OPPLYSNING_MANGLER)
                                .isNew(true)
                                .build(),
                        PdlOpphold.builder()
                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .type(MIDLERTIDIG)
                                .isNew(true)
                                .build()))
                        .call());

        assertThat(exception.getMessage(), containsString("Feil: Overlappende opphold er detektert"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput2_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new OppholdCommand(List.of(PdlOpphold.builder()
                                .oppholdFra(LocalDate.of(2020, 2, 3).atStartOfDay())
                                .type(OPPLYSNING_MANGLER)
                                .isNew(true)
                                .build(),
                        PdlOpphold.builder()
                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .oppholdTil(LocalDate.of(2020, 2, 3).atStartOfDay())
                                .type(MIDLERTIDIG)
                                .isNew(true)
                                .build()))
                        .call());

        assertThat(exception.getMessage(), containsString("Feil: Overlappende opphold er detektert"));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        var target = new OppholdCommand(List.of(PdlOpphold.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .type(OPPLYSNING_MANGLER)
                .isNew(true)
                .build())).call().get(0);

        assertThat(target.getOppholdFra(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        var target = new OppholdCommand(List.of(PdlOpphold.builder()
                        .oppholdFra(LocalDate.of(2020, 2, 4).atStartOfDay())
                        .type(OPPLYSNING_MANGLER)
                        .isNew(true)
                        .build(),
                PdlOpphold.builder()
                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .type(MIDLERTIDIG)
                        .isNew(true)
                        .build())).call();

        assertThat(target.get(1).getOppholdTil(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }
}