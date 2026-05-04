package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand.defaultAdresse;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.ENKE_ELLER_ENKEMANN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.REGISTRERT_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SAMBOER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SEPARERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SKILT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SKILT_PARTNER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UGIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.UOPPGITT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SivilstandServiceTest {

    private static final String IDENT = "12315678901";
    private static final String TESTNORGE_IDENT = "12895678901";
    private static final String PARTNER_IDENT = "02026512345";
    private static final String NY_PARTNER_IDENT = "05036543210";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CreatePersonService createPersonService;

    @Mock
    private RelasjonService relasjonService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private SivilstandService sivilstandService;

    // ---- validate: relatert person finnes ikke ----

    @Test
    void shouldRejectGiftWithNonExistingRelatertPerson() {

        var request = SivilstandDTO.builder()
                .type(GIFT)
                .sivilstandsdato(LocalDateTime.now())
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(PARTNER_IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString("Sivilstand: Relatert person finnes ikke"));
                });
    }

    @Test
    void shouldRejectSeparertWithNonExistingRelatertPerson() {

        var request = SivilstandDTO.builder()
                .type(SEPARERT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(PARTNER_IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Relatert person finnes ikke")));
    }

    @Test
    void shouldRejectSamboerWithNonExistingRelatertPerson() {

        var request = SivilstandDTO.builder()
                .type(SAMBOER)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(PARTNER_IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Relatert person finnes ikke")));
    }

    @Test
    void shouldRejectRegistrertPartnerWithNonExistingRelatertPerson() {

        var request = SivilstandDTO.builder()
                .type(REGISTRERT_PARTNER)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(PARTNER_IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Relatert person finnes ikke")));
    }

    // ---- validate: relatert person finnes ----

    @Test
    void shouldAcceptGiftWithExistingRelatertPerson() {

        var request = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(PARTNER_IDENT)).thenReturn(Mono.just(true));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyComplete();
    }

    // ---- validate: skip validation for certain types ----

    @Test
    void shouldSkipValidationForSkilt() {

        var request = SivilstandDTO.builder()
                .type(SKILT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    @Test
    void shouldSkipValidationForUgift() {

        var request = SivilstandDTO.builder()
                .type(UGIFT)
                .isNew(true)
                .build();

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    @Test
    void shouldSkipValidationForEnkeEllerEnkemann() {

        var request = SivilstandDTO.builder()
                .type(ENKE_ELLER_ENKEMANN)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    // ---- validate: skip validation for blank relatert ----

    @Test
    void shouldSkipValidationWhenRelatertVedSivilstandIsBlank() {

        var request = SivilstandDTO.builder()
                .type(GIFT)
                .isNew(true)
                .build();

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(IDENT).build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    // ---- validate: skip validation for testnorge ident ----

    @Test
    void shouldSkipValidationForTestnorgeIdent() {

        var request = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .isNew(true)
                .build();

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder().ident(TESTNORGE_IDENT).build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    // ---- enforceIntegrity: sorting ----

    @Test
    void shouldSortSivilstandByDatoDescending() {

        var giftDato = LocalDateTime.now().minusYears(3);
        var skiltDato = LocalDateTime.now();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(GIFT).sivilstandsdato(giftDato).id(1).build(),
                        SivilstandDTO.builder().type(SKILT).sivilstandsdato(skiltDato).id(2).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        assertThat(result.get(0).getSivilstandsdato(), is(equalTo(skiltDato)));
        assertThat(result.get(1).getSivilstandsdato(), is(equalTo(giftDato)));
    }

    @Test
    void shouldNotSortWhenAnySivilstandsdatoIsNull() {

        var giftDato = LocalDateTime.now().minusYears(3);

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(GIFT).sivilstandsdato(giftDato).id(1).build(),
                        SivilstandDTO.builder().type(SKILT).id(2).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        assertThat(result.get(0).getType(), is(equalTo(GIFT)));
        assertThat(result.get(1).getType(), is(equalTo(SKILT)));
    }

    // ---- enforceIntegrity: ugift dato setting ----

    @Test
    void shouldSetUgiftDatoToFoedselsdatoWhenNoEarlierSivilstand() {

        var foedselsdato = LocalDate.of(1990, 6, 15).atStartOfDay();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(foedselsdato)
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(UGIFT).id(1).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        assertThat(result.getFirst().getSivilstandsdato(), is(equalTo(foedselsdato)));
    }

    @Test
    void shouldSetUgiftDatoToThreeMonthsBeforeEarliestWhenBeforeMyndighetsalder() {

        var foedselsdato = LocalDate.of(2010, 6, 15).atStartOfDay();
        var giftDato = LocalDate.of(2020, 1, 1).atStartOfDay();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(foedselsdato)
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(UGIFT).id(1).build(),
                        SivilstandDTO.builder().type(GIFT).sivilstandsdato(giftDato).id(2).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        var ugiftResult = result.stream().filter(SivilstandDTO::isUgift).findFirst().orElseThrow();
        assertThat(ugiftResult.getSivilstandsdato(), is(equalTo(giftDato.minusMonths(3))));
    }

    @Test
    void shouldSetUgiftDatoToFoedselsdatoWhenEarliestSivilstandIsAfterMyndighetsdato() {

        var foedselsdato = LocalDate.of(1980, 1, 1).atStartOfDay();
        var giftDato = LocalDate.of(2005, 6, 1).atStartOfDay();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(foedselsdato)
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(UGIFT).id(1).build(),
                        SivilstandDTO.builder().type(GIFT).sivilstandsdato(giftDato).id(2).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        var ugiftResult = result.stream().filter(SivilstandDTO::isUgift).findFirst().orElseThrow();
        assertThat(ugiftResult.getSivilstandsdato(), is(equalTo(foedselsdato)));
    }

    @Test
    void shouldNotChangeUgiftDatoWhenAlreadySet() {

        var ugiftDato = LocalDate.of(1990, 1, 1).atStartOfDay();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                        .foedselsdato(LocalDate.of(1990, 6, 15).atStartOfDay())
                        .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder().type(UGIFT).sivilstandsdato(ugiftDato).id(1).build())))
                .build();

        var result = sivilstandService.enforceIntegrity(person);

        assertThat(result.getFirst().getSivilstandsdato(), is(equalTo(ugiftDato)));
    }

    // ---- convert: sorting via enforceIntegrity ----

    @Test
    void shouldSortSivilstandOnConvert() {

        var skiltDato = LocalDateTime.now();
        var giftDato = LocalDateTime.now().minusYears(3);

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder().type(GIFT).sivilstandsdato(giftDato).id(1).build(),
                                SivilstandDTO.builder().type(SKILT).sivilstandsdato(skiltDato).id(2).build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getSivilstand().get(0).getSivilstandsdato(), is(equalTo(skiltDato)));
                    assertThat(result.getPerson().getSivilstand().get(1).getSivilstandsdato(), is(equalTo(giftDato)));
                })
                .verifyComplete();
    }

    // ---- convert: skips non-new ----

    @Test
    void shouldSkipNonNewSivilstand() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(LocalDateTime.now())
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .isNew(false)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    verify(createPersonService, never()).execute(any());
                    verify(relasjonService, never()).setRelasjoner(anyString(), any(), anyString(), any());
                })
                .verifyComplete();
    }

    // ---- handle: null type defaults to UGIFT ----

    @Test
    void shouldDefaultToUgiftWhenTypeIsNull() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().getType(), is(equalTo(UGIFT))))
                .verifyComplete();
    }

    // ---- handle: non-gift types clear relatertVedSivilstand ----

    @Test
    void shouldClearRelatertVedSivilstandForSkilt() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(SKILT)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().getRelatertVedSivilstand(), is(nullValue())))
                .verifyComplete();
    }

    @Test
    void shouldClearRelatertVedSivilstandForEnkeEllerEnkemann() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(ENKE_ELLER_ENKEMANN)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().getRelatertVedSivilstand(), is(nullValue())))
                .verifyComplete();
    }

    @Test
    void shouldClearRelatertVedSivilstandForUoppgitt() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(UOPPGITT)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().getRelatertVedSivilstand(), is(nullValue())))
                .verifyComplete();
    }

    // ---- handle: gift with existing relatert person ----

    @Test
    void shouldSetEksisterendePersonTrueWhenRelatertIdentIsPresent() {

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(PARTNER_IDENT)
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(PARTNER_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getSivilstand().getFirst().isEksisterendePerson(), is(true));
                    verify(relasjonService).setRelasjoner(eq(IDENT), any(), eq(PARTNER_IDENT), any());
                })
                .verifyComplete();
    }

    // ---- handle: gift creates relatert sivilstand on partner ----

    @Test
    void shouldCreateRelatertSivilstandOnPartner() {

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(PARTNER_IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1981, 3, 15).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder().type(UGIFT).sivilstandsdato(LocalDate.of(1981, 3, 15).atStartOfDay()).id(1).build())))
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                .id(2)
                .build();

        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(PARTNER_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.just(partnerDbPerson));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(partnerDbPerson.getPerson().getSivilstand(), hasSize(2));
                    verify(personRepository).save(partnerDbPerson);
                })
                .verifyComplete();
    }

    // ---- handle: gift without relatert creates new person ----

    @Test
    void shouldCreateNewPersonWhenRelatertIsBlank() {

        var createdPartner = DbPerson.builder()
                .ident(NY_PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(NY_PARTNER_IDENT)
                        .bostedsadresse(new ArrayList<>())
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(createdPartner));
        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(NY_PARTNER_IDENT)).thenReturn(Mono.just(createdPartner));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .kjoenn(new ArrayList<>(List.of(KjoennDTO.builder().kjoenn(KjoennDTO.Kjoenn.MANN).build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    verify(createPersonService).execute(any(PersonRequestDTO.class));
                    assertThat(result.getPerson().getSivilstand().getFirst().getRelatertVedSivilstand(),
                            is(equalTo(NY_PARTNER_IDENT)));
                    assertThat(result.getPerson().getSivilstand().getFirst().getNyRelatertPerson(), is(nullValue()));
                    assertThat(result.getPerson().getSivilstand().getFirst().getBorIkkeSammen(), is(nullValue()));
                })
                .verifyComplete();
    }

    // ---- handle: gift creates shared adresse when borIkkeSammen is not set ----

    @Test
    void shouldCopyAdresseToPartnerWhenBorIkkeSammenIsNull() {

        var hovedpersonAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").adressenavn("Storgata").build())
                .id(1)
                .build();

        var mappedAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").adressenavn("Storgata").build())
                .build();

        var createdPartner = DbPerson.builder()
                .ident(NY_PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(NY_PARTNER_IDENT)
                        .bostedsadresse(new ArrayList<>())
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(createdPartner));
        when(mapperFacade.map(any(BostedadresseDTO.class), eq(BostedadresseDTO.class))).thenReturn(mappedAdresse);
        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(NY_PARTNER_IDENT)).thenReturn(Mono.just(createdPartner));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(mapperFacade.map(defaultAdresse(), VegadresseDTO.class)).thenReturn(new VegadresseDTO());

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .kjoenn(new ArrayList<>(List.of(KjoennDTO.builder().kjoenn(KjoennDTO.Kjoenn.MANN).build())))
                        .bostedsadresse(new ArrayList<>(List.of(hovedpersonAdresse)))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(createdPartner.getPerson().getBostedsadresse(), hasSize(1));
                    verify(personRepository, times(2)).save(createdPartner);
                })
                .verifyComplete();
    }

    // ---- handle: borIkkeSammen prevents address copying ----

    @Test
    void shouldNotCopyAdresseWhenBorIkkeSammenIsTrue() {

        var createdPartner = DbPerson.builder()
                .ident(NY_PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(NY_PARTNER_IDENT)
                        .bostedsadresse(new ArrayList<>())
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(createdPartner));
        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(NY_PARTNER_IDENT)).thenReturn(Mono.just(createdPartner));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.just(createdPartner));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .kjoenn(new ArrayList<>(List.of(KjoennDTO.builder().kjoenn(KjoennDTO.Kjoenn.MANN).build())))
                        .bostedsadresse(new ArrayList<>(List.of(
                                BostedadresseDTO.builder()
                                        .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                                        .id(1)
                                        .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .borIkkeSammen(true)
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(createdPartner.getPerson().getBostedsadresse(), hasSize(0)))
                .verifyComplete();
    }

    // ---- handle: samboer ----

    @Test
    void shouldHandleSamboerSameAsGift() {

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(PARTNER_IDENT)
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(SAMBOER)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(PARTNER_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(SAMBOER)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .sivilstandsdato(LocalDateTime.of(2015, 1, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getSivilstand().getFirst().isEksisterendePerson(), is(true));
                    verify(relasjonService).setRelasjoner(eq(IDENT), any(), eq(PARTNER_IDENT), any());
                })
                .verifyComplete();
    }

    // ---- handle: separert triggers relasjon ----

    @Test
    void shouldHandleSeparertSameAsGift() {

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(PARTNER_IDENT)
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(SEPARERT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(PARTNER_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.just(partnerDbPerson));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(SEPARERT)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .sivilstandsdato(LocalDateTime.of(2020, 1, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        verify(relasjonService).setRelasjoner(eq(IDENT), any(), eq(PARTNER_IDENT), any()))
                .verifyComplete();
    }

    // ---- handle: separert_partner also triggers relasjon ----

    @Test
    void shouldNotTriggerRelasjonForSkiltPartner() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(SKILT_PARTNER)
                                        .relatertVedSivilstand(PARTNER_IDENT)
                                        .sivilstandsdato(LocalDateTime.of(2020, 1, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().getRelatertVedSivilstand(), is(nullValue())))
                .verifyComplete();
    }

    // ---- handle: eksisterendePerson flag ----

    @Test
    void shouldSetEksisterendePersonFalseWhenRelatertIsBlank() {

        var createdPartner = DbPerson.builder()
                .ident(NY_PARTNER_IDENT)
                .person(PersonDTO.builder()
                        .ident(NY_PARTNER_IDENT)
                        .bostedsadresse(new ArrayList<>())
                        .sivilstand(new ArrayList<>())
                        .build())
                .build();

        var mappedSivilstand = SivilstandDTO.builder()
                .type(GIFT)
                .relatertVedSivilstand(IDENT)
                .id(1)
                .build();

        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(createdPartner));
        when(relasjonService.setRelasjoner(anyString(), any(), anyString(), any())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(NY_PARTNER_IDENT)).thenReturn(Mono.just(createdPartner));
        when(mapperFacade.map(any(SivilstandDTO.class), eq(SivilstandDTO.class))).thenReturn(mappedSivilstand);
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.just(createdPartner));

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1980, 1, 1).atStartOfDay())
                                .build())))
                        .kjoenn(new ArrayList<>(List.of(KjoennDTO.builder().kjoenn(KjoennDTO.Kjoenn.KVINNE).build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(LocalDateTime.of(2010, 6, 1, 0, 0))
                                        .isNew(true)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getSivilstand().getFirst().isEksisterendePerson(), is(false)))
                .verifyComplete();
    }

    // ---- convert: returns same DbPerson instance ----

    @Test
    void shouldReturnSameDbPersonInstance() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(new ArrayList<>(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build())))
                        .sivilstand(new ArrayList<>(List.of(
                                SivilstandDTO.builder().type(UGIFT).id(1).build())))
                        .build())
                .build();

        when(personRepository.save(any(DbPerson.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(sivilstandService.convert(dbPerson))
                .assertNext(result -> assertThat(result, is(dbPerson)))
                .verifyComplete();
    }
}