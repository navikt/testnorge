package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType.FORELDREANSVAR_FORELDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForeldreansvarServiceTest {

    private static final String IDENT_HOVEDPERSON = "23510001234";
    private static final String IDENT_ANDRE = "12435678901";
    private static final String ETTERNAVN = "TUBA";
    private static final String IDENT_BARN = "15031250012";
    private static final String IDENT_MOR = "12028000234";
    private static final String IDENT_FAR = "13028000134";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @Mock
    private CreatePersonService createPersonService;

    @Mock
    private RelasjonService relasjonService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private ForeldreansvarService foreldreansvarService;

    @Test
    void whenAnsvarIsMissing_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, new PersonDTO()))
                .verifyErrorSatisfies(throwable ->

                        assertThat(throwable.getMessage(), containsString("Forelderansvar: hvem som har ansvar må oppgis")));
    }

    @Test
    void whenAmbiguousAnsvarlig_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .ansvarligUtenIdentifikator(new RelatertBiPersonDTO())
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, new PersonDTO()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Forelderansvar: kun et av feltene 'ansvarlig' og " +
                                                                          "'ansvarligUtenIdentifikator' kan benyttes")));
    }

    @Test
    void whenAnsvarligPersonDontExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT_ANDRE)).thenReturn(Mono.just(false));

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, new PersonDTO()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString(String.format("Foreldreansvar: Ansvarlig person %s finnes ikke", IDENT_ANDRE))));
    }

    @Test
    void whenNonExistingNavnStated_thenThrowExecption() {

        var personnavn = NavnDTO.builder()
                .substantiv(ETTERNAVN)
                .build();

        when(genererNavnServiceConsumer.verifyNavn(personnavn)).thenReturn(Mono.just(false));

        StepVerifier.create(foreldreansvarService.validate(ForeldreansvarDTO.builder()
                        .ansvar(Ansvar.ANDRE)
                        .ansvarligUtenIdentifikator(RelatertBiPersonDTO.builder()
                                .navn(PersonnavnDTO.builder()
                                        .etternavn(ETTERNAVN)
                                        .build())
                                .build())
                        .isNew(true)
                        .build(), new PersonDTO()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Foreldreansvar: Navn er ikke i liste over gyldige verdier")));
    }

    @Test
    void whenNonExistingRelatedMorStated_thenThrowExecption() {

        var personnavn = NavnDTO.builder()
                .substantiv(ETTERNAVN)
                .build();

        when(genererNavnServiceConsumer.verifyNavn(personnavn)).thenReturn(Mono.just(false));

        StepVerifier.create(foreldreansvarService.validate(ForeldreansvarDTO.builder()
                        .ansvar(Ansvar.MOR)
                        .ansvarligUtenIdentifikator(RelatertBiPersonDTO.builder()
                                .navn(PersonnavnDTO.builder()
                                        .etternavn(ETTERNAVN)
                                        .build())
                                .build())
                        .isNew(true)
                        .build(), PersonDTO.builder()
                        .ident(IDENT_HOVEDPERSON)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Foreldreansvar: Navn er ikke i liste over gyldige verdier")));
    }

    @Test
    void whenAnsvarIsMorAndForeldreBarnRelationExcludesMor_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til mor ikke funnet")));
    }

    @Test
    void whenAnsvarIsFarAndForeldreBarnRelationExcludesFar_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FAR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, PersonDTO.builder()
                        .ident(IDENT_HOVEDPERSON)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til far ikke funnet")));
    }

    @Test
    void whenAnsvarIsFellesAndForeldreBarnRelationExcludesMorOrFar_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FELLES)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, PersonDTO.builder()
                        .ident(IDENT_HOVEDPERSON)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til mor og/eller far ikke funnet")));
    }

    // --- validate() happy paths ---

    @Test
    void whenAnsvarIsAndreWithExistingAnsvarlig_thenCompletes() {

        when(personRepository.existsByIdent(IDENT_ANDRE)).thenReturn(Mono.just(true));

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request,
                        PersonDTO.builder().ident(IDENT_HOVEDPERSON).build()))
                .verifyComplete();
    }

    @Test
    void whenAnsvarIsMorAndRelasjonMorExists_thenCompletes() {

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.MOR)
                                .relatertPerson(IDENT_BARN)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, person))
                .verifyComplete();
    }

    @Test
    void whenAnsvarIsFarAndRelasjonFarExists_thenCompletes() {

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.FAR)
                                .relatertPerson(IDENT_BARN)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FAR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, person))
                .verifyComplete();
    }

    @Test
    void whenAnsvarIsFellesAndRelasjonForeldreExists_thenCompletes() {

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.MOR)
                                .relatertPerson(IDENT_BARN)
                                .build()))
                .sivilstand(List.of(
                        SivilstandDTO.builder()
                                .type(SivilstandDTO.Sivilstand.GIFT)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FELLES)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, person))
                .verifyComplete();
    }

    @Test
    void whenAnsvarIsMorWithExplicitAnsvarlig_thenSkipsRelasjonCheck() {

        when(personRepository.existsByIdent(IDENT_ANDRE)).thenReturn(Mono.just(true));

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .ansvarlig(IDENT_ANDRE)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request,
                        PersonDTO.builder().ident(IDENT_HOVEDPERSON).build()))
                .verifyComplete();
    }

    @Test
    void whenMedmorWithoutRelasjon_thenThrowsException() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MEDMOR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request,
                        PersonDTO.builder().ident(IDENT_HOVEDPERSON).build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("barnets foreldrerelasjon til mor ikke funnet")));
    }

    // --- validate() barn branch ---

    @Test
    void whenBarnAnsvarMorWithRelasjon_thenCompletes() {

        var barn = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .relatertPerson(IDENT_MOR)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, barn))
                .verifyComplete();
    }

    @Test
    void whenBarnAnsvarFellesWithBothParents_thenCompletes() {

        var barn = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .relatertPerson(IDENT_MOR)
                                .build(),
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.FAR)
                                .relatertPerson(IDENT_FAR)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FELLES)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, barn))
                .verifyComplete();
    }

    @Test
    void whenBarnAnsvarMorWithoutRelasjon_thenThrowsException() {

        var barn = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, barn))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("barnets foreldrerelasjon til mor ikke funnet")));
    }

    @Test
    void whenBarnAnsvarFellesWithoutBothParents_thenThrowsException() {

        var barn = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .relatertPerson(IDENT_MOR)
                                .build()))
                .build();

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FELLES)
                .isNew(true)
                .build();

        StepVerifier.create(foreldreansvarService.validate(request, barn))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("barnets foreldrerelasjon til mor og/eller far ikke funnet")));
    }

    // --- convert() paths ---

    @Test
    void whenForeldreansvarIsNew_thenSetsKildeAndMaster() {

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .isNew(true)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .build();
        person.getForeldreansvar().add(foreldreansvar);

        var dbPerson = DbPerson.builder()
                .ident(IDENT_HOVEDPERSON)
                .person(person)
                .build();

        when(mapperFacade.mapAsList(anyList(), eq(ForeldreansvarDTO.class)))
                .thenAnswer(inv -> new ArrayList<>(inv.<List<ForeldreansvarDTO>>getArgument(0)));

        StepVerifier.create(foreldreansvarService.convert(dbPerson))
                .assertNext(result -> {
                    var ansvar = result.getPerson().getForeldreansvar().get(0);
                    assertThat(ansvar.getKilde(), is("Dolly"));
                    assertThat(ansvar.getMaster(), is(DbVersjonDTO.Master.FREG));
                })
                .verifyComplete();
    }

    @Test
    void whenForeldreansvarIsNotNew_thenSkipsProcessing() {

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .isNew(false)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .build();
        person.getForeldreansvar().add(foreldreansvar);

        var dbPerson = DbPerson.builder()
                .ident(IDENT_HOVEDPERSON)
                .person(person)
                .build();

        when(mapperFacade.mapAsList(anyList(), eq(ForeldreansvarDTO.class)))
                .thenAnswer(inv -> new ArrayList<>(inv.<List<ForeldreansvarDTO>>getArgument(0)));

        StepVerifier.create(foreldreansvarService.convert(dbPerson))
                .assertNext(result -> {
                    var ansvar = result.getPerson().getForeldreansvar().get(0);
                    assertThat(ansvar.getKilde(), is(nullValue()));
                })
                .verifyComplete();
    }

    // --- handle() adult paths ---

    @Test
    void whenAdultWithAnsvarssubjekt_thenCreatesRelasjonOnBarn() {

        var barnPerson = PersonDTO.builder()
                .ident(IDENT_BARN)
                .build();
        var barnDbPerson = DbPerson.builder()
                .ident(IDENT_BARN)
                .person(barnPerson)
                .build();

        when(personRepository.findByIdent(IDENT_BARN)).thenReturn(Mono.just(barnDbPerson));
        when(relasjonService.setRelasjoner(IDENT_BARN, FORELDREANSVAR_FORELDER,
                IDENT_HOVEDPERSON, FORELDREANSVAR_BARN)).thenReturn(Mono.empty());

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .ansvarssubjekt(IDENT_BARN)
                .kilde("Dolly")
                .master(DbVersjonDTO.Master.FREG)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .build();

        StepVerifier.create(foreldreansvarService.handle(foreldreansvar, person))
                .assertNext(result -> assertThat(result.getAnsvar(), is(Ansvar.ANDRE)))
                .verifyComplete();

        assertThat(barnPerson.getForeldreansvar(), hasSize(1));
        assertThat(barnPerson.getForeldreansvar().get(0).getAnsvarlig(), is(IDENT_HOVEDPERSON));
    }

    @Test
    void whenAdultMorAnsvar_thenResolvesBarnMorRelasjoner() {

        var childPerson = PersonDTO.builder()
                .ident(IDENT_BARN)
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .relatertPerson(IDENT_HOVEDPERSON)
                                .build()))
                .build();
        var childDbPerson = DbPerson.builder()
                .ident(IDENT_BARN)
                .person(childPerson)
                .build();

        when(personRepository.findByIdent(IDENT_BARN)).thenReturn(Mono.just(childDbPerson));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonService.setRelasjoner(IDENT_BARN, FORELDREANSVAR_FORELDER,
                IDENT_HOVEDPERSON, FORELDREANSVAR_BARN)).thenReturn(Mono.empty());

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.MOR)
                .kilde("Dolly")
                .master(DbVersjonDTO.Master.FREG)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.MOR)
                                .relatertPerson(IDENT_BARN)
                                .relatertPersonsRolle(Rolle.BARN)
                                .build()))
                .build();

        StepVerifier.create(foreldreansvarService.handle(foreldreansvar, person))
                .assertNext(result -> assertThat(result.getAnsvarssubjekt(), is(IDENT_BARN)))
                .verifyComplete();

        assertThat(childPerson.getForeldreansvar(), hasSize(1));
    }

    @Test
    void whenAdultAndreWithoutAnsvarlig_thenCreatesNewPerson() {

        var createdDbPerson = DbPerson.builder()
                .ident("10019800123")
                .person(new PersonDTO())
                .build();

        when(createPersonService.execute(any())).thenReturn(Mono.just(createdDbPerson));

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.ANDRE)
                .kilde("Dolly")
                .master(DbVersjonDTO.Master.FREG)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT_HOVEDPERSON)
                .build();

        StepVerifier.create(foreldreansvarService.handle(foreldreansvar, person))
                .assertNext(result -> {
                    assertThat(result.getAnsvarlig(), is("10019800123"));
                    assertThat(result.getEksisterendePerson(), is(true));
                })
                .verifyComplete();
    }

    // --- handle() barn paths ---

    @Test
    void whenBarnWithExplicitAnsvarlig_thenSetsRelasjon() {

        var morPerson = PersonDTO.builder()
                .ident(IDENT_MOR)
                .build();
        var morDbPerson = DbPerson.builder()
                .ident(IDENT_MOR)
                .person(morPerson)
                .build();

        when(relasjonService.setRelasjoner(IDENT_BARN, FORELDREANSVAR_FORELDER,
                IDENT_MOR, FORELDREANSVAR_BARN)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(IDENT_MOR)).thenReturn(Mono.just(morDbPerson));
        when(mapperFacade.map(any(ForeldreansvarDTO.class), eq(ForeldreansvarDTO.class)))
                .thenAnswer(inv -> {
                    var original = inv.<ForeldreansvarDTO>getArgument(0);
                    return ForeldreansvarDTO.builder()
                            .ansvar(original.getAnsvar())
                            .kilde(original.getKilde())
                            .master(original.getMaster())
                            .gyldigFraOgMed(original.getGyldigFraOgMed())
                            .gyldigTilOgMed(original.getGyldigTilOgMed())
                            .build();
                });
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.MOR)
                .ansvarlig(IDENT_MOR)
                .kilde("Dolly")
                .master(DbVersjonDTO.Master.FREG)
                .build();

        var barnPerson = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .build();

        StepVerifier.create(foreldreansvarService.handle(foreldreansvar, barnPerson))
                .verifyComplete();

        assertThat(morPerson.getForeldreansvar(), hasSize(1));
        assertThat(morPerson.getForeldreansvar().get(0).getAnsvarssubjekt(), is(IDENT_BARN));
    }

    @Test
    void whenBarnMorAnsvar_thenResolvesFromRelasjon() {

        var morPerson = PersonDTO.builder()
                .ident(IDENT_MOR)
                .build();
        var morDbPerson = DbPerson.builder()
                .ident(IDENT_MOR)
                .person(morPerson)
                .build();

        when(relasjonService.setRelasjoner(IDENT_BARN, FORELDREANSVAR_FORELDER,
                IDENT_MOR, FORELDREANSVAR_BARN)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(IDENT_MOR)).thenReturn(Mono.just(morDbPerson));
        when(mapperFacade.map(any(ForeldreansvarDTO.class), eq(ForeldreansvarDTO.class)))
                .thenAnswer(inv -> {
                    var original = inv.<ForeldreansvarDTO>getArgument(0);
                    return ForeldreansvarDTO.builder()
                            .ansvar(original.getAnsvar())
                            .kilde(original.getKilde())
                            .master(original.getMaster())
                            .gyldigFraOgMed(original.getGyldigFraOgMed())
                            .gyldigTilOgMed(original.getGyldigTilOgMed())
                            .build();
                });
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        var foreldreansvar = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(Ansvar.MOR)
                .kilde("Dolly")
                .master(DbVersjonDTO.Master.FREG)
                .build();

        var barnPerson = PersonDTO.builder()
                .ident(IDENT_BARN)
                .foedselsdato(List.of(FoedselsdatoDTO.builder()
                        .foedselsaar(LocalDateTime.now().getYear() - 5)
                        .build()))
                .forelderBarnRelasjon(List.of(
                        ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(Rolle.BARN)
                                .relatertPersonsRolle(Rolle.MOR)
                                .relatertPerson(IDENT_MOR)
                                .build()))
                .build();

        StepVerifier.create(foreldreansvarService.handle(foreldreansvar, barnPerson))
                .verifyComplete();

        assertThat(foreldreansvar.getAnsvarlig(), is(IDENT_MOR));
        assertThat(morPerson.getForeldreansvar(), hasSize(1));
        assertThat(morPerson.getForeldreansvar().get(0).getAnsvarssubjekt(), is(IDENT_BARN));
    }
}