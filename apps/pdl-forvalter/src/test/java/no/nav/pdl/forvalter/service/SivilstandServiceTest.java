package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SKILT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SivilstandServiceTest {

    @InjectMocks
    private SivilstandService sivilstandService;

    @Test
    void whenTypeIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .sivilstand(List.of(SivilstandDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sivilstandService.convert(request));

        assertThat(exception.getMessage(), containsString("Type av sivilstand må oppgis"));
    }

    @Test
    void whenSivilstandDatoIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .sivilstand(List.of(SivilstandDTO.builder()
                        .type(SKILT)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sivilstandService.convert(request));

        assertThat(exception.getMessage(), containsString("Sivilstand: dato for sivilstand må oppgis"));
    }

    @Test
    void whenSivilstandDatoHasInvalidOrdering_thenThrowExecption() {

        var request = PersonDTO.builder()
                .sivilstand(List.of(SivilstandDTO.builder()
                                .type(SKILT)
                                .sivilstandsdato(LocalDateTime.now().minusYears(3))
                                .isNew(true)
                                .build(),
                        SivilstandDTO.builder()
                                .type(GIFT)
                                .sivilstandsdato(LocalDateTime.now())
                                .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sivilstandService.convert(request));

        assertThat(exception.getMessage(), containsString("Sivilstand: overlappende datoer er ikke gyldig"));
    }
}