package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        when(genererNavnServiceConsumer.verifyNavn(any(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.class))).thenReturn(false);

        var exception = assertThrows(HttpClientErrorException.class, () ->
                navnService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString("Navn er ikke i liste over gyldige verdier"));
    }

    @Test
    void whenNameVerifies_acceptProposal() {

        var request = PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .fornavn("Gyldig")
                        .mellomnavn("Sjanglende")
                        .etternavn("Sjømann")
                        .isNew(true)
                        .build()))
                .build();

        var target = navnService.convert(request).getFirst();

        assertThat(target.getFornavn(), is(equalTo("Gyldig")));
        assertThat(target.getMellomnavn(), is(equalTo("Sjanglende")));
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }

    @Test
    void whenNamesNotProvidedWithoutMellomnavn_provideNames() {

        var request = PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        when(genererNavnServiceConsumer.getNavn(1)).thenReturn(Optional.of(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        var target = navnService.convert(request).getFirst();

        assertThat(target.getFornavn(), is(equalTo("Full")));
        assertThat(target.getMellomnavn(), nullValue());
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }

    @Test
    void whenNamesNotProvidedWithMellomnavn_provideNames() {

        var request = PersonDTO.builder()
                .ident(IDENT)
                .navn(List.of(NavnDTO.builder()
                        .hasMellomnavn(true)
                        .isNew(true)
                        .build()))
                .build();

        when(genererNavnServiceConsumer.getNavn(1)).thenReturn(Optional.of(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        var target = navnService.convert(request).getFirst();

        assertThat(target.getFornavn(), is(equalTo("Full")));
        assertThat(target.getMellomnavn(), is(equalTo("Sjanglende")));
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }
}