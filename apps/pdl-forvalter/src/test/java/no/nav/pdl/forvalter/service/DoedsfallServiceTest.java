package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoedsfallServiceTest {

    private static final String IDENT = "12345678901";
    private static final String PARTNER_IDENT = "98765432100";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DoedsfallService doedsfallService;

    @Test
    void whenDoedsdatoIsMissing_thenThrowExecption() {

        var request = DoedsfallDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(doedsfallService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Dødsfall: dødsdato må oppgis")));
    }

    @Test
    void shouldCompleteValidationWhenDoedsdatoIsProvided() {

        var request = DoedsfallDTO.builder()
                .doedsdato(LocalDateTime.of(2024, 1, 15, 0, 0))
                .build();

        StepVerifier.create(doedsfallService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldReturnDbPersonWhenDoedsfallListIsEmpty() {

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldSkipNonNewDoedsfallEntries() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(false)
                .doedsdato(LocalDateTime.of(2024, 3, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldSetKildeAndMasterOnNewDoedsfall() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 5, 10, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);
                    assertThat(doedsfall.getKilde()).isEqualTo("Dolly");
                    assertThat(doedsfall.getMaster()).isNotNull();
                })
                .verifyComplete();

        verify(personRepository, never()).findByIdent(any());
    }

    @Test
    void shouldPreserveExistingKildeWhenAlreadySet() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 5, 10, 0, 0))
                .kilde("Saksbehandler")
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(doedsfall.getKilde()).isEqualTo("Saksbehandler"))
                .verifyComplete();
    }

    @Test
    void shouldSortDoedsfallByDoedsdatoDescendingAndRenumberId() {

        var older = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2020, 1, 1, 0, 0))
                .build();

        var newer = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 6, 15, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(older);
        person.getDoedsfall().add(newer);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> {
                    var doedsfall = result.getPerson().getDoedsfall();
                    assertThat(doedsfall.get(0).getDoedsdato()).isEqualTo(LocalDateTime.of(2024, 6, 15, 0, 0));
                    assertThat(doedsfall.get(1).getDoedsdato()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0));
                    assertThat(doedsfall.get(0).getId()).isEqualTo(2);
                    assertThat(doedsfall.get(1).getId()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void shouldNotUpdatePartnerWhenSivilstandIsEmpty() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 3, 1, 0, 0))
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verify(personRepository, never()).findByIdent(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void shouldNotUpdatePartnerWhenSivilstandIsNotGift() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 3, 1, 0, 0))
                .build();

        var sivilstand = SivilstandDTO.builder()
                .type(Sivilstand.UGIFT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);
        person.getSivilstand().add(sivilstand);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verify(personRepository, never()).findByIdent(any());
    }

    @Test
    void shouldNotUpdatePartnerWhenRelatertVedSivilstandIsBlank() {

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(LocalDateTime.of(2024, 3, 1, 0, 0))
                .build();

        var sivilstand = SivilstandDTO.builder()
                .type(Sivilstand.GIFT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);
        person.getSivilstand().add(sivilstand);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> assertThat(result).isSameAs(dbPerson))
                .verifyComplete();

        verify(personRepository, never()).findByIdent(any());
    }

    @Test
    void shouldUpdatePartnerSivilstandToEnkeWhenGift() {

        var doedsdato = LocalDateTime.of(2024, 7, 20, 0, 0);

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(doedsdato)
                .kilde("Dolly")
                .master(Master.FREG)
                .build();

        var sivilstand = SivilstandDTO.builder()
                .type(Sivilstand.GIFT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);
        person.getSivilstand().add(sivilstand);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(PARTNER_IDENT)
                .build();
        partnerPerson.getSivilstand().add(SivilstandDTO.builder()
                .type(Sivilstand.GIFT)
                .relatertVedSivilstand(IDENT)
                .build());

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(partnerPerson)
                .build();

        var dbPersonCaptor = ArgumentCaptor.forClass(DbPerson.class);
        when(personRepository.findByIdent(PARTNER_IDENT))
                .thenReturn(Mono.just(partnerDbPerson));
        when(personRepository.save(dbPersonCaptor.capture()))
                .thenReturn(Mono.just(partnerDbPerson));

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result).isSameAs(dbPerson);

                    var savedPartner = dbPersonCaptor.getValue();
                    var partnerSivilstandList = savedPartner.getPerson().getSivilstand();
                    assertThat(partnerSivilstandList).hasSize(2);

                    var newSivilstand = partnerSivilstandList.getFirst();
                    assertThat(newSivilstand.getType()).isEqualTo(Sivilstand.ENKE_ELLER_ENKEMANN);
                    assertThat(newSivilstand.getSivilstandsdato()).isEqualTo(doedsdato);
                    assertThat(newSivilstand.getKilde()).isEqualTo("Dolly");
                    assertThat(newSivilstand.getMaster()).isEqualTo(Master.FREG);
                })
                .verifyComplete();

        verify(personRepository).findByIdent(PARTNER_IDENT);
        verify(personRepository).save(any(DbPerson.class));
    }

    @Test
    void shouldUpdatePartnerSivilstandToGjenlevendePartnerWhenRegistrertPartner() {

        var doedsdato = LocalDateTime.of(2024, 8, 5, 0, 0);

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(doedsdato)
                .build();

        var sivilstand = SivilstandDTO.builder()
                .type(Sivilstand.REGISTRERT_PARTNER)
                .relatertVedSivilstand(PARTNER_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);
        person.getSivilstand().add(sivilstand);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(PARTNER_IDENT)
                .build();
        partnerPerson.getSivilstand().add(SivilstandDTO.builder()
                .type(Sivilstand.REGISTRERT_PARTNER)
                .relatertVedSivilstand(IDENT)
                .build());

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(partnerPerson)
                .build();

        var dbPersonCaptor = ArgumentCaptor.forClass(DbPerson.class);
        when(personRepository.findByIdent(PARTNER_IDENT))
                .thenReturn(Mono.just(partnerDbPerson));
        when(personRepository.save(dbPersonCaptor.capture()))
                .thenReturn(Mono.just(partnerDbPerson));

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> {
                    var newSivilstand = dbPersonCaptor.getValue()
                            .getPerson().getSivilstand().getFirst();
                    assertThat(newSivilstand.getType()).isEqualTo(Sivilstand.GJENLEVENDE_PARTNER);
                    assertThat(newSivilstand.getSivilstandsdato()).isEqualTo(doedsdato);
                })
                .verifyComplete();
    }

    @Test
    void shouldSetCorrectIdOnNewPartnerSivilstand() {

        var doedsdato = LocalDateTime.of(2024, 9, 1, 0, 0);

        var doedsfall = DoedsfallDTO.builder()
                .isNew(true)
                .doedsdato(doedsdato)
                .build();

        var sivilstand = SivilstandDTO.builder()
                .type(Sivilstand.GIFT)
                .relatertVedSivilstand(PARTNER_IDENT)
                .build();

        var person = PersonDTO.builder()
                .ident(IDENT)
                .build();
        person.getDoedsfall().add(doedsfall);
        person.getSivilstand().add(sivilstand);

        var dbPerson = DbPerson.builder()
                .ident(IDENT)
                .person(person)
                .build();

        var partnerPerson = PersonDTO.builder()
                .ident(PARTNER_IDENT)
                .build();
        partnerPerson.getSivilstand().add(SivilstandDTO.builder().type(Sivilstand.UGIFT).build());
        partnerPerson.getSivilstand().add(SivilstandDTO.builder().type(Sivilstand.GIFT).build());

        var partnerDbPerson = DbPerson.builder()
                .ident(PARTNER_IDENT)
                .person(partnerPerson)
                .build();

        var dbPersonCaptor = ArgumentCaptor.forClass(DbPerson.class);
        when(personRepository.findByIdent(PARTNER_IDENT))
                .thenReturn(Mono.just(partnerDbPerson));
        when(personRepository.save(dbPersonCaptor.capture()))
                .thenReturn(Mono.just(partnerDbPerson));

        StepVerifier.create(doedsfallService.convert(dbPerson))
                .assertNext(result -> {
                    var newSivilstand = dbPersonCaptor.getValue()
                            .getPerson().getSivilstand().getFirst();
                    assertThat(newSivilstand.getId()).isEqualTo(3);
                })
                .verifyComplete();
    }
}