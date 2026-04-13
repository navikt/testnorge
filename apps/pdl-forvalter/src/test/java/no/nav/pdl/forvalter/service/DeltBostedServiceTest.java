package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeltBostedServiceTest {

    private static final String FORELDER1_IDENT = "12016512345";
    private static final String FORELDER2_IDENT = "02026512345";
    private static final String BARN_IDENT = "01012350123";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private DeltBostedService deltBostedService;

    // ---- validate: voksen (forelder) ----

    @Test
    void shouldRejectWhenVoksenHasNoPartner() {

        var person = buildVoksenPerson(FORELDER1_IDENT, List.of());

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString("må ha to foreldre"));
                });
    }

    @Test
    void shouldRejectWhenVoksenHasPartnerButNotGiftOrSamboer() {

        var person = buildVoksenPerson(FORELDER1_IDENT, List.of(
                SivilstandDTO.builder()
                        .type(Sivilstand.SKILT)
                        .relatertVedSivilstand(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("må ha to foreldre")));
    }

    @Test
    void shouldAcceptVoksenWithGiftPartner() {

        var person = buildVoksenPerson(FORELDER1_IDENT, List.of(
                SivilstandDTO.builder()
                        .type(Sivilstand.GIFT)
                        .relatertVedSivilstand(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        when(personRepository.findByIdentInOrderBySistOppdatertDesc(anyList()))
                .thenReturn(Flux.just(
                        buildDbPerson(FORELDER1_IDENT, buildVoksenPerson(FORELDER1_IDENT, List.of())),
                        buildDbPerson(FORELDER2_IDENT, buildVoksenPerson(FORELDER2_IDENT, List.of()))));

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyComplete();
    }

    @Test
    void shouldAcceptVoksenWithSamboerPartner() {

        var person = buildVoksenPerson(FORELDER1_IDENT, List.of(
                SivilstandDTO.builder()
                        .type(Sivilstand.SAMBOER)
                        .relatertVedSivilstand(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        when(personRepository.findByIdentInOrderBySistOppdatertDesc(anyList()))
                .thenReturn(Flux.just(
                        buildDbPerson(FORELDER1_IDENT, buildVoksenPerson(FORELDER1_IDENT, List.of())),
                        buildDbPerson(FORELDER2_IDENT, buildVoksenPerson(FORELDER2_IDENT, List.of()))));

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyComplete();
    }

    // ---- validate: barn ----

    @Test
    void shouldRejectWhenBarnHasOnlyOneForelder() {

        var person = buildBarnPerson(BARN_IDENT, List.of(
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.MOR)
                        .relatertPerson(FORELDER1_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("må ha to foreldre")));
    }

    @Test
    void shouldRejectWhenBarnHasNoForeldre() {

        var person = buildBarnPerson(BARN_IDENT, List.of());

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("må ha to foreldre")));
    }

    @Test
    void shouldAcceptBarnWithTwoForeldre() {

        var person = buildBarnPerson(BARN_IDENT, List.of(
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.MOR)
                        .relatertPerson(FORELDER1_IDENT)
                        .build(),
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.FAR)
                        .relatertPerson(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        when(personRepository.findByIdentInOrderBySistOppdatertDesc(anyList()))
                .thenReturn(Flux.just(
                        buildDbPerson(FORELDER1_IDENT, buildVoksenPerson(FORELDER1_IDENT, List.of())),
                        buildDbPerson(FORELDER2_IDENT, buildVoksenPerson(FORELDER2_IDENT, List.of()))));

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyComplete();
    }

    // ---- validate: adresser error ----

    @Test
    void shouldRejectWhenMultipleAdresserOnDeltBosted() {

        var person = buildBarnPerson(BARN_IDENT, List.of(
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.MOR)
                        .relatertPerson(FORELDER1_IDENT)
                        .build(),
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.FAR)
                        .relatertPerson(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .matrikkeladresse(MatrikkeladresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        when(personRepository.findByIdentInOrderBySistOppdatertDesc(anyList()))
                .thenReturn(Flux.just(
                        buildDbPerson(FORELDER1_IDENT, buildVoksenPerson(FORELDER1_IDENT, List.of())),
                        buildDbPerson(FORELDER2_IDENT, buildVoksenPerson(FORELDER2_IDENT, List.of()))));

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Foreldre må ha ulike adresser")));
    }

    @Test
    void shouldRejectWhenForeldreHaveSameAdresse() {

        var sharedVegadresse = VegadresseDTO.builder().kommunenummer("0301").adressenavn("Storgata").husnummer("1").build();

        var person = buildBarnPerson(BARN_IDENT, List.of(
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.MOR)
                        .relatertPerson(FORELDER1_IDENT)
                        .build(),
                ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(Rolle.BARN)
                        .relatertPersonsRolle(Rolle.FAR)
                        .relatertPerson(FORELDER2_IDENT)
                        .build()));

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        var forelder1Person = PersonDTO.builder()
                .ident(FORELDER1_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(
                        BostedadresseDTO.builder().vegadresse(sharedVegadresse).id(1).build(),
                        BostedadresseDTO.builder().vegadresse(sharedVegadresse).id(2).build())))
                .build();

        when(personRepository.findByIdentInOrderBySistOppdatertDesc(anyList()))
                .thenReturn(Flux.just(buildDbPerson(FORELDER1_IDENT, forelder1Person)));

        StepVerifier.create(deltBostedService.validate(deltBosted, person))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Foreldre må ha ulike adresser")));
    }

    // ---- prepAdresser ----

    @Test
    void shouldCallVegadresseServiceWhenVegadresseIsPresent() {

        var deltBosted = DeltBostedDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        var serviceResponse = new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO();
        serviceResponse.setMatrikkelId("matrikkel-123");

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(Mono.just(serviceResponse));

        StepVerifier.create(deltBostedService.prepAdresser(deltBosted))
                .verifyComplete();

        assertThat(deltBosted.getAdresseIdentifikatorFraMatrikkelen(), is(equalTo("matrikkel-123")));
        verify(adresseServiceConsumer).getVegadresse(any(VegadresseDTO.class), any());
    }

    @Test
    void shouldCallMatrikkeladresseServiceWhenMatrikkeladresseIsPresent() {

        var deltBosted = DeltBostedDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        var serviceResponse = new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO();
        serviceResponse.setMatrikkelId("matrikkel-456");

        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(Mono.just(serviceResponse));

        StepVerifier.create(deltBostedService.prepAdresser(deltBosted))
                .verifyComplete();

        assertThat(deltBosted.getAdresseIdentifikatorFraMatrikkelen(), is(equalTo("matrikkel-456")));
        verify(adresseServiceConsumer).getMatrikkeladresse(any(MatrikkeladresseDTO.class), any());
    }

    @Test
    void shouldDoNothingWhenOnlyUkjentBosted() {

        var deltBosted = DeltBostedDTO.builder()
                .ukjentBosted(UkjentBostedDTO.builder().bostedskommune("0301").build())
                .isNew(true)
                .build();

        StepVerifier.create(deltBostedService.prepAdresser(deltBosted))
                .verifyComplete();

        verify(adresseServiceConsumer, never()).getVegadresse(any(), any());
        verify(adresseServiceConsumer, never()).getMatrikkeladresse(any(), any());
    }

    @Test
    void shouldDoNothingWhenNoAdresser() {

        var deltBosted = DeltBostedDTO.builder().isNew(true).build();

        StepVerifier.create(deltBostedService.prepAdresser(deltBosted))
                .verifyComplete();

        verify(adresseServiceConsumer, never()).getVegadresse(any(), any());
        verify(adresseServiceConsumer, never()).getMatrikkeladresse(any(), any());
    }

    // ---- convert: skips non-new entries ----

    @Test
    void shouldSkipNonNewDeltBosted() {

        var deltBosted = DeltBostedDTO.builder()
                .startdatoForKontrakt(LocalDateTime.of(2020, 1, 1, 0, 0))
                .isNew(false)
                .build();

        var dbPerson = buildDbPerson(BARN_IDENT, buildBarnPerson(BARN_IDENT, List.of()));
        dbPerson.getPerson().setDeltBosted(new ArrayList<>(List.of(deltBosted)));

        StepVerifier.create(deltBostedService.convert(dbPerson))
                .assertNext(result ->
                        verify(personRepository, never()).findByIdentInOrderBySistOppdatertDesc(anyList()))
                .verifyComplete();
    }

    // ---- convert: clears deltBosted for adults ----

    @Test
    void shouldClearDeltBostedWhenPersonIsMyndig() {

        var deltBosted = DeltBostedDTO.builder()
                .isNew(false)
                .build();

        var dbPerson = buildDbPerson(FORELDER1_IDENT, buildVoksenPerson(FORELDER1_IDENT, List.of()));
        dbPerson.getPerson().setDeltBosted(new ArrayList<>(List.of(deltBosted)));

        StepVerifier.create(deltBostedService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getDeltBosted(), is(empty())))
                .verifyComplete();
    }

    // ---- handle (3-arg overload): sets startdatoForKontrakt from barn foedselsdato ----

    @Test
    void shouldSetStartdatoFromBarnFoedselsdatoWhenNull() {

        var deltBosted = DeltBostedDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        var hovedperson = buildVoksenPerson(FORELDER1_IDENT, List.of());

        var barnPerson = buildBarnPerson(BARN_IDENT, List.of());

        var barnDbPerson = buildDbPerson(BARN_IDENT, barnPerson);

        var serviceResponse = new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO();
        serviceResponse.setMatrikkelId("matrikkel-123");

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(Mono.just(serviceResponse));
        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(DeltBostedDTO.builder()
                        .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                        .build());
        when(personRepository.findByIdent(BARN_IDENT))
                .thenReturn(Mono.just(barnDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBosted, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(barnDbPerson.getPerson().getDeltBosted(), hasSize(1));
    }

    @Test
    void shouldKeepExistingStartdatoForKontrakt() {

        var startdato = LocalDateTime.of(2020, 5, 1, 0, 0);

        var deltBosted = DeltBostedDTO.builder()
                .startdatoForKontrakt(startdato)
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        var hovedperson = buildVoksenPerson(FORELDER1_IDENT, List.of());

        var barnDbPerson = buildDbPerson(BARN_IDENT, buildBarnPerson(BARN_IDENT, List.of()));

        var serviceResponse = new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO();
        serviceResponse.setMatrikkelId("matrikkel-123");

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(Mono.just(serviceResponse));
        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(DeltBostedDTO.builder()
                        .startdatoForKontrakt(startdato)
                        .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                        .build());
        when(personRepository.findByIdent(BARN_IDENT))
                .thenReturn(Mono.just(barnDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBosted, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(barnDbPerson.getPerson().getDeltBosted().getFirst().getStartdatoForKontrakt(),
                is(equalTo(startdato)));
    }

    // ---- handle (3-arg overload): partner adresse lookup ----

    @Test
    void shouldUsePartnerAdresseWhenNoAdresseOnDeltBostedAndPartnerDiffers() {

        var hovedpersonAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").adressenavn("Storgata").husnummer("1").build())
                .id(1)
                .build();
        var partnerAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0302").adressenavn("Lillegata").husnummer("5").build())
                .id(1)
                .build();

        var hovedperson = PersonDTO.builder()
                .ident(FORELDER1_IDENT)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>(List.of(hovedpersonAdresse)))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .relatertVedSivilstand(FORELDER2_IDENT)
                                .build())))
                .forelderBarnRelasjon(new ArrayList<>())
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(FORELDER2_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(partnerAdresse)))
                .build();
        var partnerDbPerson = buildDbPerson(FORELDER2_IDENT, partnerPerson);

        var barnDbPerson = buildDbPerson(BARN_IDENT, buildBarnPerson(BARN_IDENT, List.of()));

        var deltBostedInput = DeltBostedDTO.builder().isNew(true).build();
        var deltBostedMapped = DeltBostedDTO.builder().build();

        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(deltBostedMapped);
        when(personRepository.findByIdent(FORELDER2_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(personRepository.findByIdent(BARN_IDENT)).thenReturn(Mono.just(barnDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBostedInput, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(deltBostedMapped.getVegadresse(), is(notNullValue()));
        assertThat(deltBostedMapped.getVegadresse().getKommunenummer(), is(equalTo("0302")));
    }

    @Test
    void shouldNotSetPartnerAdresseWhenAdresserAreEqual() {

        var sharedAdresse = VegadresseDTO.builder().kommunenummer("0301").adressenavn("Storgata").husnummer("1").build();

        var hovedperson = PersonDTO.builder()
                .ident(FORELDER1_IDENT)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>(List.of(
                        BostedadresseDTO.builder().vegadresse(sharedAdresse).id(1).build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.GIFT)
                                .relatertVedSivilstand(FORELDER2_IDENT)
                                .build())))
                .forelderBarnRelasjon(new ArrayList<>())
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(FORELDER2_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(
                        BostedadresseDTO.builder().vegadresse(sharedAdresse).id(1).build())))
                .build();
        var partnerDbPerson = buildDbPerson(FORELDER2_IDENT, partnerPerson);

        var deltBostedInput = DeltBostedDTO.builder().isNew(true).build();
        var deltBostedMapped = DeltBostedDTO.builder().build();

        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(deltBostedMapped);
        when(personRepository.findByIdent(FORELDER2_IDENT)).thenReturn(Mono.just(partnerDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBostedInput, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(deltBostedMapped.getVegadresse(), is(nullValue()));
    }

    @Test
    void shouldNotLookUpPartnerWhenNotGiftOrSeparert() {

        var hovedperson = PersonDTO.builder()
                .ident(FORELDER1_IDENT)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>())
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.UGIFT)
                                .build())))
                .forelderBarnRelasjon(new ArrayList<>())
                .build();

        var deltBostedInput = DeltBostedDTO.builder().isNew(true).build();
        var deltBostedMapped = DeltBostedDTO.builder().build();

        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(deltBostedMapped);

        StepVerifier.create(deltBostedService.handle(deltBostedInput, hovedperson, BARN_IDENT))
                .verifyComplete();

        verify(personRepository, never()).findByIdent(anyString());
    }

    // ---- handle (3-arg overload): with explicit adresse ----

    @Test
    void shouldUseDeltBostedAdresseDirectlyWhenProvided() {

        var deltBostedInput = DeltBostedDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .isNew(true)
                .build();

        var hovedperson = buildVoksenPerson(FORELDER1_IDENT, List.of());
        var barnDbPerson = buildDbPerson(BARN_IDENT, buildBarnPerson(BARN_IDENT, List.of()));

        var mappedDeltBosted = DeltBostedDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .build();

        var serviceResponse = new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO();
        serviceResponse.setMatrikkelId("matrikkel-789");

        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(mappedDeltBosted);
        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(Mono.just(serviceResponse));
        when(personRepository.findByIdent(BARN_IDENT))
                .thenReturn(Mono.just(barnDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBostedInput, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(barnDbPerson.getPerson().getDeltBosted(), hasSize(1));
        verify(personRepository, never()).findByIdent(FORELDER2_IDENT);
    }

    // ---- handle (3-arg overload): separert partner also triggers lookup ----

    @Test
    void shouldLookUpPartnerAdresseForSeparertSivilstand() {

        var partnerAdresse = BostedadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder().kommunenummer("0302").build())
                .id(1)
                .build();

        var hovedperson = PersonDTO.builder()
                .ident(FORELDER1_IDENT)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>(List.of(
                        BostedadresseDTO.builder()
                                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                                .id(1)
                                .build())))
                .sivilstand(new ArrayList<>(List.of(
                        SivilstandDTO.builder()
                                .type(Sivilstand.SEPARERT)
                                .relatertVedSivilstand(FORELDER2_IDENT)
                                .build())))
                .forelderBarnRelasjon(new ArrayList<>())
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(FORELDER2_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(partnerAdresse)))
                .build();
        var partnerDbPerson = buildDbPerson(FORELDER2_IDENT, partnerPerson);

        var barnDbPerson = buildDbPerson(BARN_IDENT, buildBarnPerson(BARN_IDENT, List.of()));

        var deltBostedInput = DeltBostedDTO.builder().isNew(true).build();
        var deltBostedMapped = DeltBostedDTO.builder().build();

        when(mapperFacade.map(any(DeltBostedDTO.class), eq(DeltBostedDTO.class)))
                .thenReturn(deltBostedMapped);
        when(personRepository.findByIdent(FORELDER2_IDENT)).thenReturn(Mono.just(partnerDbPerson));
        when(personRepository.findByIdent(BARN_IDENT)).thenReturn(Mono.just(barnDbPerson));

        StepVerifier.create(deltBostedService.handle(deltBostedInput, hovedperson, BARN_IDENT))
                .verifyComplete();

        assertThat(deltBostedMapped.getMatrikkeladresse(), is(notNullValue()));
        assertThat(deltBostedMapped.getMatrikkeladresse().getKommunenummer(), is(equalTo("0302")));
    }

    // ---- helpers ----

    private PersonDTO buildVoksenPerson(String ident, List<SivilstandDTO> sivilstand) {
        return PersonDTO.builder()
                .ident(ident)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>())
                .sivilstand(new ArrayList<>(sivilstand))
                .forelderBarnRelasjon(new ArrayList<>())
                .deltBosted(new ArrayList<>())
                .build();
    }

    private PersonDTO buildBarnPerson(String ident, List<ForelderBarnRelasjonDTO> relasjoner) {
        return PersonDTO.builder()
                .ident(ident)
                .foedselsdato(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .bostedsadresse(new ArrayList<>())
                .sivilstand(new ArrayList<>())
                .forelderBarnRelasjon(new ArrayList<>(relasjoner))
                .deltBosted(new ArrayList<>())
                .build();
    }

    private DbPerson buildDbPerson(String ident, PersonDTO person) {
        return DbPerson.builder()
                .id((long) ident.hashCode())
                .ident(ident)
                .person(person)
                .build();
    }
}
