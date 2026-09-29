package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.consumers.BrukerServiceConsumer;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.BrukereDTO;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.apps.templatesearchservice.repository.TenorPersonMalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

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

    @ParameterizedTest
    @EnumSource(value = TenorMalBrukerType.class, names = {"AZURE", "TEAM"})
    void shouldGetAllAzureAndTeamTemplatesWithoutCallingBrukerService(TenorMalBrukerType brukertype) {
        var currentUser = bruker("current-owner", brukertype);
        var otherUser = bruker("azure-2", TenorMalBrukerType.AZURE);
        var otherUserMal = mal(otherUser);
        var otherTeamMal = mal(bruker("team-bruker-id-42", TenorMalBrukerType.TEAM));
        when(malRepository.findByBrukertypeIn(Set.of(TenorMalBrukerType.AZURE, TenorMalBrukerType.TEAM)))
                .thenReturn(Flux.just(otherUserMal, otherTeamMal));

        StepVerifier.create(accessService.getAccessibleMaler(currentUser))
                .expectNext(otherUserMal, otherTeamMal)
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
