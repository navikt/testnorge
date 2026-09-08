package no.nav.testnav.apps.tenorsearchservice.service;

import no.nav.testnav.apps.tenorsearchservice.domain.OpprettTenorMalRequest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMal;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBruker;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalUpsertResult;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.tenorsearchservice.repository.TenorMalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenorMalServiceTest {

    private static final Instant CREATED = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private CurrentTenorUserService currentUserService;

    @Mock
    private TenorMalAccessService accessService;

    @Mock
    private TenorMalRepository malRepository;

    @Mock
    private TenorMalMetrics metrics;

    private JsonMapper jsonMapper;
    private TenorMalService malService;
    private TenorMalBruker currentUser;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder().build();
        malService = new TenorMalService(
                currentUserService,
                accessService,
                new TenorMalValidationService(jsonMapper),
                malRepository,
                jsonMapper,
                metrics);
        currentUser = TenorMalBruker.builder()
                .id(12L)
                .brukerId("azure-id")
                .brukernavn("Testbruker")
                .brukertype(TenorMalBrukerType.AZURE)
                .build();
    }

    @Test
    void shouldCreateTemplateWithAuthenticatedOwner() {
        var request = new OpprettTenorMalRequest(
                "  Min mal  ",
                TenorMalType.PERSON,
                jsonMapper.readTree("{}"));
        when(currentUserService.getOrCreateCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.upsert("Min mal", "min mal", "PERSON", "{}", 12L))
                .thenReturn(Mono.just(new TenorMalUpsertResult(
                        42L,
                        "Min mal",
                        "min mal",
                        TenorMalType.PERSON,
                        "{}",
                        12L,
                        CREATED,
                        CREATED,
                        true)));

        StepVerifier.create(malService.save(request))
                .assertNext(result -> {
                    assertThat(result.opprettet()).isTrue();
                    assertThat(result.mal().brukerId()).isEqualTo("azure-id");
                    assertThat(result.mal().id()).isEqualTo(42L);
                })
                .verifyComplete();
    }

    @Test
    void shouldGetOnlyCurrentUsersTemplatesByDefault() {
        var mal = template(42L, currentUser.getId());
        when(currentUserService.getOrCreateCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.findByBrukerIdInOrderByMalNavnAscIdAsc(List.of(12L)))
                .thenReturn(Flux.just(mal));

        StepVerifier.create(malService.getMaler(null, null))
                .assertNext(response -> assertThat(response.id()).isEqualTo(42L))
                .verifyComplete();

        verify(accessService, never()).getAccessibleUsers(currentUser);
    }

    @Test
    void shouldScopeDeleteToAuthenticatedOwner() {
        when(currentUserService.getOrCreateCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.deleteByIdAndBrukerId(42L, 12L)).thenReturn(Mono.just(1));

        StepVerifier.create(malService.delete(42L))
                .verifyComplete();

        verify(malRepository).deleteByIdAndBrukerId(42L, 12L);
    }

    @Test
    void shouldHideTemplateWhenAuthenticatedOwnerDoesNotMatch() {
        when(currentUserService.getOrCreateCurrentUser()).thenReturn(Mono.just(currentUser));
        when(malRepository.deleteByIdAndBrukerId(42L, 12L)).thenReturn(Mono.just(0));

        StepVerifier.create(malService.delete(42L))
                .expectError(TenorMalNotFoundException.class)
                .verify();
    }

    private static TenorMal template(Long id, Long ownerId) {
        return TenorMal.builder()
                .id(id)
                .malNavn("Min mal")
                .malNavnNormalisert("min mal")
                .malType(TenorMalType.PERSON)
                .soekKriterier("{}")
                .brukerId(ownerId)
                .opprettet(CREATED)
                .sistOppdatert(CREATED)
                .build();
    }
}
