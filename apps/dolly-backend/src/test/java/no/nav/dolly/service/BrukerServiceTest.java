package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.DokumentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private static final Long ID = 1L;
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

    @Mock
    private DokumentRepository dokumentRepository;

    @InjectMocks
    private BrukerService brukerService;

    @Test
    void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerOrTeamBrukerErFunnet() {

        var bruker = Bruker.builder().id(1L).build();

        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(bruker));

        StepVerifier.create(brukerService.fetchBruker("test"))
                .assertNext(bruker1 ->
                        assertThat(bruker1, is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void fetchBruker_kasterExceptionHvisIngenBrukerOrTeamBrukerFunnet() {

        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.empty());
        StepVerifier.create(brukerService.fetchBruker(BRUKERID))
                .verifyError(NotFoundException.class);
    }

    @Test
    void fetchBrukere() {

        var bruker = Bruker.builder().id(ID).brukerId(BRUKERID).brukertype(Bruker.Brukertype.AZURE).build();
        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(bruker));
        when(brukerRepository.findByBrukerId(any())).thenReturn(Mono.just(bruker));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));

        StepVerifier.create(brukerService.fetchBrukerWithoutTeam())
                .assertNext(bruker1 -> {
                    assertThat(bruker1, is(notNullValue()));
                    assertThat(bruker1.getBrukertype(), is(Bruker.Brukertype.AZURE));
                })
                .verifyComplete();
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
                    verify(brukerRepository).save(any());
                })
                .verifyComplete();
    }

    @Test
    void leggTilFavoritter_medGrupperIDer() {

        var testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        var bruker = Bruker.builder().id(ID).brukerId(BRUKERID).build();

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(BRUKERID)).thenReturn(Mono.just(bruker));
        when(testgruppeRepository.findById(ID)).thenReturn(Mono.just(testgruppe));
        when(brukerFavoritterRepository.save(any())).thenReturn(Mono.just(BrukerFavoritter.builder().gruppeId(ID).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));

        StepVerifier.create(brukerService.leggTilFavoritt(ID))
                .assertNext(hentetBruker -> {
                    assertThat(hentetBruker, is(notNullValue()));
                    assertThat(hentetBruker.getBrukerId(), is(BRUKERID));
                    verify(brukerFavoritterRepository).save(any());
                })
                .verifyComplete();
    }

    @Test
    void fjernFavoritter_medGrupperIDer() {

        var bruker = Bruker.builder().id(ID).brukerId(BRUKERID).build();
        var testgruppe = Testgruppe.builder().id(ID).navn("gruppe").hensikt("hen").build();

        when(getAuthenticatedUserId.call()).thenReturn(Mono.just(BRUKERID));
        when(brukerRepository.findByBrukerId(BRUKERID)).thenReturn(Mono.just(bruker));
        when(brukerFavoritterRepository.deleteByBrukerIdAndGruppeId(ID, ID)).thenReturn(Mono.empty());
        when(testgruppeRepository.findById(ID)).thenReturn(Mono.just(testgruppe));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));

        StepVerifier.create(brukerService.fjernFavoritt(ID))
                .assertNext(hentetBruker -> {
                    assertThat(hentetBruker, is(notNullValue()));
                    assertThat(hentetBruker.getBrukerId(), is(BRUKERID));
                    verify(brukerFavoritterRepository).deleteByBrukerIdAndGruppeId(ID, ID);
                })
                .verifyComplete();
    }
}