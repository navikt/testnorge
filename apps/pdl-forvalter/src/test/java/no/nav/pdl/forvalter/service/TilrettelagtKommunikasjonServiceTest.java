package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.TilrettelagtKommunikasjonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class TilrettelagtKommunikasjonServiceTest {

    @InjectMocks
    private TilrettelagtKommunikasjonService tilrettelagtKommunikasjonService;

    @Test
    void whenNoSpraakGiven_thenThrowExecption() {

        var request = TilrettelagtKommunikasjonDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(
                        tilrettelagtKommunikasjonService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Språk for taletolk og/eller tegnspråktolk må oppgis")));
    }

    @Test
    void whenInvalidSpraakTaletolkGiven_thenThrowExecption() {

        var request = TilrettelagtKommunikasjonDTO.builder()
                .spraakForTaletolk("svensk")
                .isNew(true)
                .build();

        StepVerifier.create(
                        tilrettelagtKommunikasjonService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Språk for taletolk er ugyldig: forventet 2 tegn i hht kodeverk Språk")));
    }

    @Test
    void whenInvalidSpraakTegnspraakTolkGiven_thenThrowExecption() {

        var request = TilrettelagtKommunikasjonDTO.builder()
                .spraakForTegnspraakTolk("kyrgisistansk")
                .isNew(true)
                .build();

        StepVerifier.create(
                        tilrettelagtKommunikasjonService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Språk for tegnspråktolk er ugyldig: forventet 2 tegn i hht kodeverk Språk")));
    }
}