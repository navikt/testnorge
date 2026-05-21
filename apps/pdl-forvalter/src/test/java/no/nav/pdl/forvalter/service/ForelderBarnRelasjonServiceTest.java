package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
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

import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MEDMOR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForelderBarnRelasjonServiceTest {

    private static final String IDENT = "12345678901";
    private static final String TESTNORGE_IDENT = "12895678901";
    private static final String RELATERT_IDENT = "02026512345";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private ForelderBarnRelasjonService forelderBarnRelasjonService;

    @Test
    void whenMinRolleForPersonIsMissing_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("ForelderBarnRelasjon: min rolle for person må oppgis")));
    }

    @Test
    void whenRelatertRolleForPersonIsMissing_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FAR)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("ForelderBarnRelasjon: relatert persons rolle må oppgis")));
    }

    @Test
    void whenAmbiguousRollerForelderIsGiven_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(FAR)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("ForelderBarnRelasjon: min rolle og relatert persons rolle " +
                                                                          "må være av type barn -- forelder, eller forelder -- barn")));
    }

    @Test
    void whenAmbiguousRollerBarnIsGiven_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(BARN)
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("ForelderBarnRelasjon: min rolle og relatert persons rolle " +
                                                                          "må være av type barn -- forelder, eller forelder -- barn")));
    }

    @Test
    void whenNonExistingPersonStated_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(BARN)
                .relatertPerson(IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString(String.format("ForelderBarnRelasjon: Relatert person %s finnes ikke", IDENT))));
    }

    @Test
    void whenAmbiguosAdresserExist_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FORELDER)
                .relatertPersonsRolle(BARN)
                .deltBosted(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build())
                .build();

        StepVerifier.create(
                        forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                                .ident(IDENT)
                                .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("Delt bosted: kun én adresse skal være satt (vegadresse, ukjentBosted, matrikkeladresse)")));
    }

    @Test
    void whenDualIdentifikatorExists_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FORELDER)
                .relatertPersonsRolle(BARN)
                .relatertPerson(IDENT)
                .relatertPersonUtenFolkeregisteridentifikator(new RelatertBiPersonDTO())
                .build();

        StepVerifier.create(
                        forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                                .ident(IDENT)
                                .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("ForelderBarnRelasjon: Relatert person skal finnes med eller uten ident, " +
                                              "ikke begge deler")));
    }


    @Test
    void whenSortering() {

        var request = List.of(ForelderBarnRelasjonDTO.builder()
                        .id(1)
                        .build(),
                ForelderBarnRelasjonDTO.builder()
                        .id(2)
                        .build());

        for (int i = 0; i < request.size(); i++) {
            request.get(i).setId(request.size() - i);
        }
        assertThat(request.getFirst().getId(), is(equalTo(2)));
        assertThat(request.get(1).getId(), is(equalTo(1)));
    }

    // ---- validate: happy path ----

    @Test
    void shouldValidateSuccessfullyForMorBarnRelasjon() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    @Test
    void shouldValidateSuccessfullyForBarnFarRelasjon() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(BARN)
                .relatertPersonsRolle(FAR)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    @Test
    void shouldValidateSuccessfullyForForelderBarnRelasjon() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FORELDER)
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    @Test
    void shouldValidateSuccessfullyForBarnMedmorRelasjon() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(BARN)
                .relatertPersonsRolle(MEDMOR)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    // ---- validate: existing relatert person passes ----

    @Test
    void shouldValidateSuccessfullyWhenRelatertPersonExists() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(BARN)
                .relatertPerson(RELATERT_IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(RELATERT_IDENT)).thenReturn(Mono.just(true));

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    // ---- validate: testnorge ident skips person check ----

    @Test
    void shouldSkipPersonCheckForTestnorgeIdent() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(BARN)
                .relatertPerson(RELATERT_IDENT)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(TESTNORGE_IDENT)
                        .build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    // ---- validate: blank relatert skips person check ----

    @Test
    void shouldSkipPersonCheckWhenRelatertPersonIsBlank() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FAR)
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();

        verify(personRepository, never()).existsByIdent(anyString());
    }

    // ---- validate: delt bosted single adresse passes ----

    @Test
    void shouldValidateSuccessfullyWithDeltBostedSingleAdresse() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FORELDER)
                .relatertPersonsRolle(BARN)
                .deltBosted(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build())
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    // ---- validate: only relatertPersonUtenFolkeregisteridentifikator passes ----

    @Test
    void shouldValidateSuccessfullyWhenOnlyRelatertPersonUtenIdentifikator() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FAR)
                .relatertPersonsRolle(BARN)
                .relatertPersonUtenFolkeregisteridentifikator(new RelatertBiPersonDTO())
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyComplete();
    }

    // ---- validate: ambiguous FORELDER-FORELDER ----

    @Test
    void shouldRejectForelderForelderRelasjon() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FAR)
                .relatertPersonsRolle(MOR)
                .isNew(true)
                .build();

        StepVerifier.create(forelderBarnRelasjonService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("må være av type barn -- forelder, eller forelder -- barn")));
    }

    // ---- convert: empty relasjon list ----

    @Test
    void shouldConvertSuccessfullyWhenRelasjonListIsEmpty() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .forelderBarnRelasjon(new ArrayList<>())
                        .build())
                .build();

        StepVerifier.create(forelderBarnRelasjonService.convert(dbPerson))
                .assertNext(result -> assertThat(result.getPerson().getForelderBarnRelasjon().isEmpty(), is(true)))
                .verifyComplete();
    }

    // ---- convert: isNew=false skipped ----

    @Test
    void shouldSkipItemsNotMarkedAsNew() {

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .forelderBarnRelasjon(new ArrayList<>(List.of(
                                ForelderBarnRelasjonDTO.builder()
                                        .minRolleForPerson(MOR)
                                        .relatertPersonsRolle(BARN)
                                        .relatertPerson(RELATERT_IDENT)
                                        .isNew(false)
                                        .id(1)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(forelderBarnRelasjonService.convert(dbPerson))
                .assertNext(result -> {
                    var relasjon = result.getPerson().getForelderBarnRelasjon().getFirst();
                    assertThat(relasjon.getKilde(), is(nullValue()));
                    assertThat(relasjon.getMaster(), is(nullValue()));
                })
                .verifyComplete();
    }
}