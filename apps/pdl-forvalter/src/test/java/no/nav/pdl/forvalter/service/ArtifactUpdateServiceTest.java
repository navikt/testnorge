package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.test.publisher.PublisherProbe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.VERGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactUpdateServiceTest {

    private static final String IDENT = "12345678901";
    private static final String VERGE_IDENT = "23456789012";
    private static final String NY_VERGE_IDENT = "34567890123";

    @Mock
    private PersonRepository personRepository;
    @Mock
    private RelasjonRepository relasjonRepository;
    @Mock
    private PersonService personService;
    @Mock
    private DeleteRelasjonerService deleteRelasjonerService;
    @Mock
    private VergemaalService vergemaalService;

    @InjectMocks
    private ArtifactUpdateService artifactUpdateService;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReplaceVergemaalWithoutDuplicatesAndPreserveOtherEntries(boolean sameExistingGuardian) {
        var original = vergemaal(2);
        var other = vergemaal(4);
        var person = person(original, other);
        var request = VergemaalDTO.builder().vergeIdent(VERGE_IDENT).build();
        if (sameExistingGuardian) {
            request.setNyVergeIdent(PersonRequestDTO.builder().eksisterendeIdent(VERGE_IDENT).build());
        }
        givenPerson(person, request);
        givenConversionAndSave(person);

        StepVerifier.create(artifactUpdateService.updateVergemaal(" " + IDENT + " ", 2, request))
                .verifyComplete();

        assertThat(person.getPerson().getVergemaal()).containsExactly(other, request);
        assertThat(person.getPerson().getVergemaal())
                .extracting(VergemaalDTO::getId).containsExactly(4, 2);
        assertThat(person.getPerson().getVergemaal().getLast()).isSameAs(request);
        verifyNoInteractions(deleteRelasjonerService, relasjonRepository, personService);
        var order = inOrder(vergemaalService, personRepository);
        order.verify(vergemaalService).convert(person);
        order.verify(personRepository).save(person);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldCreateVergemaalWithNextIdExactlyOnce(boolean existingEntries) {
        var existing = vergemaal(4);
        var person = existingEntries ? person(existing) : person();
        var request = VergemaalDTO.builder().vergeIdent(VERGE_IDENT).build();
        givenPerson(person, request);
        givenConversionAndSave(person);

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 0, request))
                .verifyComplete();

        if (existingEntries) {
            assertThat(person.getPerson().getVergemaal()).containsExactly(request, existing);
        } else {
            assertThat(person.getPerson().getVergemaal()).containsExactly(request);
        }
        assertThat(request.getId()).isEqualTo(existingEntries ? 5 : 1);
        verifyNoInteractions(deleteRelasjonerService, relasjonRepository, personService);
    }

    @Test
    void shouldRejectMissingVergemaalWithoutConvertingOrSaving() {
        var original = vergemaal(1);
        var person = person(original);
        var request = VergemaalDTO.builder().vergeIdent(VERGE_IDENT).build();
        givenPerson(person, request);

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 7, request))
                .verifyErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining("Vergemaal med id: 7 ble ikke funnet"));

        assertThat(person.getPerson().getVergemaal()).containsExactly(original);
        verify(vergemaalService, never()).convert(any());
        verify(personRepository, never()).save(any());
        verifyNoInteractions(deleteRelasjonerService, relasjonRepository, personService);
    }

    @Test
    void shouldRejectMissingPerson() {
        var request = VergemaalDTO.builder().build();
        when(vergemaalService.validate(request)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.empty());

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyErrorSatisfies(error -> assertThat(error)
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageContaining("Person med ident: " + IDENT + " ble ikke funnet"));

        verify(vergemaalService, never()).convert(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void shouldPropagateValidationErrorWithoutSubscribingToPersonLookup() {
        var request = VergemaalDTO.builder().build();
        var error = new InvalidRequestException("Invalid guardianship");
        var lookup = PublisherProbe.of(Mono.just(person()));
        when(vergemaalService.validate(request)).thenReturn(Mono.error(error));
        when(personRepository.findByIdent(IDENT)).thenReturn(lookup.mono());

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyErrorSatisfies(actual -> assertThat(actual).isSameAs(error));

        lookup.assertWasNotSubscribed();
        verify(vergemaalService, never()).convert(any());
        verify(personRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true"})
    void shouldCleanUpOldGuardianBeforeConvertingAndSaving(boolean standalone, boolean remainingRelations) {
        var original = vergemaal(1);
        original.setEksisterendePerson(standalone);
        var person = person(original);
        var guardian = DbPerson.builder().id(2L).ident(VERGE_IDENT).build();
        var request = changedGuardianRequest();
        givenPerson(person, request);
        when(personRepository.findByIdent(VERGE_IDENT)).thenReturn(Mono.just(guardian));
        when(deleteRelasjonerService.deleteRelasjoner(person, guardian, VERGE)).thenReturn(Mono.empty());
        when(relasjonRepository.existsByPersonIdOrRelatertPersonId(guardian.getId()))
                .thenReturn(Mono.just(remainingRelations));
        if (!standalone && !remainingRelations) {
            when(personService.utfoerEksternSletting(Set.of(VERGE_IDENT))).thenReturn(Mono.just(Set.of(VERGE_IDENT)));
            when(personService.slettMotDatabase(Set.of(VERGE_IDENT))).thenReturn(Mono.just(Set.of(VERGE_IDENT)));
        }
        givenConversionAndSave(person);

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyComplete();

        var order = inOrder(deleteRelasjonerService, relasjonRepository, personService,
                vergemaalService, personRepository);
        order.verify(deleteRelasjonerService).deleteRelasjoner(person, guardian, VERGE);
        order.verify(relasjonRepository).existsByPersonIdOrRelatertPersonId(guardian.getId());
        if (!standalone && !remainingRelations) {
            order.verify(personService).utfoerEksternSletting(Set.of(VERGE_IDENT));
            order.verify(personService).slettMotDatabase(Set.of(VERGE_IDENT));
        } else {
            verifyNoInteractions(personService);
        }
        order.verify(vergemaalService).convert(person);
        order.verify(personRepository).save(person);
        assertThat(person.getPerson().getVergemaal()).containsExactly(request);
    }

    @Test
    void shouldWaitForRelationCleanupBeforeReplacingVergemaal() {
        var original = vergemaal(1);
        original.setEksisterendePerson(true);
        var person = person(original);
        var guardian = DbPerson.builder().id(2L).ident(VERGE_IDENT).build();
        var request = changedGuardianRequest();
        var cleanup = TestPublisher.<Void>create();
        givenPerson(person, request);
        when(personRepository.findByIdent(VERGE_IDENT)).thenReturn(Mono.just(guardian));
        when(deleteRelasjonerService.deleteRelasjoner(person, guardian, VERGE)).thenReturn(cleanup.mono());
        when(relasjonRepository.existsByPersonIdOrRelatertPersonId(guardian.getId())).thenReturn(Mono.just(false));
        givenConversionAndSave(person);

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .then(() -> {
                    cleanup.assertWasSubscribed();
                    assertThat(person.getPerson().getVergemaal()).containsExactly(original);
                    verify(vergemaalService, never()).convert(any());
                    verify(personRepository, never()).save(any());
                })
                .then(cleanup::complete)
                .verifyComplete();

        assertThat(person.getPerson().getVergemaal()).containsExactly(request);
    }

    @Test
    void shouldPropagateRelationCleanupErrorWithoutReplacingOrSaving() {
        var original = vergemaal(1);
        var person = person(original);
        var guardian = DbPerson.builder().id(2L).ident(VERGE_IDENT).build();
        var request = changedGuardianRequest();
        var error = new IllegalStateException("Cleanup failed");
        givenPerson(person, request);
        when(personRepository.findByIdent(VERGE_IDENT)).thenReturn(Mono.just(guardian));
        when(deleteRelasjonerService.deleteRelasjoner(person, guardian, VERGE)).thenReturn(Mono.error(error));

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyErrorSatisfies(actual -> assertThat(actual).isSameAs(error));

        assertThat(person.getPerson().getVergemaal()).containsExactly(original);
        verify(vergemaalService, never()).convert(any());
        verify(personRepository, never()).save(any());
        verifyNoInteractions(relasjonRepository, personService);
    }

    @Test
    void shouldPropagateConversionErrorWithoutSaving() {
        var person = person(vergemaal(1));
        var request = VergemaalDTO.builder().vergeIdent(VERGE_IDENT).build();
        var error = new IllegalStateException("Conversion failed");
        givenPerson(person, request);
        when(vergemaalService.convert(person)).thenReturn(Mono.error(error));

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyErrorSatisfies(actual -> assertThat(actual).isSameAs(error));

        verify(personRepository, never()).save(any());
    }

    @Test
    void shouldPropagateSaveError() {
        var person = person(vergemaal(1));
        var request = VergemaalDTO.builder().vergeIdent(VERGE_IDENT).build();
        var error = new IllegalStateException("Save failed");
        givenPerson(person, request);
        when(vergemaalService.convert(person)).thenReturn(Mono.just(person));
        when(personRepository.save(person)).thenReturn(Mono.error(error));

        StepVerifier.create(artifactUpdateService.updateVergemaal(IDENT, 1, request))
                .verifyErrorSatisfies(actual -> assertThat(actual).isSameAs(error));
    }

    private void givenPerson(DbPerson person, VergemaalDTO request) {
        when(vergemaalService.validate(request)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(person));
    }

    private void givenConversionAndSave(DbPerson person) {
        when(vergemaalService.convert(person)).thenReturn(Mono.just(person));
        when(personRepository.save(person)).thenReturn(Mono.just(person));
    }

    private VergemaalDTO vergemaal(int id) {
        return VergemaalDTO.builder()
                .id(id)
                .vergeIdent(VERGE_IDENT)
                .build();
    }

    private VergemaalDTO changedGuardianRequest() {
        return VergemaalDTO.builder()
                .nyVergeIdent(PersonRequestDTO.builder().eksisterendeIdent(NY_VERGE_IDENT).build())
                .build();
    }

    private DbPerson person(VergemaalDTO... vergemaal) {
        var person = new PersonDTO();
        person.setVergemaal(new ArrayList<>(List.of(vergemaal)));
        return DbPerson.builder().id(1L).ident(IDENT).person(person).build();
    }
}
