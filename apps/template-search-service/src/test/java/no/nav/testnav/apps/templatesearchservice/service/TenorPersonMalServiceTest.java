package no.nav.testnav.apps.templatesearchservice.service;

import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalConflictException;
import no.nav.testnav.apps.templatesearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.templatesearchservice.repository.TenorPersonMalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenorPersonMalServiceTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private CurrentTenorUserService currentUserService;

    @Mock
    private TenorMalAccessService accessService;

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
                new TenorPersonMalValidationService(jsonMapper),
                malRepository,
                jsonMapper);
        currentUser = new TenorMalOwner(
                "azure-id",
                "Testbruker",
                TenorMalBrukerType.AZURE);
    }

    @Test
    void shouldCreateTemplateWithAuthenticatedOwner() {
        var request = new OpprettTenorPersonMalRequest(
                "  Min mal  ",
                jsonMapper.readTree("{}"));
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByBrukerIdAndMalNavnIgnoreCase("azure-id", "Min mal"))
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
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(currentUserMal, otherUserMal));

        StepVerifier.create(malService.getMaler("other-azure-id"))
                .assertNext(mal -> assertThat(mal.id()).isEqualTo(43L))
                .verifyComplete();
    }

    @Test
    void shouldReturnAllAzureTemplatesForAlle() {
        var otherUser = new TenorMalOwner(
                "other-azure-id",
                "Annen bruker",
                TenorMalBrukerType.AZURE);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(
                        template(42L, currentUser),
                        template(43L, otherUser)));

        StepVerifier.create(malService.getMaler("ALLE"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldReturnDollyStyleUserOverview() {
        var otherUser = new TenorMalOwner(
                "other-azure-id",
                "Annen bruker",
                TenorMalBrukerType.AZURE);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(accessService.getAccessibleMaler(currentUser))
                .thenReturn(Flux.just(
                        template(42L, currentUser),
                        template(43L, otherUser)));

        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler())
                        .extracting("brukerId")
                        .containsExactly("ALLE", "other-azure-id", "azure-id"))
                .verifyComplete();
    }

    @Test
    void shouldNotExposeAlleForBankIdUser() {
        var bankIdUser = new TenorMalOwner(
                "hashed-id",
                "BankID-bruker",
                TenorMalBrukerType.BANKID);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(bankIdUser));
        when(accessService.getAccessibleMaler(bankIdUser))
                .thenReturn(Flux.just(template(42L, bankIdUser)));

        StepVerifier.create(malService.getMaler("ALLE"))
                .verifyComplete();
        StepVerifier.create(malService.getMalOversikt())
                .assertNext(response -> assertThat(response.brukereMedMaler())
                        .extracting("brukerId")
                        .containsExactly("hashed-id"))
                .verifyComplete();
    }

    @Test
    void shouldReturnOnlyAlleWhenAzureUserHasNoTemplates() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
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
    void shouldHideTemplateWhenAuthenticatedOwnerDoesNotMatch() {
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.deleteByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(0L));

        StepVerifier.create(malService.delete(42L))
                .expectError(TenorMalNotFoundException.class)
                .verify();
    }

    @Test
    void shouldRenameOwnedTemplate() {
        var mal = template(42L, currentUser);
        when(currentUserService.getCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByIdAndBrukerId(42L, "azure-id")).thenReturn(Mono.just(mal));
        when(malRepository.save(mal)).thenReturn(Mono.just(mal));

        StepVerifier.create(malService.updateMalNavn(42L, "Nytt navn"))
                .assertNext(response -> assertThat(response.malNavn()).isEqualTo("Nytt navn"))
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
