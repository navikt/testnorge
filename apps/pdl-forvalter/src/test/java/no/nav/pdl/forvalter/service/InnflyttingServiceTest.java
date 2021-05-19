package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.dto.RsInnflytting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InnflyttingServiceTest {

    @InjectMocks
    private InnflyttingService innflyttingService;
    
    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                innflyttingService.convert(List.of(RsInnflytting.builder()
                        .fraflyttingsland("Finnland")
                        .isNew(true)
                        .build())));

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland"));
    }

    @Test
    void whenInvalidFlyttedatoSequence_thenThrowExecption() {

        var exception = assertThrows(HttpClientErrorException.class, () ->
                innflyttingService.convert(List.of(RsInnflytting.builder()
                                .fraflyttingsland("AUS")
                                .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                                .isNew(true)
                                .build(),
                        RsInnflytting.builder()
                                .fraflyttingsland("RUS")
                                .flyttedato(LocalDate.of(2015, 12, 31).atStartOfDay())
                                .isNew(true)
                                .build()
                )));

        assertThat(exception.getMessage(), containsString("Ugyldig flyttedato, ny dato må være etter en eksisterende"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        var target = innflyttingService.convert(List.of(RsInnflytting.builder()
                .isNew(true)
                .build()))
                .get(0);

        assertThat(target.getFraflyttingsland(), hasLength(3));
    }
}