package no.nav.pdl.forvalter.service;

import io.r2dbc.spi.R2dbcException;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentitetServiceTest {

    private static final String IDENT = "27502551642";
    private static final String RELATERT_IDENT = "01010112345";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelasjonRepository relasjonRepository;

    @InjectMocks
    private IdentitetService identitetService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldUpdateStandaloneOnLockedPerson(boolean standalone) {

        var hovedperson = person(1L, IDENT, 4);
        hovedperson.getPerson().setStandalone(!standalone);
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.just(hovedperson));
        when(personRepository.save(hovedperson)).thenReturn(Mono.just(hovedperson));
        when(relasjonRepository.findByPersonId(hovedperson.getId())).thenReturn(Flux.empty());

        StepVerifier.create(identitetService.updateStandalone(IDENT, standalone))
                .verifyComplete();

        assertThat(hovedperson.getPerson().isStandalone()).isEqualTo(standalone);
        verify(personRepository).findByIdentAndLock(IDENT);
        verify(personRepository).save(hovedperson);
        verify(personRepository, never()).findByIdent(IDENT);
    }

    @Test
    void shouldPropagateOptimisticLockingFailureWithoutRetrying() {

        var optimisticLockingFailure = new OptimisticLockingFailureException("stale");
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.just(person(1L, IDENT, 4)));
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.error(optimisticLockingFailure));

        StepVerifier.create(identitetService.updateStandalone(IDENT, true))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(optimisticLockingFailure))
                .verify();

        verify(personRepository).findByIdentAndLock(IDENT);
        verify(personRepository).save(any(DbPerson.class));
        verifyNoInteractions(relasjonRepository);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldUpdateMatchingRelationOnRelatedPersonOnlyOnce(boolean standalone) {

        var hovedperson = person(1L, IDENT, 4);
        var relatertPerson = relatertPerson(2L, 2);
        var matchingRelation = relatertPerson.getPerson().getForelderBarnRelasjon().getFirst();
        matchingRelation.setEksisterendePerson(!standalone);
        var unrelatedRelation = ForelderBarnRelasjonDTO.builder()
                .relatertPerson(RELATERT_IDENT)
                .eksisterendePerson(!standalone)
                .build();
        relatertPerson.getPerson().setForelderBarnRelasjon(List.of(matchingRelation, unrelatedRelation));
        var relasjon = DbRelasjon.builder()
                .personId(hovedperson.getId())
                .relatertPersonId(relatertPerson.getId())
                .build();
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.just(hovedperson));
        when(personRepository.save(hovedperson)).thenReturn(Mono.just(hovedperson));
        when(relasjonRepository.findByPersonId(hovedperson.getId())).thenReturn(Flux.just(relasjon, relasjon));
        when(personRepository.findById(relatertPerson.getId())).thenReturn(Mono.just(relatertPerson));
        when(personRepository.save(relatertPerson)).thenReturn(Mono.just(relatertPerson));

        StepVerifier.create(identitetService.updateStandalone(IDENT, standalone))
                .verifyComplete();

        assertThat(matchingRelation.isEksisterendePerson()).isEqualTo(standalone);
        assertThat(unrelatedRelation.isEksisterendePerson()).isEqualTo(!standalone);
        verify(personRepository).findById(relatertPerson.getId());
        verify(personRepository).save(relatertPerson);
    }

    @Test
    void shouldRejectMissingPersonWithoutSavingOrUpdatingRelations() {

        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.empty());

        StepVerifier.create(identitetService.updateStandalone(IDENT, true))
                .expectError(NotFoundException.class)
                .verify();

        verify(personRepository, never()).save(any(DbPerson.class));
        verifyNoInteractions(relasjonRepository);
    }

    @Test
    void shouldPropagateRelatedPersonSaveFailureWithoutRetrying() {

        var hovedperson = person(1L, IDENT, 4);
        var relatertPerson = relatertPerson(2L, 2);
        var saveFailure = new OptimisticLockingFailureException("stale related person");
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.just(hovedperson));
        when(personRepository.save(hovedperson)).thenReturn(Mono.just(hovedperson));
        when(relasjonRepository.findByPersonId(hovedperson.getId())).thenReturn(Flux.just(
                DbRelasjon.builder()
                        .personId(hovedperson.getId())
                        .relatertPersonId(relatertPerson.getId())
                        .build()));
        when(personRepository.findById(relatertPerson.getId())).thenReturn(Mono.just(relatertPerson));
        when(personRepository.save(relatertPerson)).thenReturn(Mono.error(saveFailure));

        StepVerifier.create(identitetService.updateStandalone(IDENT, true))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(saveFailure))
                .verify();

        verify(personRepository).findById(relatertPerson.getId());
        verify(personRepository).save(relatertPerson);
    }

    @Test
    void shouldRereadBothPersonsWhenRelatedPersonSaveDeadlocks() {

        var hovedperson = person(1L, IDENT, 4);
        var freshHovedperson = person(1L, IDENT, 5);
        var relatertPerson = relatertPerson(2L, 2);
        var freshRelatertPerson = relatertPerson(2L, 3);
        var deadlock = mock(R2dbcException.class);
        when(deadlock.getSqlState()).thenReturn("40P01");
        when(personRepository.findByIdentAndLock(IDENT))
                .thenReturn(Mono.just(hovedperson))
                .thenReturn(Mono.just(freshHovedperson));
        when(personRepository.save(same(hovedperson))).thenReturn(Mono.just(hovedperson));
        when(personRepository.save(same(freshHovedperson))).thenReturn(Mono.just(freshHovedperson));
        when(relasjonRepository.findByPersonId(hovedperson.getId())).thenAnswer(_ -> Flux.just(
                DbRelasjon.builder()
                        .personId(hovedperson.getId())
                        .relatertPersonId(relatertPerson.getId())
                        .build()));
        when(personRepository.findById(relatertPerson.getId()))
                .thenReturn(Mono.just(relatertPerson))
                .thenReturn(Mono.just(freshRelatertPerson));
        when(personRepository.save(same(relatertPerson))).thenReturn(Mono.error(deadlock));
        when(personRepository.save(same(freshRelatertPerson))).thenReturn(Mono.just(freshRelatertPerson));

        StepVerifier.withVirtualTime(() -> identitetService.updateStandalone(IDENT, true))
                .thenAwait(Duration.ofSeconds(1))
                .verifyComplete();

        assertThat(freshHovedperson.getPerson().isStandalone()).isTrue();
        assertThat(freshRelatertPerson.getPerson().getForelderBarnRelasjon()
                .getFirst().isEksisterendePerson()).isTrue();
        verify(personRepository, times(2)).findByIdentAndLock(IDENT);
        verify(personRepository, times(2)).findById(relatertPerson.getId());
        verify(personRepository).save(same(hovedperson));
        verify(personRepository).save(same(freshHovedperson));
        verify(personRepository).save(same(relatertPerson));
        verify(personRepository).save(same(freshRelatertPerson));
    }

    @Test
    void shouldPropagateOriginalDeadlockAfterThreeRetries() {

        var deadlock = mock(R2dbcException.class);
        when(deadlock.getSqlState()).thenReturn("40P01");
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.error(deadlock));

        StepVerifier.withVirtualTime(() -> identitetService.updateStandalone(IDENT, true))
                .thenAwait(Duration.ofSeconds(3))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(deadlock))
                .verify();

        verify(personRepository, times(4)).findByIdentAndLock(IDENT);
        verify(personRepository, never()).save(any(DbPerson.class));
        verifyNoInteractions(relasjonRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"23505", "40001"})
    void shouldPropagateOtherSqlStatesWithoutRetrying(String sqlState) {

        var databaseFailure = mock(R2dbcException.class);
        when(databaseFailure.getSqlState()).thenReturn(sqlState);
        when(personRepository.findByIdentAndLock(IDENT)).thenReturn(Mono.error(databaseFailure));

        StepVerifier.create(identitetService.updateStandalone(IDENT, true))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(databaseFailure))
                .verify();

        verify(personRepository).findByIdentAndLock(IDENT);
        verify(personRepository, never()).save(any(DbPerson.class));
        verifyNoInteractions(relasjonRepository);
    }

    private static DbPerson person(Long id, String ident, Integer versjon) {
        return DbPerson.builder()
                .id(id)
                .ident(ident)
                .versjon(versjon)
                .person(PersonDTO.builder()
                        .ident(ident)
                        .build())
                .build();
    }

    private static DbPerson relatertPerson(Long id, Integer versjon) {
        return DbPerson.builder()
                .id(id)
                .ident(RELATERT_IDENT)
                .versjon(versjon)
                .person(PersonDTO.builder()
                        .ident(RELATERT_IDENT)
                        .forelderBarnRelasjon(List.of(
                                ForelderBarnRelasjonDTO.builder()
                                        .relatertPerson(IDENT)
                                        .build()))
                        .build())
                .build();
    }
}
