package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.consumers.BrukerServiceConsumer;
import no.nav.testnav.apps.tenorsearchservice.consumers.dto.BrukereDTO;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.tenorsearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.apps.tenorsearchservice.repository.TenorPersonMalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenorMalAccessServiceTest {

    @Mock
    private TenorPersonMalRepository malRepository;

    @Mock
    private BrukerServiceConsumer brukerServiceConsumer;

    private TenorMalAccessService accessService;

    @BeforeEach
    void setUp() {
        accessService = new TenorMalAccessService(malRepository, brukerServiceConsumer);
    }

    @Test
    void shouldGetAzureUsersWithoutCallingBrukerService() {
        var currentUser = bruker("azure-1", TenorMalBrukerType.AZURE);
        var otherUser = bruker("azure-2", TenorMalBrukerType.AZURE);
        var otherUserMal = mal(otherUser);
        when(malRepository.findByBrukertype(TenorMalBrukerType.AZURE))
                .thenReturn(Flux.just(otherUserMal));

        StepVerifier.create(accessService.getAccessibleMaler(currentUser))
                .expectNext(otherUserMal)
                .verifyComplete();

        verify(brukerServiceConsumer, never()).getKollegaerIOrganisasjon(currentUser.brukerId());
    }

    @Test
    void shouldLimitBankIdUsersToOrganizationColleagues() {
        var currentUser = bruker("hashed-1", TenorMalBrukerType.BANKID);
        var colleague = bruker("hashed-2", TenorMalBrukerType.BANKID);
        var colleagueMal = mal(colleague);
        when(brukerServiceConsumer.getKollegaerIOrganisasjon(currentUser.brukerId()))
                .thenReturn(Mono.just(BrukereDTO.builder()
                        .brukere(List.of(colleague.brukerId()))
                        .build()));
        when(malRepository.findByBrukertypeAndBrukerIdIn(
                org.mockito.ArgumentMatchers.eq(TenorMalBrukerType.BANKID),
                argThat(ids -> ids.contains(currentUser.brukerId()) && ids.contains(colleague.brukerId()))))
                .thenReturn(Flux.just(colleagueMal));

        StepVerifier.create(accessService.getAccessibleMaler(currentUser))
                .expectNext(colleagueMal)
                .verifyComplete();
    }

    @Test
    void shouldPropagateBrukerServiceFailure() {
        var currentUser = bruker("hashed-1", TenorMalBrukerType.BANKID);
        when(brukerServiceConsumer.getKollegaerIOrganisasjon(currentUser.brukerId()))
                .thenReturn(Mono.error(new BrukerServiceUnavailableException("Utilgjengelig", new RuntimeException())));

        StepVerifier.create(accessService.getAccessibleMaler(currentUser))
                .expectError(BrukerServiceUnavailableException.class)
                .verify();

        verify(malRepository, never()).findByBrukertypeAndBrukerIdIn(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    private static TenorMalOwner bruker(String brukerId, TenorMalBrukerType brukerType) {
        return new TenorMalOwner(brukerId, brukerId, brukerType);
    }

    private static TenorPersonMal mal(TenorMalOwner owner) {
        return TenorPersonMal.builder()
                .id((long) owner.brukerId().hashCode())
                .malNavn("Mal")
                .soekKriterier("{}")
                .brukerId(owner.brukerId())
                .brukernavn(owner.brukernavn())
                .brukertype(owner.brukertype())
                .build();
    }
}
