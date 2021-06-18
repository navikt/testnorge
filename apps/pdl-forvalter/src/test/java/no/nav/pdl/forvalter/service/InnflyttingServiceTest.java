package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.InnflyttingDTO;
import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InnflyttingServiceTest {

    @Mock
    private TilfeldigLandService tilfeldigLandService;

    @InjectMocks
    private InnflyttingService innflyttingService;
    
    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var request = List.of(InnflyttingDTO.builder()
                .fraflyttingsland("Finnland")
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                innflyttingService.convert((List<InnflyttingDTO>) request));

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland"));
    }

    @Test
    void whenInvalidFlyttedatoSequence_thenThrowExecption() {

        var request = List.of(InnflyttingDTO.builder()
                        .fraflyttingsland("AUS")
                        .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                        .isNew(true)
                        .build(),
                InnflyttingDTO.builder()
                        .fraflyttingsland("RUS")
                        .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                innflyttingService.convert(request));

        assertThat(exception.getMessage(), containsString("Ugyldig flyttedato, ny dato må være etter en eksisterende"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        when(tilfeldigLandService.getLand()).thenReturn("IND");

        var target = innflyttingService.convert(List.of(InnflyttingDTO.builder()
                .isNew(true)
                .build()))
                .get(0);

        verify(tilfeldigLandService).getLand();

        assertThat(target.getFraflyttingsland(), is(equalTo("IND")));
    }
}