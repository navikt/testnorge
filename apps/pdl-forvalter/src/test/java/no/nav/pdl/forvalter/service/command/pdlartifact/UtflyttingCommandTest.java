package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.dto.RsUtflytting;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtflyttingCommandTest {

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new UtflyttingCommand(List.of(RsUtflytting.builder()
                        .tilflyttingsland("Mali")
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland"));
    }


    @Test
    void whenInvalidFlyttedatoSequence_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                new UtflyttingCommand(List.of(RsUtflytting.builder()
                                .tilflyttingsland("USA")
                                .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                                .isNew(true)
                                .build(),
                        RsUtflytting.builder()
                                .tilflyttingsland("CAN")
                                .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                                .isNew(true)
                                .build()
                ))
                        .call());

        assertThat(exception.getMessage(), containsString("Ugyldig flyttedato, ny dato må være etter en eksisterende"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        var target = new UtflyttingCommand(List.of(RsUtflytting.builder().isNew(true).build())).call().get(0);

        assertThat(target.getTilflyttingsland(), hasLength(3));
    }
}