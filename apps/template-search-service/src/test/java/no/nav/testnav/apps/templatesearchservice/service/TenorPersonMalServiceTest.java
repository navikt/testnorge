package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.consumers.DollyBackendConsumer;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.DollyTeamDTO;
import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalBrukerResponse;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMalResponse;
import no.nav.testnav.apps.templatesearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.apps.templatesearchservice.exception.DollyBackendUnavailableException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalConflictException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.templatesearchservice.repository.TenorPersonMalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenorPersonMalServiceTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T10:00:00Z");
    private static final TenorMalOwner TEAM_OWNER = new TenorMalOwner(
            "team-bruker-id-42", "team-bruker-id-42", TenorMalBrukerType.TEAM);

    @Mock
    private CurrentTenorUserService currentUserService;

    @Mock
    private TenorMalAccessService accessService;

    @Mock
    private DollyBackendConsumer dollyBackendConsumer;

    @Mock
    private TenorPersonMalRepository malRepository;

    private JsonMapper jsonMapper;
    private TenorPersonMalService malService;
    private TenorMalOwner currentUser;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder().build();
        malService = new TenorPersonMalService(
                currentUserService,
                accessService,
                dollyBackendConsumer,
                new TenorPersonMalValidationService(jsonMapper),
                malRepository,
                jsonMapper);
        currentUser = new TenorMalOwner(
                "azure-id",
                "Testbruker",
                TenorMalBrukerType.AZURE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Min mal", "Min mal, med tegn!"})
    void shouldCreateTemplateWithAuthenticatedOwner(String malNavn) {
        var request = new OpprettTenorPersonMalRequest(
                "  " + malNavn + "  ",
                jsonMapper.readTree("{}"));
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByBrukerIdAndMalNavnIgnoreCase("azure-id", malNavn))
                .thenReturn(Mono.empty());
        when(malRepository.save(any())).thenAnswer(invocation -> {
            var mal = invocation.getArgument(0, TenorPersonMal.class);
            mal.setId(42L);
            return Mono.just(mal);
        });

        StepVerifier.create(malService.save(request))
                .assertNext(result -> {
                    assertThat(result.opprettet()).isTrue();
                    assertThat(result.mal().id()).isEqualTo(42L);
                    assertThat(result.mal().malNavn()).isEqualTo(malNavn);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateTemplateWithTeamOwner() {
        var teamOwner = new TenorMalOwner(
                "team-bruker-id-42",
                "team-bruker-id-42",
                TenorMalBrukerType.TEAM);
        var request = new OpprettTenorPersonMalRequest(
                "Team-mal",
                jsonMapper.readTree("{}"));
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(teamOwner));
        when(malRepository.findByBrukerIdAndMalNavnIgnoreCase("team-bruker-id-42", "Team-mal"))
                .thenReturn(Mono.empty());
        when(malRepository.save(argThat(mal ->
                mal.getBrukerId().equals("team-bruker-id-42") &&
                        mal.getBrukernavn().equals("team-bruker-id-42") &&
                        mal.getBrukertype() == TenorMalBrukerType.TEAM)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(malService.save(request))
                .assertNext(result -> {
                    assertThat(result.mal().malNavn()).isEqualTo("Team-mal");
                    assertThat(result.opprettet()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void shouldGetTemplatesForSelectedAccessibleUser() {
        var otherUser = new TenorMalOwner(
                "other-azure-id",
                "Annen bruker",
                TenorMalBrukerType.AZURE);
        var currentUserMal = template(42L, currentUser);
        var otherUserMal = template(43L, otherUser);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(currentUserMal, otherUserMal));

        StepVerifier.create(malService.getMaler("other-azure-id"))
                .assertNext(mal -> assertThat(mal.id()).isEqualTo(43L))
                .verifyComplete();
    }

    @ParameterizedTest
    @EnumSource(value = TenorMalBrukerType.class, names = {"AZURE", "TEAM"})
    void shouldReturnAllAzureAndTeamTemplatesForAlle(TenorMalBrukerType brukertype) {
        var requester = brukertype == TenorMalBrukerType.AZURE ? currentUser : TEAM_OWNER;
        var otherUser = new TenorMalOwner(
                "other-azure-id",
                "Annen bruker",
                TenorMalBrukerType.AZURE);
        var otherTeam = new TenorMalOwner(
                "team-bruker-id-81", "team-bruker-id-81", TenorMalBrukerType.TEAM);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(requester));
        when(accessService.getAccessibleMaler(requester))
                .thenReturn(Flux.just(
                        template(42L, currentUser),
                        template(43L, otherUser),
                        template(44L, otherTeam)));

        StepVerifier.create(malService.getMaler("ALLE").collectList())
                .assertNext(maler -> assertThat(maler)
                        .extracting(TenorPersonMalResponse::id)
                        .containsExactly(42L, 43L, 44L))
                .verifyComplete();

        verify(currentUserService, never()).getCurrentUser();
        verifyNoInteractions(dollyBackendConsumer);
    }

    @Test
    void shouldFilterTemplatesBySelectedTeam() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(template(42L, currentUser), template(43L, TEAM_OWNER)));

        StepVerifier.create(malService.getMaler(TEAM_OWNER.brukerId()))
                .assertNext(mal -> assertThat(mal.id()).isEqualTo(43L))
                .verifyComplete();

        verifyNoInteractions(dollyBackendConsumer);
    }

    @Test
    void shouldNotReturnUnavailableBankIdOwnerToAzureUser() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(template(42L, currentUser), template(43L, TEAM_OWNER)));

        StepVerifier.create(malService.getMaler("hashed-bankid-id"))
                .verifyComplete();
    }

    @Test
    void shouldReturnDollyStyleUserOverview() {
        var otherUser = new TenorMalOwner(
                "other-azure-id",
                "Annen bruker",
                TenorMalBrukerType.AZURE);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(
                        template(42L, currentUser),
                        template(43L, otherUser)));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler())
                        .extracting("brukerId")
                        .containsExactly("ALLE", "other-azure-id", "azure-id"))
                .verifyComplete();

        verifyNoInteractions(dollyBackendConsumer);
    }

    @Test
    void shouldRefreshTeamNamesOnEachOverviewWithoutPersistingThem() {
        var teamMal = template(43L, TEAM_OWNER);
        var anotherTeamMal = template(44L, TEAM_OWNER);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(template(42L, currentUser), teamMal, anotherTeamMal));
        when(dollyBackendConsumer.getTeams())
                .thenReturn(Mono.just(List.of(
                        new DollyTeamDTO(TEAM_OWNER.brukerId(), "Alfa"),
                        new DollyTeamDTO("team-bruker-id-99", "Team uten maler"))))
                .thenReturn(Mono.just(List.of(new DollyTeamDTO(TEAM_OWNER.brukerId(), "Zulu"))));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Alfa"),
                        new TenorPersonMalBrukerResponse("azure-id", "Testbruker")))
                .verifyComplete();
        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse("azure-id", "Testbruker"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Zulu")))
                .verifyComplete();

        assertThat(teamMal.getBrukernavn()).isEqualTo(TEAM_OWNER.brukerId());
        assertThat(anotherTeamMal.getBrukernavn()).isEqualTo(TEAM_OWNER.brukerId());
        verify(dollyBackendConsumer, times(2)).getTeams();
        verify(currentUserService, never()).getCurrentUser();
        verifyNoInteractions(malRepository);
    }

    @Test
    void shouldIncludeAlleInTeamUserOverview() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(TEAM_OWNER));
        when(accessService.getAccessibleMaler(TEAM_OWNER))
                .thenReturn(Flux.just(template(42L, currentUser), template(43L, TEAM_OWNER)));
        when(dollyBackendConsumer.getTeams()).thenReturn(
                Mono.just(List.of(new DollyTeamDTO(TEAM_OWNER.brukerId(), "Alfa"))));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Alfa"),
                        new TenorPersonMalBrukerResponse("azure-id", "Testbruker")))
                .verifyComplete();
    }

    @Test
    void shouldKeepTemplatesVisibleWhenTeamCatalogFails() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(template(42L, currentUser), template(43L, TEAM_OWNER)));
        when(dollyBackendConsumer.getTeams()).thenReturn(Mono.error(
                new DollyBackendUnavailableException("Utilgjengelig", new IllegalStateException())));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse("azure-id", "Testbruker"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Ukjent team")))
                .verifyComplete();

        verify(currentUserService, never()).getCurrentUser();
        verifyNoInteractions(malRepository);
    }

    @Test
    void shouldKeepDeletedTeamVisibleByItsStableOwnerId() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser)).thenReturn(Flux.just(template(43L, TEAM_OWNER)));
        when(dollyBackendConsumer.getTeams()).thenReturn(Mono.just(List.of()));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Ukjent team")))
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "Team 41010100044"})
    void shouldUseUnknownTeamForMissingOrUnsafeTeamName(String teamNavn) {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser)).thenReturn(Flux.just(template(43L, TEAM_OWNER)));
        when(dollyBackendConsumer.getTeams()).thenReturn(
                Mono.just(List.of(new DollyTeamDTO(TEAM_OWNER.brukerId(), teamNavn))));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler()).containsExactly(
                        new TenorPersonMalBrukerResponse("ALLE", "ALLE"),
                        new TenorPersonMalBrukerResponse(TEAM_OWNER.brukerId(), "Ukjent team")))
                .verifyComplete();
    }

    @Test
    void shouldNotExposeAlleForBankIdUser() {
        var bankIdUser = new TenorMalOwner(
                "hashed-id",
                "BankID-bruker",
                TenorMalBrukerType.BANKID);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(bankIdUser));
        when(accessService.getAccessibleMaler(bankIdUser))
                .thenReturn(Flux.just(template(42L, bankIdUser)));

        StepVerifier.create(malService.getMaler("ALLE"))
                .verifyComplete();
        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler())
                        .extracting("brukerId")
                        .containsExactly("hashed-id"))
                .verifyComplete();

        verifyNoInteractions(dollyBackendConsumer);
    }

    @Test
    void shouldPropagateBankIdOrganizationFailureWithoutTeamFallback() {
        var bankIdUser = new TenorMalOwner("hashed-id", "BankID-bruker", TenorMalBrukerType.BANKID);
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(bankIdUser));
        when(accessService.getAccessibleMaler(bankIdUser)).thenReturn(Flux.error(
                new BrukerServiceUnavailableException("Utilgjengelig", new IllegalStateException())));

        StepVerifier.create(malService.getMalOversikt())
                .expectError(BrukerServiceUnavailableException.class)
                .verify();
        StepVerifier.create(malService.getMaler(bankIdUser.brukerId()))
                .expectError(BrukerServiceUnavailableException.class)
                .verify();

        verifyNoInteractions(dollyBackendConsumer);
    }

    @Test
    void shouldReturnOnlyAlleWhenAzureUserHasNoTemplates() {
        when(currentUserService.getAuthenticatedUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser)).thenReturn(Flux.empty());

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler())
                        .extracting("brukerId")
                        .containsExactly("ALLE"))
                .verifyComplete();
    }

    @Test
    void shouldScopeDeleteToAuthenticatedOwner() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.deleteByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(1L));

        StepVerifier.create(malService.delete(42L))
                .verifyComplete();

        verify(malRepository).deleteByIdAndBrukerId(42L, "azure-id");
    }

    @Test
    void shouldScopeDeleteToCurrentlyRepresentedTeam() {
        var teamOwner = new TenorMalOwner(
                "team-bruker-id-42",
                "team-bruker-id-42",
                TenorMalBrukerType.TEAM);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(teamOwner));
        when(malRepository.deleteByIdAndBrukerId(42L, "team-bruker-id-42")).thenReturn(Mono.just(1L));

        StepVerifier.create(malService.delete(42L))
                .verifyComplete();

        verify(malRepository).deleteByIdAndBrukerId(42L, "team-bruker-id-42");
    }

    @Test
    void shouldHideTemplateWhenAuthenticatedOwnerDoesNotMatch() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.deleteByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(0L));

        StepVerifier.create(malService.delete(42L))
                .expectError(TenorMalNotFoundException.class)
                .verify();
    }

    @Test
    void shouldRejectDeletingTemplateOwnedByAnotherTeam() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(TEAM_OWNER));
        when(malRepository.deleteByIdAndBrukerId(43L, TEAM_OWNER.brukerId())).thenReturn(Mono.just(0L));

        StepVerifier.create(malService.delete(43L))
                .expectError(TenorMalNotFoundException.class)
                .verify();

        verify(malRepository).deleteByIdAndBrukerId(43L, TEAM_OWNER.brukerId());
        verifyNoInteractions(accessService, dollyBackendConsumer);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Nytt navn", "Nytt navn, med tegn!"})
    void shouldRenameOwnedTemplate(String malNavn) {
        var mal = template(42L, currentUser);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(mal));
        when(malRepository.save(mal)).thenReturn(Mono.just(mal));

        StepVerifier.create(malService.updateMalNavn(42L, malNavn))
                .assertNext(response -> assertThat(response.malNavn()).isEqualTo(malNavn))
                .verifyComplete();

        assertThat(mal.getBrukernavn()).isEqualTo("Testbruker");
        assertThat(mal.getBrukertype()).isEqualTo(TenorMalBrukerType.AZURE);
    }

    @Test
    void shouldRejectRenameForTemplateOwnedByAnotherUser() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.empty());

        StepVerifier.create(malService.updateMalNavn(42L, "Nytt navn"))
                .expectError(TenorMalNotFoundException.class)
                .verify();
    }

    @Test
    void shouldRejectRenameToExistingName() {
        var mal = template(42L, currentUser);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(mal));
        when(malRepository.save(mal)).thenReturn(Mono.error(new DataIntegrityViolationException("duplicate")));

        StepVerifier.create(malService.updateMalNavn(42L, "Eksisterende navn"))
                .expectError(TenorMalConflictException.class)
                .verify();
    }

    private static TenorPersonMal template(Long id, TenorMalOwner owner) {
        return TenorPersonMal.builder()
                .id(id)
                .malNavn("Min mal")
                .soekKriterier("{}")
                .brukerId(owner.brukerId())
                .brukernavn(owner.brukernavn())
                .brukertype(owner.brukertype())
                .opprettet(CREATED)
                .sistOppdatert(CREATED)
                .build();
    }
}
