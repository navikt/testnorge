package no.nav.pdl.forvalter.service;

import lombok.val;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

        val request = UtflyttingDTO.builder()
                .tilflyttingsland("Mali")
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                    Assertions.assertThat(throwable)
                            .isInstanceOf(InvalidRequestException.class)
                            .hasMessageContaining("400 Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland"));
    }

    @Test
    void whenEmptyLandkode_thenProvideCountryFromGeografiskeKodeverkConsumer() {

        val request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .utflytting(List.of(UtflyttingDTO.builder().isNew(true).build()))
                .build();

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("TGW"));
        when(kontaktAdresseService.convert(any(PersonDTO.class), anyBoolean())).thenReturn(Mono.just(request));
        
        StepVerifier.create(utflyttingService.convert(request))
                        .verifyComplete();

        verify(kodeverkConsumer).getTilfeldigLand();
        assertThat(request.getUtflytting().getFirst().getTilflyttingsland(), is(equalTo("TGW")));
    }
}