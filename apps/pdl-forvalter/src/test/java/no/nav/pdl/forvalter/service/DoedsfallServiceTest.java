package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DoedsfallServiceTest {

    @InjectMocks
    private DoedsfallService doedsfallService;

    @Test
    void whenDoedsdatoIsMissing_thenThrowExecption() {

        var request = DoedsfallDTO.builder()
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                doedsfallService.validate(request));

        assertThat(exception.getMessage(), containsString("Dødsfall: dødsdato må oppgis"));
    }
}