package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlUtflytting;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtflyttingCommandTest {

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                new UtflyttingCommand(List.of(PdlUtflytting.builder()
                        .tilflyttingsland("Mali")
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        var target = new UtflyttingCommand(List.of(PdlUtflytting.builder().isNew(true).build())).call().get(0);

        assertThat(target.getTilflyttingsland(), hasLength(3));
    }
}