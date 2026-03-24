package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UtflyttingServiceTest {

    private static final String FNR_IDENT = "03012312345";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KontaktAdresseService kontaktAdresseService;

    @InjectMocks
    private UtflyttingService utflyttingService;

    // --- validate ---

    @Test
    void shouldRejectInvalidLandkode() {

        var request = UtflyttingDTO.builder()
                .tilflyttingsland("Mali")
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), is(equalTo("400 Landkode må oppgis i hht ISO-3 Landkoder for tilflyttingsland")));
                });
    }

    @Test
    void shouldRejectLowercaseLandkode() {

        var request = UtflyttingDTO.builder()
                .tilflyttingsland("swe")
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable instanceof InvalidRequestException, is(true)));
    }

    @Test
    void shouldRejectTwoCharacterLandkode() {

        var request = UtflyttingDTO.builder()
                .tilflyttingsland("SE")
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable instanceof InvalidRequestException, is(true)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CAN", "???", ""})
    void shouldAcceptValidIso3LandkodeAndBlankLandkodeInValidation(String landkode) {

        var request = UtflyttingDTO.builder()
                .tilflyttingsland(landkode)
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptNullLandkodeInValidation() {

        var request = UtflyttingDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(utflyttingService.validate(request))
                .verifyComplete();
    }

    // --- convert / handle ---

    @Test
    void shouldProvideRandomCountryWhenLandkodeIsBlank() {

        var dbPerson = buildDbPerson(FNR_IDENT, UtflyttingDTO.builder()
                .isNew(true)
                .build());

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("TGW"));
        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    verify(kodeverkConsumer).getTilfeldigLand();
                    assertThat(target.getPerson().getUtflytting().getFirst().getTilflyttingsland(), is(equalTo("TGW")));
                })
                .verifyComplete();
    }

    @Test
    void shouldKeepProvidedLandkode() {

        var dbPerson = buildDbPerson(FNR_IDENT, UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .isNew(true)
                .build());

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getUtflytting().getFirst().getTilflyttingsland(), is(equalTo("SWE")));
                    verify(kodeverkConsumer, never()).getTilfeldigLand();
                })
                .verifyComplete();
    }

    @Test
    void shouldSetUtflyttingsdatoWhenNull() {

        var dbPerson = buildDbPerson(FNR_IDENT, UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .isNew(true)
                .build());

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getUtflytting().getFirst().getUtflyttingsdato(), is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldKeepProvidedUtflyttingsdato() {

        var dato = LocalDateTime.of(2020, 6, 15, 0, 0);

        var dbPerson = buildDbPerson(FNR_IDENT, UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(dato)
                .isNew(true)
                .build());

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getUtflytting().getFirst().getUtflyttingsdato(), is(equalTo(dato))))
                .verifyComplete();
    }

    @Test
    void shouldRemoveNorskBostedsadresserAfterUtflyttingsdato() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var futureNorskAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(utflyttingsdato.plusMonths(1))
                .id(1)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(futureNorskAdresse)))
                        .build())
                .build();

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getBostedsadresse(), is(empty())))
                .verifyComplete();
    }

    @Test
    void shouldSetGyldigTilOgMedOnCurrentNorskBostedsadresse() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var currentNorskAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(utflyttingsdato.minusYears(1))
                .id(1)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(currentNorskAdresse)))
                        .build())
                .build();

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getBostedsadresse().getFirst().getGyldigTilOgMed(),
                                is(equalTo(utflyttingsdato.minusDays(1)))))
                .verifyComplete();
    }

    @Test
    void shouldCreateKontaktadresseWithUtenlandskAdresseWhenVelkjentLand() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>())
                        .kontaktadresse(new ArrayList<>())
                        .build())
                .build();

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getKontaktadresse(), hasSize(1));
                    assertThat(target.getPerson().getKontaktadresse().getFirst().getUtenlandskAdresse().getLandkode(),
                            is(equalTo("SWE")));
                    assertThat(target.getPerson().getKontaktadresse().getFirst().getGyldigFraOgMed(),
                            is(equalTo(utflyttingsdato)));
                    verify(kontaktAdresseService).convert(any(DbPerson.class), anyBoolean());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateKontaktadresseWhenLandIsUkjent() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("XUK")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>())
                        .kontaktadresse(new ArrayList<>())
                        .build())
                .build();

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getKontaktadresse(), is(empty()));
                    verify(kontaktAdresseService, never()).convert(any(DbPerson.class), anyBoolean());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateKontaktadresseWhenUtenlandskBostedsadresseAlreadyExists() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var existingUtenlandskBoadresse = BostedadresseDTO.builder()
                .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("SWE").build())
                .gyldigFraOgMed(utflyttingsdato)
                .id(1)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(existingUtenlandskBoadresse)))
                        .kontaktadresse(new ArrayList<>())
                        .build())
                .build();

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getKontaktadresse(), is(empty()));
                    verify(kontaktAdresseService, never()).convert(any(DbPerson.class), anyBoolean());
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateKontaktadresseWhenUtenlandskKontaktadresseAlreadyExists() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var existingUtenlandskKontaktadresse = KontaktadresseDTO.builder()
                .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("SWE").build())
                .gyldigFraOgMed(utflyttingsdato)
                .id(1)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>())
                        .kontaktadresse(new ArrayList<>(List.of(existingUtenlandskKontaktadresse)))
                        .build())
                .build();

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getKontaktadresse(), hasSize(1));
                    verify(kontaktAdresseService, never()).convert(any(DbPerson.class), anyBoolean());
                })
                .verifyComplete();
    }

    @Test
    void shouldSkipUtflyttingThatIsNotNew() {

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .isNew(false)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .build())
                .build();

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getUtflytting().getFirst().getTilflyttingsland(), is(equalTo("SWE")));
                    verify(kodeverkConsumer, never()).getTilfeldigLand();
                    verify(kontaktAdresseService, never()).convert(any(DbPerson.class), anyBoolean());
                })
                .verifyComplete();
    }

    @Test
    void shouldAssignNewKontaktadresseIdBasedOnExistingMaxId() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var existingKontaktadresse = KontaktadresseDTO.builder()
                .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("FIN").build())
                .gyldigFraOgMed(utflyttingsdato.minusYears(2))
                .gyldigTilOgMed(utflyttingsdato.minusMonths(1))
                .id(7)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>())
                        .kontaktadresse(new ArrayList<>(List.of(existingKontaktadresse)))
                        .build())
                .build();

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    var newKontaktadresse = target.getPerson().getKontaktadresse().getFirst();
                    assertThat(newKontaktadresse.getId(), is(equalTo(8)));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnSameDbPersonInstance() {

        var dbPerson = buildDbPerson(FNR_IDENT, UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .isNew(true)
                .build());

        when(kontaktAdresseService.convert(any(DbPerson.class), anyBoolean())).thenReturn(Mono.just(dbPerson));

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target -> assertThat(target, is(dbPerson)))
                .verifyComplete();
    }

    @Test
    void shouldNotRemoveUtenlandskBostedsadresserOnUtflytting() {

        var utflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var utenlandskAdresse = BostedadresseDTO.builder()
                .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("FIN").build())
                .gyldigFraOgMed(utflyttingsdato.plusMonths(1))
                .id(1)
                .build();

        var utflytting = UtflyttingDTO.builder()
                .tilflyttingsland("SWE")
                .utflyttingsdato(utflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(utenlandskAdresse)))
                        .kontaktadresse(new ArrayList<>())
                        .build())
                .build();

        StepVerifier.create(utflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getBostedsadresse(), hasSize(1)))
                .verifyComplete();
    }

    private DbPerson buildDbPerson(String ident, UtflyttingDTO utflytting) {
        return DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(ident)
                        .utflytting(new ArrayList<>(List.of(utflytting)))
                        .build())
                .build();
    }
}