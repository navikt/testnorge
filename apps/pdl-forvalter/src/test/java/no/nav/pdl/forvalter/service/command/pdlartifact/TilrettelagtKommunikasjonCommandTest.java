package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.dto.RsTilrettelagtKommunikasjon;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TilrettelagtKommunikasjonCommandTest {

    @Test
    void whenNoSpraakGiven_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                new TilrettelagtKommunikasjonCommand(List.of(RsTilrettelagtKommunikasjon.builder()
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Enten språk for taletolk og/eller tegnspråktolk må oppgis"));
    }

    @Test
    void whenInvalidSpraakTaletolkGiven_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                new TilrettelagtKommunikasjonCommand(List.of(RsTilrettelagtKommunikasjon.builder()
                        .spraakForTaletolk("svensk")
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Språk for taletolk er ugyldig: forventet 2 tegn i hht kodeverk Språk"));
    }

    @Test
    void whenInvalidSpraakTegnspraakTolkGiven_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                new TilrettelagtKommunikasjonCommand(List.of(RsTilrettelagtKommunikasjon.builder()
                        .spraakForTegnspraakTolk("kyrgisistansk")
                        .build())).call());

        assertThat(exception.getMessage(), containsString("Språk for tegnspråktolk er ugyldig: forventet 2 tegn i hht kodeverk Språk"));
    }
}