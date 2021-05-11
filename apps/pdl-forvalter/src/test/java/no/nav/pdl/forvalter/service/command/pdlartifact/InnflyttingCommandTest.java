package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlInnflytting;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InnflyttingCommandTest {

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                new InnflyttingCommand(List.of(PdlInnflytting.builder()
                        .fraflyttingsland("Finnland")
                        .isNew(true)
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        var target = new InnflyttingCommand(List.of(PdlInnflytting.builder().isNew(true).build())).call().get(0);

        assertThat(target.getFraflyttingsland(), hasLength(3));
    }
}