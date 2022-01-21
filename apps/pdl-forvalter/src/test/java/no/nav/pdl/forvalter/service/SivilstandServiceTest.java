package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SKILT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SivilstandServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SivilstandService sivilstandService;

    @Test
    void whenTypeIsMissing_thenThrowExecption() {

        var request = SivilstandDTO.builder()
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sivilstandService.validate(request));

        assertThat(exception.getMessage(), containsString("Type av sivilstand må oppgis"));
    }

    @Test
    void whenRelatertPersonDontExist_thenThrowExecption() {

        var request = SivilstandDTO.builder()
                        .type(GIFT)
                        .sivilstandsdato(LocalDateTime.now())
                        .relatertVedSivilstand(IDENT)
                        .isNew(true)
                        .build();

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sivilstandService.validate(request));

        assertThat(exception.getMessage(), containsString("Sivilstand: Relatert person finnes ikke"));
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