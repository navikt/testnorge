package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

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

    private static final String FNR_IDENT = "03012312345";
    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KontaktAdresseService kontaktAdresseService;

    @InjectMocks
    private UtflyttingService utflyttingService;

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var request = UtflyttingDTO.builder()
                .tilflyttingsland("Mali")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                utflyttingService.validate(request));

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland"));
    }

    @Test
    void whenEmptyLandkode_thenProvideCountryFromGeografiskeKodeverkConsumer() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("TGW"));

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .utflytting(List.of(UtflyttingDTO.builder().isNew(true).build()))
                .build();

        var target = utflyttingService.convert(request).getFirst();

        verify(kodeverkConsumer).getTilfeldigLand();
        assertThat(target.getTilflyttingsland(), is(equalTo("TGW")));
    }
}