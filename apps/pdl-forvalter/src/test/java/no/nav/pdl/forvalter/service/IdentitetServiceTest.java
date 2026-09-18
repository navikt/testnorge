package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Test
    void shouldRetryStandaloneUpdateWithFreshPersonAfterOptimisticLockingFailure() {

        var stalePerson = person(1L, IDENT, 4);
        var freshPerson = person(1L, IDENT, 5);
        var savedPerson = person(1L, IDENT, 6);
        when(personRepository.findByIdent(IDENT))
                .thenReturn(Mono.just(stalePerson), Mono.just(freshPerson));
        when(personRepository.save(stalePerson))
                .thenReturn(Mono.error(new OptimisticLockingFailureException("stale")));
        when(personRepository.save(freshPerson)).thenReturn(Mono.just(savedPerson));
        when(relasjonRepository.findByPersonId(savedPerson.getId())).thenReturn(Flux.empty());

        StepVerifier.withVirtualTime(() -> identitetService.updateStandalone(IDENT, true))
                .thenAwait(Duration.ofMillis(100))
                .verifyComplete();

        assertThat(freshPerson.getPerson().isStandalone()).isTrue();
        verify(personRepository, times(2)).findByIdent(IDENT);
        verify(personRepository, times(2)).save(any(DbPerson.class));
    }

    @Test
    void shouldPropagateOriginalOptimisticLockingFailureAfterThreeRetries() {

        var optimisticLockingFailure = new OptimisticLockingFailureException("stale");
        when(personRepository.findByIdent(IDENT)).thenAnswer(_ -> Mono.just(person(1L, IDENT, 4)));
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.error(optimisticLockingFailure));

        StepVerifier.withVirtualTime(() -> identitetService.updateStandalone(IDENT, true))
                .thenAwait(Duration.ofSeconds(1))
                .expectErrorSatisfies(error -> assertThat(error).isSameAs(optimisticLockingFailure))
                .verify();

        verify(personRepository, times(4)).findByIdent(IDENT);
        verify(personRepository, times(4)).save(any(DbPerson.class));
    }

    @Test
    void shouldRetryRelatedPersonUpdateWithFreshPersonAfterOptimisticLockingFailure() {

        var hovedperson = person(1L, IDENT, 4);
        var lagretHovedperson = person(1L, IDENT, 5);
        lagretHovedperson.getPerson().setStandalone(true);
        var staleRelatertPerson = relatertPerson(2L, 2);
        var freshRelatertPerson = relatertPerson(2L, 3);
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(hovedperson));
        when(personRepository.save(hovedperson)).thenReturn(Mono.just(lagretHovedperson));
        when(relasjonRepository.findByPersonId(lagretHovedperson.getId())).thenReturn(Flux.just(
                DbRelasjon.builder()
                        .personId(lagretHovedperson.getId())
                        .relatertPersonId(freshRelatertPerson.getId())
                        .build()));
        when(personRepository.findById(freshRelatertPerson.getId()))
                .thenReturn(Mono.just(staleRelatertPerson), Mono.just(freshRelatertPerson));
        when(personRepository.save(staleRelatertPerson))
                .thenReturn(Mono.error(new OptimisticLockingFailureException("stale")));
        when(personRepository.save(freshRelatertPerson)).thenReturn(Mono.just(freshRelatertPerson));

        StepVerifier.withVirtualTime(() -> identitetService.updateStandalone(IDENT, true))
                .thenAwait(Duration.ofMillis(100))
                .verifyComplete();

        assertThat(freshRelatertPerson.getPerson()
                .getForelderBarnRelasjon()
                .getFirst()
                .isEksisterendePerson()).isTrue();
        verify(personRepository, times(2)).findById(freshRelatertPerson.getId());
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
