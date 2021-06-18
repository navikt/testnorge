package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.domain.NavnDTO;
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

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @InjectMocks
    private NavnService navnService;

    @Test
    void whenNameDoesNotVerify_thenThrowExecption() {

        var request = List.of(NavnDTO.builder()
                .fornavn("Ugyldig")
                .mellomnavn("Sjanglende")
                .etternavn("Sjømann")
                .isNew(true)
                .build());

        when(genererNavnServiceConsumer.verifyNavn(any(no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO.class))).thenReturn(false);

        var exception = assertThrows(HttpClientErrorException.class, () ->
                navnService.convert((List<NavnDTO>) request));

        assertThat(exception.getMessage(), containsString("Navn er ikke i liste over gyldige verdier"));
    }

    @Test
    void whenNameVerifies_acceptProposal() {

        var request = List.of(NavnDTO.builder()
                .fornavn("Gyldig")
                .mellomnavn("Sjanglende")
                .etternavn("Sjømann")
                .isNew(true)
                .build());

        when(genererNavnServiceConsumer.verifyNavn(any(no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO.class))).thenReturn(true);

        var target = navnService.convert((List<NavnDTO>) request).get(0);

        assertThat(target.getFornavn(), is(equalTo("Gyldig")));
        assertThat(target.getMellomnavn(), is(equalTo("Sjanglende")));
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }

    @Test
    void whenNamesNotProvidedWithoutMellomnavn_provideNames() {

        var request = List.of(NavnDTO.builder()
                .isNew(true)
                .build());

        when(genererNavnServiceConsumer.getNavn(1)).thenReturn(Optional.of(no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        var target = navnService.convert((List<NavnDTO>) request).get(0);

        assertThat(target.getFornavn(), is(equalTo("Full")));
        assertThat(target.getMellomnavn(), nullValue());
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }

    @Test
    void whenNamesNotProvidedWithMellomnavn_provideNames() {

        var request = List.of(NavnDTO.builder()
                .hasMellomnavn(true)
                .isNew(true)
                .build());

        when(genererNavnServiceConsumer.getNavn(1)).thenReturn(Optional.of(no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO.builder()
                .adjektiv("Full")
                .adverb("Sjanglende")
                .substantiv("Sjømann")
                .build()));

        var target = navnService.convert((List<NavnDTO>) request).get(0);

        assertThat(target.getFornavn(), is(equalTo("Full")));
        assertThat(target.getMellomnavn(), is(equalTo("Sjanglende")));
        assertThat(target.getEtternavn(), is(equalTo("Sjømann")));
    }
}