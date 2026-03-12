package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        StepVerifier.create(innflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland")));
}

    @Test
    void whenEmptyLandkode_thenProvideRandomCountry() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("IND"));
        when(bostedAdresseService.convert(any(PersonDTO.class), eq(false))).thenReturn(Mono.just(new PersonDTO()));

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .innflytting(List.of(InnflyttingDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        StepVerifier.create(innflyttingService.convert(request))
                .assertNext(target -> {
                    assertThat(target.getIdent(), is(equalTo(DNR_IDENT)));
                    assertThat(target.getInnflytting().getFirst().getFraflyttingsland(), is(equalTo("IND")));
                })
                .verifyComplete();
    }
}