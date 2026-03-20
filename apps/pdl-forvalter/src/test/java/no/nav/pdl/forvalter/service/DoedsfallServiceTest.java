package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class DoedsfallServiceTest {

    @InjectMocks
    private DoedsfallService doedsfallService;

    @Test
    void whenDoedsdatoIsMissing_thenThrowExecption() {

        var request = DoedsfallDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(doedsfallService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Dødsfall: dødsdato må oppgis")));
    }
}