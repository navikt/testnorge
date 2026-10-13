package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO.IdenttypeExtended;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavPersonIdentifikatorDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.FREG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master.PDL;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.GAMMEL_IDENTITET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.NY_IDENTITET;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdenttypeServiceTest {

    private static final String FNR_IDENT = "01458012345";
    private static final String DNR_IDENT = "41458012345";
    private static final String NPID_IDENT = "01658012345";

    @Mock
    private AliasRepository aliasRepository;
    @Mock
    private CreatePersonService createPersonService;
    @Mock
    private RelasjonService relasjonService;
    @Mock
    private SwopIdentsService swopIdentsService;
    @Mock
    private MapperFacade mapperFacade;
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private IdenttypeService identtypeService;

    @ParameterizedTest
    @CsvSource({"true, true", "true, false", "false, true", "false, false"})
    void shouldRejectDatesOutsideSupportedInterval(boolean foedtEtter, boolean future) {
        var date = future ? LocalDateTime.now().plusDays(1) : LocalDateTime.of(1899, 12, 31, 23, 59);
        var request = IdentRequestDTO.builder()
                .foedtEtter(foedtEtter ? date : null)
                .foedtFoer(foedtEtter ? null : date)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable).isInstanceOf(InvalidRequestException.class)
                                .hasMessage("400 Identtype ugyldig forespørsel: støttet datointervall " +
                                        "er fødsel mellom 1.1.1900 og dagens dato"));
    }

    @Test
    void shouldRejectReversedDateInterval() {

        var request = IdentRequestDTO.builder()
                .identtype(IdenttypeExtended.FNR)
                .foedtEtter(LocalDateTime.of(1980, 1, 1, 1, 0, 0))
                .foedtFoer(LocalDateTime.of(1970, 1, 1, 0, 0))
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable).isInstanceOf(InvalidRequestException.class)
                                .hasMessage("400 Identtype ugyldig forespørsel: fødtFør kan ikke være tidligere enn fødtEtter"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 121})
    void shouldRejectAgeOutsideBoundaries(int age) {
        var request = IdentRequestDTO.builder()
                .alder(age)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable).isInstanceOf(InvalidRequestException.class)
                                .hasMessage("400 Alder må være mellom 0 og 120 år"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, 120})
    void shouldAcceptOptionalAgeAndBoundaryValues(Integer age) {
        var request = IdentRequestDTO.builder()
                .alder(age)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({"true, false", "false, true", "true, true"})
    void shouldAcceptOptionalDateBoundsAtStartOfEra(boolean foedtEtter, boolean foedtFoer) {
        var date = LocalDateTime.of(1900, 1, 1, 0, 0);
        var request = IdentRequestDTO.builder()
                .foedtEtter(foedtEtter ? date : null)
                .foedtFoer(foedtFoer ? date : null)
                .build();

        StepVerifier.create(identtypeService.validate(request)).verifyComplete();
    }

    @Test
    void shouldAcceptValidDateInterval() {
        var request = IdentRequestDTO.builder()
                .foedtEtter(LocalDateTime.of(1980, 1, 1, 0, 0))
                .foedtFoer(LocalDateTime.now().minusDays(1))
                .build();

        StepVerifier.create(identtypeService.validate(request)).verifyComplete();
    }

    @Test
    void shouldReturnUnchangedPersonWithoutNewIdentities() {
        var person = buildPerson(1L, Identtype.FNR);

        StepVerifier.create(identtypeService.convert(person))
                .assertNext(result -> assertThat(result).isSameAs(person))
                .verifyComplete();

        verifyNoInteractions(mapperFacade, createPersonService, personRepository,
                swopIdentsService, aliasRepository, relasjonService);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = {false})
    void shouldSkipIdentitiesNotMarkedAsNew(Boolean isNew) {
        var person = buildPerson(1L, Identtype.FNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(isNew).build();
        prepareMapping(person, request);

        StepVerifier.create(identtypeService.convert(person))
                .assertNext(result -> assertThat(result).isSameAs(person))
                .verifyComplete();

        verifyNoInteractions(createPersonService, personRepository, swopIdentsService, aliasRepository, relasjonService);
    }

    @ParameterizedTest
    @CsvSource({
            "NPID, NPID, true", "NPID, DNR, true", "NPID, FNR, true",
            "DNR, NPID, false", "DNR, DNR, true", "DNR, FNR, true",
            "FNR, NPID, false", "FNR, DNR, false", "FNR, FNR, true"
    })
    void shouldSwapOnlyForEqualOrHigherPriorityIdentity(Identtype originalType, Identtype newType, boolean swap) {
        var person = buildPerson(1L, originalType);
        var newPerson = buildPerson(2L, newType);
        var request = IdentRequestDTO.builder()
                .identtype(IdenttypeExtended.valueOf(newType.name())).isNew(true).build();
        prepareMapping(person, request);
        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(newPerson));
        preparePersistence();
        if (swap) {
            when(swopIdentsService.execute(person.getIdent(), newPerson.getIdent())).thenReturn(Mono.just(newPerson));
        }
        var expectedPerson = swap ? newPerson : person;
        var previousPerson = swap ? person : newPerson;

        StepVerifier.create(identtypeService.convert(person))
                .assertNext(result -> assertThat(result).isSameAs(expectedPerson))
                .verifyComplete();

        var aliasCaptor = ArgumentCaptor.forClass(DbAlias.class);
        verify(aliasRepository).save(aliasCaptor.capture());
        assertThat(aliasCaptor.getValue().getTidligereIdent()).isEqualTo(previousPerson.getIdent());
        assertThat(aliasCaptor.getValue().getPersonId()).isEqualTo(expectedPerson.getId());
        assertThat(aliasCaptor.getValue().getSistOppdatert()).isNotNull();
        verify(relasjonService).setRelasjoner(expectedPerson.getIdent(), GAMMEL_IDENTITET,
                previousPerson.getIdent(), NY_IDENTITET);
        if (swap) {
            verify(swopIdentsService).execute(person.getIdent(), newPerson.getIdent());
        } else {
            verifyNoInteractions(swopIdentsService);
        }
        verifyNoInteractions(personRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "FNR, SYNT, false, true",
            "DNR, SYNT, false, true",
            "NPID, SYNT, false, true",
            "FNR, , , true",
            "FNR, DNR, false, false",
            "FNR, DNR, true, true"
    })
    void shouldNormalizeIdentityTypeAndSyntheticFlag(Identtype originalType, IdenttypeExtended requestedType,
                                                     Boolean synthetic, boolean expectedSynthetic) {
        var person = buildPerson(1L, originalType);
        var request = IdentRequestDTO.builder()
                .identtype(requestedType).syntetisk(synthetic).isNew(true).build();
        var expectedType = requestedType == null || requestedType == IdenttypeExtended.SYNT ?
                originalType : Identtype.valueOf(requestedType.name());
        prepareMapping(person, request);
        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(buildPerson(2L, Identtype.NPID)));
        preparePersistence();
        if (originalType == Identtype.NPID) {
            when(swopIdentsService.execute(any(String.class), any(String.class))).thenReturn(Mono.just(person));
        }

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        var createdRequest = capturePersonRequest();
        assertThat(createdRequest.getIdenttype()).isEqualTo(expectedType);
        assertThat(createdRequest.getSyntetisk()).isEqualTo(expectedSynthetic);
        assertThat(request.getIdenttype()).isEqualTo(IdenttypeExtended.valueOf(expectedType.name()));
        assertThat(request.getKilde()).isEqualTo("Dolly");
        assertThat(request.getMaster()).isEqualTo(originalType == Identtype.NPID ? PDL : FREG);
    }

    @Test
    void shouldInheritNonSyntheticFlagFromOriginalIdent() {
        var person = buildPerson(1L, Identtype.FNR);
        person.setIdent("01018012345");
        person.getPerson().setIdent(person.getIdent());
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(true).build();
        prepareCreation(person, request);

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        assertThat(capturePersonRequest().getSyntetisk()).isFalse();
    }

    @Test
    void shouldUseExistingIdentityWithoutCreatingPerson() {
        var person = buildPerson(1L, Identtype.FNR);
        var existingPerson = buildPerson(2L, Identtype.DNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR)
                .eksisterendeIdent(existingPerson.getIdent()).isNew(true).build();
        prepareMapping(person, request);
        when(personRepository.findByIdent(existingPerson.getIdent())).thenReturn(Mono.just(existingPerson));
        preparePersistence();

        StepVerifier.create(identtypeService.convert(person))
                .assertNext(result -> assertThat(result).isSameAs(person))
                .verifyComplete();

        verify(personRepository).findByIdent(existingPerson.getIdent());
        verifyNoInteractions(createPersonService, swopIdentsService);
        verify(relasjonService).setRelasjoner(person.getIdent(), GAMMEL_IDENTITET,
                existingPerson.getIdent(), NY_IDENTITET);
    }

    @Test
    void shouldFailWhenExistingIdentityIsMissing() {
        var person = buildPerson(1L, Identtype.FNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR)
                .eksisterendeIdent(DNR_IDENT).isNew(true).build();
        prepareMapping(person, request);
        when(personRepository.findByIdent(DNR_IDENT)).thenReturn(Mono.empty());

        StepVerifier.create(identtypeService.convert(person))
                .verifyErrorSatisfies(error -> assertThat(error).isInstanceOf(NotFoundException.class)
                        .hasMessage("404 Eksisterende ident %s ble ikke funnet", DNR_IDENT));

        verifyNoInteractions(createPersonService, swopIdentsService, aliasRepository, relasjonService);
    }

    @Test
    void shouldPreserveExplicitCreationFieldsAndExpireOnlyActiveNavIdentifiers() {
        var person = buildPerson(1L, Identtype.FNR);
        var expiredDate = LocalDate.of(2020, 1, 1);
        var activeIdent = NavPersonIdentifikatorDTO.builder().identifikator(NPID_IDENT).build();
        var expiredIdent = NavPersonIdentifikatorDTO.builder().gyldigTilOgMed(expiredDate).build();
        person.getPerson().getNavPersonIdentifikator().addAll(List.of(activeIdent, expiredIdent));
        var start = LocalDateTime.of(1990, 1, 1, 0, 0);
        var end = start.plusDays(10);
        var name = IdentRequestDTO.NyttNavnDTO.builder().hasMellomnavn(true).build();
        var mappedName = PersonRequestDTO.NyttNavnDTO.builder().hasMellomnavn(true).build();
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(true)
                .kjoenn(KVINNE).foedtEtter(start).foedtFoer(end).alder(40).id2032(true)
                .nyttNavn(name).kilde("Test").master(PDL).build();
        prepareCreation(person, request);
        when(mapperFacade.map(name, PersonRequestDTO.NyttNavnDTO.class)).thenReturn(mappedName);

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        var createdRequest = capturePersonRequest();
        assertThat(createdRequest.getKjoenn()).isEqualTo(KVINNE);
        assertThat(createdRequest.getFoedtEtter()).isEqualTo(start);
        assertThat(createdRequest.getFoedtFoer()).isEqualTo(end);
        assertThat(createdRequest.getId2032()).isTrue();
        assertThat(createdRequest.getNyttNavn()).isSameAs(mappedName);
        assertThat(request.getKilde()).isEqualTo("Test");
        assertThat(request.getMaster()).isEqualTo(PDL);
        assertThat(activeIdent.getGyldigTilOgMed()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(expiredIdent.getGyldigTilOgMed()).isEqualTo(expiredDate);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"UKJENT"})
    void shouldInheritGenderBirthDateAndId2032WhenNotSpecified(KjoennDTO.Kjoenn gender) {
        var person = buildPerson(1L, Identtype.FNR);
        person.getPerson().setId2032(true);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR)
                .kjoenn(gender).isNew(true).build();
        prepareCreation(person, request);

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        var createdRequest = capturePersonRequest();
        assertThat(createdRequest.getKjoenn()).isEqualTo(MANN);
        assertThat(createdRequest.getFoedtEtter()).isEqualTo(LocalDateTime.of(1980, 5, 1, 0, 0));
        assertThat(createdRequest.getFoedtFoer()).isEqualTo(createdRequest.getFoedtEtter());
        assertThat(createdRequest.getId2032()).isTrue();
    }

    @Test
    void shouldUseAgeForBirthDateBoundsAndAllowExplicitFalseId2032() {
        var person = buildPerson(1L, Identtype.FNR);
        person.getPerson().setId2032(true);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR)
                .alder(30).id2032(false).isNew(true).build();
        prepareCreation(person, request);
        var earliest = LocalDateTime.now().minusYears(30);

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        var latest = LocalDateTime.now().minusYears(30);
        var createdRequest = capturePersonRequest();
        assertThat(createdRequest.getFoedtEtter()).isBetween(earliest, latest);
        assertThat(createdRequest.getFoedtFoer()).isBetween(earliest, latest);
        assertThat(createdRequest.getId2032()).isFalse();
    }

    @Test
    void shouldAdjustEndDateWhenInheritedBirthDatePrecedesExplicitStart() {
        var person = buildPerson(1L, Identtype.FNR);
        var start = LocalDateTime.of(1990, 1, 1, 0, 0);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR)
                .foedtEtter(start).isNew(true).build();
        prepareCreation(person, request);

        StepVerifier.create(identtypeService.convert(person)).expectNextCount(1).verifyComplete();

        assertThat(capturePersonRequest().getFoedtFoer()).isEqualTo(start.plusDays(3));
    }

    @ParameterizedTest
    @CsvSource({"FNR, DNR, NPID", "DNR, NPID, FNR", "NPID, FNR, DNR"})
    void shouldProcessIdentitiesSequentiallyInPriorityOrderUsingUpdatedPerson(
            IdenttypeExtended firstType, IdenttypeExtended secondType, IdenttypeExtended thirdType) {
        var person = buildPerson(1L, Identtype.NPID);
        var npidPerson = buildPerson(4L, Identtype.NPID);
        npidPerson.setIdent("01658054321");
        npidPerson.getPerson().setIdent(npidPerson.getIdent());
        var dnrPerson = buildPerson(2L, Identtype.DNR);
        var fnrPerson = buildPerson(3L, Identtype.FNR);
        var requests = Stream.of(firstType, secondType, thirdType)
                .map(type -> IdentRequestDTO.builder().identtype(type).isNew(true).build())
                .toArray(IdentRequestDTO[]::new);
        prepareMapping(person, requests);
        when(createPersonService.execute(any(PersonRequestDTO.class))).thenAnswer(invocation -> {
            PersonRequestDTO request = invocation.getArgument(0);
            return Mono.just(switch (request.getIdenttype()) {
                case NPID -> npidPerson;
                case DNR -> dnrPerson;
                case FNR -> fnrPerson;
            });
        });
        when(swopIdentsService.execute(NPID_IDENT, npidPerson.getIdent())).thenReturn(Mono.just(npidPerson));
        when(swopIdentsService.execute(npidPerson.getIdent(), DNR_IDENT)).thenReturn(Mono.just(dnrPerson));
        when(swopIdentsService.execute(DNR_IDENT, FNR_IDENT)).thenReturn(Mono.just(fnrPerson));
        preparePersistence();

        StepVerifier.create(identtypeService.convert(person))
                .assertNext(result -> assertThat(result).isSameAs(fnrPerson))
                .verifyComplete();

        var order = inOrder(createPersonService, swopIdentsService, aliasRepository, relasjonService);
        var requestCaptor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        order.verify(createPersonService).execute(requestCaptor.capture());
        order.verify(swopIdentsService).execute(NPID_IDENT, npidPerson.getIdent());
        order.verify(aliasRepository).save(any(DbAlias.class));
        order.verify(relasjonService).setRelasjoner(npidPerson.getIdent(), GAMMEL_IDENTITET, NPID_IDENT, NY_IDENTITET);
        order.verify(createPersonService).execute(requestCaptor.capture());
        order.verify(swopIdentsService).execute(npidPerson.getIdent(), DNR_IDENT);
        order.verify(aliasRepository).save(any(DbAlias.class));
        order.verify(relasjonService).setRelasjoner(DNR_IDENT, GAMMEL_IDENTITET, npidPerson.getIdent(), NY_IDENTITET);
        order.verify(createPersonService).execute(requestCaptor.capture());
        order.verify(swopIdentsService).execute(DNR_IDENT, FNR_IDENT);
        order.verify(aliasRepository).save(any(DbAlias.class));
        order.verify(relasjonService).setRelasjoner(FNR_IDENT, GAMMEL_IDENTITET, DNR_IDENT, NY_IDENTITET);
        assertThat(requestCaptor.getAllValues()).extracting(PersonRequestDTO::getIdenttype)
                .containsExactly(Identtype.NPID, Identtype.DNR, Identtype.FNR);
        for (var request : requests) {
            assertThat(request.getMaster()).isEqualTo(request.getIdenttype() == IdenttypeExtended.FNR ? FREG : PDL);
        }
    }

    @Test
    void shouldPropagateCreationFailureWithoutPersistingAliasOrRelations() {
        var person = buildPerson(1L, Identtype.FNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(true).build();
        prepareMapping(person, request);
        var error = new IllegalStateException("Creation failed");
        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.error(error));

        StepVerifier.create(identtypeService.convert(person)).verifyErrorSatisfies(actual ->
                assertThat(actual).isSameAs(error));

        verifyNoInteractions(aliasRepository, relasjonService, swopIdentsService);
    }

    @Test
    void shouldPropagateAliasFailureWithoutCreatingRelations() {
        var person = buildPerson(1L, Identtype.FNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(true).build();
        prepareMapping(person, request);
        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(buildPerson(2L, Identtype.DNR)));
        var error = new IllegalStateException("Alias failed");
        when(aliasRepository.save(any(DbAlias.class))).thenReturn(Mono.error(error));

        StepVerifier.create(identtypeService.convert(person)).verifyErrorSatisfies(actual ->
                assertThat(actual).isSameAs(error));

        verifyNoInteractions(relasjonService);
    }

    @Test
    void shouldPropagateSwapFailureWithoutPersistingAliasOrRelations() {
        var person = buildPerson(1L, Identtype.DNR);
        var newPerson = buildPerson(2L, Identtype.FNR);
        var request = IdentRequestDTO.builder().identtype(IdenttypeExtended.FNR).isNew(true).build();
        prepareMapping(person, request);
        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(newPerson));
        var error = new IllegalStateException("Swap failed");
        when(swopIdentsService.execute(DNR_IDENT, FNR_IDENT)).thenReturn(Mono.error(error));

        StepVerifier.create(identtypeService.convert(person)).verifyErrorSatisfies(actual ->
                assertThat(actual).isSameAs(error));

        verifyNoInteractions(aliasRepository, relasjonService);
    }

    @Test
    void shouldStopProcessingFurtherIdentitiesWhenRelationCreationFails() {
        var person = buildPerson(1L, Identtype.FNR);
        var dnrRequest = IdentRequestDTO.builder().identtype(IdenttypeExtended.DNR).isNew(true).build();
        var fnrRequest = IdentRequestDTO.builder().identtype(IdenttypeExtended.FNR).isNew(true).build();
        prepareMapping(person, dnrRequest, fnrRequest);
        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(buildPerson(2L, Identtype.DNR)));
        when(aliasRepository.save(any(DbAlias.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        var error = new IllegalStateException("Relation failed");
        when(relasjonService.setRelasjoner(FNR_IDENT, GAMMEL_IDENTITET, DNR_IDENT, NY_IDENTITET))
                .thenReturn(Mono.error(error));

        StepVerifier.create(identtypeService.convert(person)).verifyErrorSatisfies(actual ->
                assertThat(actual).isSameAs(error));

        assertThat(capturePersonRequest().getIdenttype()).isEqualTo(Identtype.DNR);
        verify(aliasRepository).save(any(DbAlias.class));
        verifyNoInteractions(swopIdentsService);
    }

    private void prepareMapping(DbPerson person, IdentRequestDTO... requests) {
        person.getPerson().getNyident().addAll(List.of(requests));
        when(mapperFacade.mapAsList(person.getPerson().getNyident(), IdentRequestDTO.class))
                .thenReturn(new ArrayList<>(List.of(requests)));
        if (requests.length > 1 || Stream.of(requests)
                .anyMatch(request -> Boolean.TRUE.equals(request.getIsNew()) && isBlank(request.getEksisterendeIdent()))) {
            when(mapperFacade.map(any(IdenttypeExtended.class), eq(Identtype.class)))
                    .thenAnswer(invocation -> Identtype.valueOf(((IdenttypeExtended) invocation.getArgument(0)).name()));
        }
        for (var request : requests) {
            if (request.getIdenttype() == null || request.getIdenttype() == IdenttypeExtended.SYNT) {
                when(mapperFacade.map(person.getPerson().getIdenttype(), IdenttypeExtended.class))
                        .thenReturn(IdenttypeExtended.valueOf(person.getPerson().getIdenttype().name()));
            }
        }
    }

    private void prepareCreation(DbPerson person, IdentRequestDTO request) {
        prepareMapping(person, request);
        when(createPersonService.execute(any(PersonRequestDTO.class)))
                .thenReturn(Mono.just(buildPerson(2L, Identtype.DNR)));
        preparePersistence();
    }

    private void preparePersistence() {
        when(aliasRepository.save(any(DbAlias.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(relasjonService.setRelasjoner(any(String.class), eq(GAMMEL_IDENTITET),
                any(String.class), eq(NY_IDENTITET))).thenReturn(Mono.empty());
    }

    private PersonRequestDTO capturePersonRequest() {
        var captor = ArgumentCaptor.forClass(PersonRequestDTO.class);
        verify(createPersonService).execute(captor.capture());
        return captor.getValue();
    }

    private DbPerson buildPerson(Long id, Identtype identtype) {
        var ident = switch (identtype) {
            case FNR -> FNR_IDENT;
            case DNR -> DNR_IDENT;
            case NPID -> NPID_IDENT;
        };
        return DbPerson.builder().id(id).ident(ident)
                .person(PersonDTO.builder().ident(ident).identtype(identtype)
                        .kjoenn(List.of(KjoennDTO.builder().kjoenn(MANN).build()))
                        .build())
                .build();
    }
}