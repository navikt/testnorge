package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.utils.TilfeldigLandService;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.UtflyttingDTO;
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
class UtflyttingServiceTest {

    @Mock
    private TilfeldigLandService tilfeldigLandService;

    @InjectMocks
    private UtflyttingService utflyttingService;

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var request = List.of(UtflyttingDTO.builder()
                .tilflyttingsland("Mali")
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                utflyttingService.convert((List<UtflyttingDTO>) request));

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland"));
    }

    @Test
    void whenInvalidFlyttedatoSequence_thenThrowExecption() {

        var request = List.of(UtflyttingDTO.builder()
                        .tilflyttingsland("USA")
                        .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                        .isNew(true)
                        .build(),
                UtflyttingDTO.builder()
                        .tilflyttingsland("CAN")
                        .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                utflyttingService.convert(request));

        assertThat(exception.getMessage(), containsString("Ugyldig flyttedato, ny dato må være etter en eksisterende"));
    }

    @Test
    void whenEmptyLandkode_thenProvideCountryFromTilfeldigLandService() {

        when(tilfeldigLandService.getLand()).thenReturn("TGW");

        var target = utflyttingService.convert(List.of(UtflyttingDTO.builder().isNew(true).build())).get(0);

        verify(tilfeldigLandService).getLand();
        assertThat(target.getTilflyttingsland(), is(equalTo("TGW")));
    }
}