package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

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

    private static final String DNR_IDENT = "45023412345";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private BostedAdresseService bostedAdresseService;

    @InjectMocks
    private InnflyttingService innflyttingService;

    @Test
    void whenInvalidLandkode_thenThrowExecption() {

        var request = InnflyttingDTO.builder()
                .fraflyttingsland("Finnland")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                innflyttingService.validate(request));

        assertThat(exception.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland"));
    }

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn("IND");

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .innflytting(List.of(InnflyttingDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        var target = innflyttingService.convert(request)
                .getFirst();

        verify(kodeverkConsumer).getTilfeldigLand();

        assertThat(target.getFraflyttingsland(), is(equalTo("IND")));
    }
}