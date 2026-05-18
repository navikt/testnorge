package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NavnServiceTest {

    private static final String IDENT = "12305678901";

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @InjectMocks
    private NavnService navnService;

    @Test
    @SuppressWarnings("java:S5778")
    void whenNameDoesNotVerify_thenThrowExecption() {

        var request = NavnDTO.builder()
                .fornavn("Ugyldig")
                .mellomnavn("Sjanglende")
                .etternavn("Sjømann")
                .isNew(true)
                .build();

        when(genererNavnServiceConsumer.verifyNavn(any(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.class))).thenReturn(Mono.just(false));

        StepVerifier.create(navnService.validate(request, new PersonDTO()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Navn er ikke i liste over gyldige verdier")));
    }

    @Test
    void whenNameVerifies_acceptProposal() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .fornavn("Gyldig")
                        .mellomnavn("Sjanglende")
                        .etternavn("Sjømann")
                        .isNew(true)
                        .build()))
                .build())
                .build();

        StepVerifier.create(navnService.convert(request))
                .assertNext(target -> {
                    assertThat(target.getPerson().getNavn().getFirst().getFornavn(), is(equalTo("Gyldig")));
                    assertThat(target.getPerson().getNavn().getFirst().getMellomnavn(), is(equalTo("Sjanglende")));
                    assertThat(target.getPerson().getNavn().getFirst().getEtternavn(), is(equalTo("Sjømann")));
                })
                .verifyComplete();

    }

    @Test
    void whenNamesNotProvidedWithoutMellomnavn_provideNames() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .isNew(true)
                        .build()))
                .build())
                .build();

        when(genererNavnServiceConsumer.getNavn()).thenReturn(Mono.just(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        StepVerifier.create(navnService.convert(request))
                .assertNext(target -> {

                    assertThat(target.getPerson().getNavn().getFirst().getFornavn(), is(equalTo("Full")));
                    assertThat(target.getPerson().getNavn().getFirst().getMellomnavn(), nullValue());
                    assertThat(target.getPerson().getNavn().getFirst().getEtternavn(), is(equalTo("Sjømann")));
                })
                .verifyComplete();
    }

    @Test
    void whenNamesNotProvidedWithMellomnavn_provideNames() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .hasMellomnavn(true)
                        .isNew(true)
                        .build()))
                .build())
                .build();

        when(genererNavnServiceConsumer.getNavn()).thenReturn(Mono.just(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        StepVerifier.create(navnService.convert(request))
                .assertNext(target -> {

                    assertThat(target.getPerson().getNavn().getFirst().getFornavn(), is(equalTo("Full")));
                    assertThat(target.getPerson().getNavn().getFirst().getMellomnavn(), is(equalTo("Sjanglende")));
                    assertThat(target.getPerson().getNavn().getFirst().getEtternavn(), is(equalTo("Sjømann")));
                })
                .verifyComplete();
    }
}