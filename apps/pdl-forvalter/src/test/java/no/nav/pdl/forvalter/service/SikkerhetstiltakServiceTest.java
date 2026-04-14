package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO.Tiltakstype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class SikkerhetstiltakServiceTest {

    private static final String IDENT = "12345678901";

    @InjectMocks
    private SikkerhetstiltakService sikkerhetstiltakService;

    @Test
    void whenTiltakstypeIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: Tiltakstype må angis")));
    }

    @Test
    void whenBeskrivelseIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: Beskrivelse må angis")));
    }

    @Test
    void whenGyldigFomIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: GyldigFom må angis")));
    }

    @Test
    void whenGyldigTomIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: GyldigTom må angis")));
    }

    @Test
    void whenKontaktpersonIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: Personident og enhet må angis")));
    }

    @Test
    void whenPersonidentIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .kontaktperson(new SikkerhetstiltakDTO.Kontaktperson())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: NAV personident må angis")));
    }

    @Test
    void whenEnhetIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z999999")
                        .build())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: Enhet må angis")));
    }

    @Test
    void whenUgyldigDatoIntervall_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().minusDays(1))
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z999999")
                        .enhet("0218")
                        .build())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sikkerhetstiltak: Ugyldig datointervall: gyldigFom må være før gyldigTom")));
    }

    @Test
    void shouldCompleteValidationWhenAllFieldsAreValid() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.FYUS)
                .beskrivelse("Fysisk utestengelse")
                .gyldigFraOgMed(LocalDateTime.of(2024, 1, 1, 0, 0))
                .gyldigTilOgMed(LocalDateTime.of(2025, 1, 1, 0, 0))
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z999999")
                        .enhet("0218")
                        .build())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldCompleteValidationWhenGyldigFomEqualsGyldigTom() {

        var sameDate = LocalDateTime.of(2024, 6, 15, 0, 0);

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TFUS)
                .beskrivelse("Telefonisk utestengelse")
                .gyldigFraOgMed(sameDate)
                .gyldigTilOgMed(sameDate)
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z123456")
                        .enhet("1234")
                        .build())
                .build();

        StepVerifier.create(sikkerhetstiltakService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldReturnDbPersonWhenSikkerhetstiltakListIsEmpty() {

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();
    }

    @Test
    void shouldSkipNonNewEntries() {

        var existing = SikkerhetstiltakDTO.builder()
                .isNew(false)
                .tiltakstype(Tiltakstype.TOAN)
                .gyldigFraOgMed(LocalDateTime.of(2024, 1, 1, 0, 0))
                .kilde("Original")
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().add(existing);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(existing.getKilde()).isEqualTo("Original");
                })
                .verifyComplete();
    }

    @Test
    void shouldSetKildeToDollyWhenBlank() {

        var tiltak = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.DIUS)
                .gyldigFraOgMed(LocalDateTime.of(2024, 3, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().add(tiltak);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(tiltak.getKilde()).isEqualTo("Dolly"))
                .verifyComplete();
    }

    @Test
    void shouldPreserveExistingKilde() {

        var tiltak = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.FTUS)
                .gyldigFraOgMed(LocalDateTime.of(2024, 5, 1, 0, 0))
                .kilde("Saksbehandler")
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().add(tiltak);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(tiltak.getKilde()).isEqualTo("Saksbehandler"))
                .verifyComplete();
    }

    @Test
    void shouldAlwaysSetMasterToPdl() {

        var tiltak = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .gyldigFraOgMed(LocalDateTime.of(2024, 4, 1, 0, 0))
                .master(Master.FREG)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().add(tiltak);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(tiltak.getMaster()).isEqualTo(Master.PDL))
                .verifyComplete();
    }

    @Test
    void shouldSortByGyldigFraOgMedDescending() {

        var older = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.FYUS)
                .gyldigFraOgMed(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();

        var newer = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .gyldigFraOgMed(LocalDateTime.of(2024, 6, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().addAll(List.of(older, newer));

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result -> {
                    var list = result.getPerson().getSikkerhetstiltak();
                    assertThat(list.get(0).getGyldigFraOgMed())
                            .isEqualTo(LocalDateTime.of(2024, 6, 1, 0, 0));
                    assertThat(list.get(1).getGyldigFraOgMed())
                            .isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0));
                })
                .verifyComplete();
    }

    @Test
    void shouldRenumberIdsAfterSorting() {

        var first = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.FYUS)
                .gyldigFraOgMed(LocalDateTime.of(2021, 1, 1, 0, 0))
                .build();

        var second = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TFUS)
                .gyldigFraOgMed(LocalDateTime.of(2023, 6, 1, 0, 0))
                .build();

        var third = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.DIUS)
                .gyldigFraOgMed(LocalDateTime.of(2025, 3, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().addAll(List.of(first, second, third));

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result -> {
                    var list = result.getPerson().getSikkerhetstiltak();
                    assertThat(list).hasSize(3);
                    assertThat(list.get(0).getId()).isEqualTo(3);
                    assertThat(list.get(1).getId()).isEqualTo(2);
                    assertThat(list.get(2).getId()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void shouldOnlyProcessNewEntriesInMixedList() {

        var existingTiltak = SikkerhetstiltakDTO.builder()
                .isNew(false)
                .tiltakstype(Tiltakstype.TOAN)
                .gyldigFraOgMed(LocalDateTime.of(2022, 1, 1, 0, 0))
                .kilde("Existing")
                .master(Master.FREG)
                .build();

        var newTiltak = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.FYUS)
                .gyldigFraOgMed(LocalDateTime.of(2024, 5, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getSikkerhetstiltak().addAll(List.of(existingTiltak, newTiltak));

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(sikkerhetstiltakService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(newTiltak.getKilde()).isEqualTo("Dolly");
                    assertThat(newTiltak.getMaster()).isEqualTo(Master.PDL);
                    assertThat(existingTiltak.getKilde()).isEqualTo("Existing");
                    assertThat(existingTiltak.getMaster()).isEqualTo(Master.FREG);
                })
                .verifyComplete();
    }
}