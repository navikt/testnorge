package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerServiceTest {

    private static final String BRUKERID = "123";
    private static final String BRUKERID2 = "1234";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private GetAuthenticatedUserId getAuthenticatedUserId;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private UserInfoExtended userInfo;

    @Mock
    private BrukerFavoritterRepository brukerFavoritterRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @InjectMocks
    private BrukerService brukerService;

    @Test
    void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerOrTeamBrukerErFunnet() {

        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(Bruker.builder().build()));
        StepVerifier.create(brukerService.fetchBrukerOrTeamBruker("test"))
                .assertNext(bruker ->
                        assertThat(bruker, is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void fetchBruker_kasterExceptionHvisIngenBrukerOrTeamBrukerFunnet() {

        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.empty());
        StepVerifier.create(brukerService.fetchBrukerOrTeamBruker(BRUKERID))
                .verifyError(NotFoundException.class);
    }

    @Test
    void fetchBrukere() {

        var bruker = Bruker.builder().brukerId(BRUKERID).brukertype(Bruker.Brukertype.AZURE).build();
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(bruker));
        when(brukerRepository.findByOrderById()).thenReturn(Flux.just(bruker));
        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(bruker));
        when(brukerRepository.findByOrderById()).thenReturn(Flux.just(bruker));

        StepVerifier.create(brukerService.fetchBrukere())
                .assertNext(bruker1 -> {
                    assertThat(bruker1, is(notNullValue()));
                    assertThat(bruker1.getBrukertype(), is(Bruker.Brukertype.AZURE));
                })
                .verifyComplete();
        verify(brukerRepository).findByOrderById();
    }

    @Test
    void fetchOrCreateBruker_saveKallesVedNotFoundException() {

        when(brukerRepository.findByBrukerId(BRUKERID2)).thenReturn(Mono.empty());
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));
        when(brukerRepository.save(any())).thenReturn(Mono.just(Bruker.builder().brukerId(BRUKERID2).build()));

        StepVerifier.create(brukerService.fetchOrCreateBruker(BRUKERID2))
                .assertNext(bruker1 -> {
                    assertThat(bruker1, is(notNullValue()));
                    assertThat(bruker1.getBrukerId(), is(BRUKERID2));
                })
                .verifyComplete();
        verify(brukerRepository).save(any());
    }

    @Test
    void leggTilFavoritter_medGrupperIDer() {

        var id = 1L;
        var testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        var bruker = Bruker.builder().brukerId(BRUKERID).build();

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(BRUKERID)).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.findById(id)).thenReturn(Mono.just(testgruppe));
        when(brukerFavoritterRepository.save(any())).thenReturn(Mono.just(BrukerFavoritter.builder().gruppeId(id).build()));

        StepVerifier.create(brukerService.leggTilFavoritt(id))
                .assertNext(hentetBruker -> {
                    assertThat(hentetBruker, is(notNullValue()));
                    assertThat(hentetBruker.getBrukerId(), is(BRUKERID));
                })
                .verifyComplete();

        verify(brukerFavoritterRepository).save(any());
    }

    @Test
    void fjernFavoritter_medGrupperIDer() {

        var id = 1L;
        var bruker = Bruker.builder().id(id).brukerId(BRUKERID).build();
        var testgruppe = Testgruppe.builder().id(id).navn("gruppe").hensikt("hen").build();

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(BRUKERID)).thenReturn(Mono.just(bruker));
        when(brukerFavoritterRepository.deleteByBrukerIdAndGruppeId(id, id)).thenReturn(Mono.empty());
        when(testgruppeRepository.findById(id)).thenReturn(Mono.just(testgruppe));

        StepVerifier.create(brukerService.fjernFavoritt(id))
                .assertNext(hentetBruker -> {
                    assertThat(hentetBruker, is(notNullValue()));
                    assertThat(hentetBruker.getBrukerId(), is(BRUKERID));
                })
                .verifyComplete();

        verify(brukerFavoritterRepository).deleteByBrukerIdAndGruppeId(id, id);
    }
}