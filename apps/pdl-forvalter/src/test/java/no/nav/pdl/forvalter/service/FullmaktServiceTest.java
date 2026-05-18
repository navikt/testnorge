package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FullmaktServiceTest {

    private static final String IDENT = "12345678901";
    private static final String FULLMEKTIG_IDENT = "98765432100";
    private static final String CREATED_IDENT = "11122233344";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CreatePersonService createPersonService;

    @Mock
    private RelasjonService relasjonService;

    @InjectMocks
    private FullmaktService fullmaktService;

    @Test
    void shouldReturnDbPersonWhenNoFullmaktIsNew() {

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(FullmaktDTO.builder()
                .isNew(false)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .build());

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verifyNoInteractions(createPersonService, relasjonService, personRepository);
    }

    @Test
    void shouldReturnDbPersonWhenFullmaktListIsEmpty() {

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verifyNoInteractions(createPersonService, relasjonService, personRepository);
    }

    @Test
    void shouldCreateNewPersonWhenMotpartsPersonidentIsBlank() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var createdPerson = PersonDTO.builder()
                .ident(CREATED_IDENT)
                .build();
        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(createdPerson)
                .build();

        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(IDENT, RelasjonType.FULLMAKTSGIVER,
                CREATED_IDENT, RelasjonType.FULLMEKTIG))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(fullmakt.getMotpartsPersonident()).isEqualTo(CREATED_IDENT);
                    assertThat(fullmakt.getMaster()).isEqualTo(Master.PDL);
                    assertThat(fullmakt.getEksisterendePerson()).isFalse();
                    assertThat(fullmakt.getKilde()).isEqualTo("Dolly");
                })
                .verifyComplete();

        verify(createPersonService).execute(any(PersonRequestDTO.class));
        verify(relasjonService).setRelasjoner(IDENT, RelasjonType.FULLMAKTSGIVER,
                CREATED_IDENT, RelasjonType.FULLMEKTIG);
    }

    @Test
    void shouldSetDefaultAgeRangeWhenNoAgeParametersProvided() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var createdPerson = PersonDTO.builder()
                .ident(CREATED_IDENT)
                .build();
        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(createdPerson)
                .build();

        var requestCaptor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        when(createPersonService.execute(requestCaptor.capture()))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .expectNextCount(1)
                .verifyComplete();

        var capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFoedtFoer()).isBefore(LocalDateTime.now().minusYears(17));
        assertThat(capturedRequest.getFoedtEtter()).isBefore(LocalDateTime.now().minusYears(74));
    }

    @Test
    void shouldNotOverrideAgeWhenAlderIsProvided() {

        var nyFullmektig = new PersonRequestDTO();
        nyFullmektig.setAlder(30);

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .nyFullmektig(nyFullmektig)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var createdPerson = PersonDTO.builder()
                .ident(CREATED_IDENT)
                .build();
        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(createdPerson)
                .build();

        var requestCaptor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        when(createPersonService.execute(requestCaptor.capture()))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .expectNextCount(1)
                .verifyComplete();

        var capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getAlder()).isEqualTo(30);
        assertThat(capturedRequest.getFoedtFoer()).isNull();
        assertThat(capturedRequest.getFoedtEtter()).isNull();
    }

    @Test
    void shouldNotOverrideAgeWhenFoedtEtterIsProvided() {

        var nyFullmektig = new PersonRequestDTO();
        nyFullmektig.setFoedtEtter(LocalDateTime.of(1980, 1, 1, 0, 0));

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .nyFullmektig(nyFullmektig)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(PersonDTO.builder().ident(CREATED_IDENT).build())
                .build();

        var requestCaptor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        when(createPersonService.execute(requestCaptor.capture()))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .expectNextCount(1)
                .verifyComplete();

        var capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFoedtEtter()).isEqualTo(LocalDateTime.of(1980, 1, 1, 0, 0));
        assertThat(capturedRequest.getFoedtFoer()).isNull();
    }

    @Test
    void shouldUseExistingPersonWhenMotpartsPersonidentIsPresent() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var existingPerson = DbPerson.builder()
                .ident(FULLMEKTIG_IDENT)
                .person(PersonDTO.builder().ident(FULLMEKTIG_IDENT).build())
                .build();

        when(personRepository.findByIdent(FULLMEKTIG_IDENT))
                .thenReturn(Mono.just(existingPerson));
        when(relasjonService.setRelasjoner(IDENT, RelasjonType.FULLMAKTSGIVER,
                FULLMEKTIG_IDENT, RelasjonType.FULLMEKTIG))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(fullmakt.getEksisterendePerson()).isTrue();
                    assertThat(fullmakt.getMaster()).isEqualTo(Master.PDL);
                    assertThat(fullmakt.getMotpartsPersonident()).isEqualTo(FULLMEKTIG_IDENT);
                })
                .verifyComplete();

        verify(createPersonService, never()).execute(any());
        verify(personRepository).findByIdent(FULLMEKTIG_IDENT);
    }

    @Test
    void shouldCreateAndSavePersonWhenExistingPersonNotFoundInRepository() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var savedPerson = DbPerson.builder()
                .ident(FULLMEKTIG_IDENT)
                .person(PersonDTO.builder().ident(FULLMEKTIG_IDENT).build())
                .sistOppdatert(LocalDateTime.now())
                .build();

        when(personRepository.findByIdent(FULLMEKTIG_IDENT))
                .thenReturn(Mono.empty());
        when(personRepository.save(any(DbPerson.class)))
                .thenReturn(Mono.just(savedPerson));
        when(relasjonService.setRelasjoner(IDENT, RelasjonType.FULLMAKTSGIVER,
                FULLMEKTIG_IDENT, RelasjonType.FULLMEKTIG))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(fullmakt.getEksisterendePerson()).isTrue();
                    assertThat(fullmakt.getMaster()).isEqualTo(Master.PDL);
                })
                .verifyComplete();

        verify(personRepository).findByIdent(FULLMEKTIG_IDENT);
        verify(personRepository).save(any(DbPerson.class));
        verify(createPersonService, never()).execute(any());
    }

    @Test
    void shouldSetKildeToExistingValueWhenPresent() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .kilde("TestKilde")
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var existingPerson = DbPerson.builder()
                .ident(FULLMEKTIG_IDENT)
                .person(PersonDTO.builder().ident(FULLMEKTIG_IDENT).build())
                .build();

        when(personRepository.findByIdent(FULLMEKTIG_IDENT))
                .thenReturn(Mono.just(existingPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(fullmakt.getKilde()).isEqualTo("TestKilde"))
                .verifyComplete();
    }

    @Test
    void shouldSetKildeToDollyWhenKildeIsBlank() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var existingPerson = DbPerson.builder()
                .ident(FULLMEKTIG_IDENT)
                .person(PersonDTO.builder().ident(FULLMEKTIG_IDENT).build())
                .build();

        when(personRepository.findByIdent(FULLMEKTIG_IDENT))
                .thenReturn(Mono.just(existingPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(fullmakt.getKilde()).isEqualTo("Dolly"))
                .verifyComplete();
    }

    @Test
    void shouldProcessMultipleNewFullmaktEntries() {

        var fullmakt1 = FullmaktDTO.builder()
                .isNew(true)
                .motpartsPersonident(FULLMEKTIG_IDENT)
                .build();

        var fullmakt2 = FullmaktDTO.builder()
                .isNew(true)
                .build();

        var fullmaktNotNew = FullmaktDTO.builder()
                .isNew(false)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().addAll(List.of(fullmakt1, fullmakt2, fullmaktNotNew));

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var existingPerson = DbPerson.builder()
                .ident(FULLMEKTIG_IDENT)
                .person(PersonDTO.builder().ident(FULLMEKTIG_IDENT).build())
                .build();

        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(PersonDTO.builder().ident(CREATED_IDENT).build())
                .build();

        when(personRepository.findByIdent(FULLMEKTIG_IDENT))
                .thenReturn(Mono.just(existingPerson));
        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(fullmakt1.getMotpartsPersonident()).isEqualTo(FULLMEKTIG_IDENT);
                    assertThat(fullmakt2.getMotpartsPersonident()).isEqualTo(CREATED_IDENT);
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateNyFullmektigWhenItIsNull() {

        var fullmakt = FullmaktDTO.builder()
                .isNew(true)
                .nyFullmektig(null)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getFullmakt().add(fullmakt);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var createdDbPerson = DbPerson.builder()
                .ident(CREATED_IDENT)
                .person(PersonDTO.builder().ident(CREATED_IDENT).build())
                .build();

        var requestCaptor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        when(createPersonService.execute(requestCaptor.capture()))
                .thenReturn(Mono.just(createdDbPerson));
        when(relasjonService.setRelasjoner(any(), any(), any(), any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(fullmaktService.convert(dbPerson))
                .expectNextCount(1)
                .verifyComplete();

        assertThat(requestCaptor.getValue()).isNotNull();
        assertThat(requestCaptor.getValue().getFoedtFoer()).isNotNull();
        assertThat(requestCaptor.getValue().getFoedtEtter()).isNotNull();
    }

    @Test
    void shouldReturnEmptyOnValidate() {

        var fullmakt = FullmaktDTO.builder().build();
        var person = PersonDTO.builder().ident(IDENT).build();

        StepVerifier.create(fullmaktService.validate(fullmakt, person))
                .verifyComplete();
    }
}
