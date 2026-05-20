package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InnflyttingServiceTest {

    private static final String FNR_IDENT = "12345678901";
    private static final String DNR_IDENT = "45023412345";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private BostedAdresseService bostedAdresseService;

    @InjectMocks
    private InnflyttingService innflyttingService;

    // --- validate ---

    @Test
    void shouldRejectInvalidLandkode() {

        var request = InnflyttingDTO.builder()
                .fraflyttingsland("Finnland")
                .isNew(true)
                .build();

        StepVerifier.create(innflyttingService.validate(request))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString("Landkode må oppgis i hht ISO-3 Landkoder på fraflyttingsland"));
                });
    }

    @Test
    void shouldRejectLowercaseLandkode() {

        var request = InnflyttingDTO.builder()
                .fraflyttingsland("fin")
                .isNew(true)
                .build();

        StepVerifier.create(innflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable instanceof InvalidRequestException, is(true)));
    }

    @Test
    void shouldRejectTwoCharacterLandkode() {

        var request = InnflyttingDTO.builder()
                .fraflyttingsland("FI")
                .isNew(true)
                .build();

        StepVerifier.create(innflyttingService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable instanceof InvalidRequestException, is(true)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SWE", "???", ""})
    void shouldAcceptValidIso3LandkodeAndEmptyLandkode(String landkode) {

        var request = InnflyttingDTO.builder()
                .fraflyttingsland(landkode)
                .isNew(true)
                .build();

        StepVerifier.create(innflyttingService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptNullLandkodeInValidation() {

        var request = InnflyttingDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(innflyttingService.validate(request))
                .verifyComplete();
    }

    // --- convert / handle ---

    @Test
    void shouldProvideRandomCountryWhenLandkodeIsBlank() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("IND"));
        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(DNR_IDENT, InnflyttingDTO.builder()
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getIdent(), is(equalTo(DNR_IDENT)));
                    assertThat(target.getPerson().getInnflytting().getFirst().getFraflyttingsland(), is(equalTo("IND")));
                })
                .verifyComplete();
    }

    @Test
    void shouldKeepProvidedLandkode() {

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(FNR_IDENT, InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getInnflytting().getFirst().getFraflyttingsland(), is(equalTo("SWE")));
                    verify(kodeverkConsumer, never()).getTilfeldigLand();
                })
                .verifyComplete();
    }

    @Test
    void shouldSetInnflyttingsdatoWhenNull() {

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(FNR_IDENT, InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getInnflytting().getFirst().getInnflyttingsdato(), is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldKeepProvidedInnflyttingsdato() {

        var dato = LocalDateTime.of(2020, 6, 15, 0, 0);

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(FNR_IDENT, InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(dato)
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target ->
                        assertThat(target.getPerson().getInnflytting().getFirst().getInnflyttingsdato(), is(equalTo(dato))))
                .verifyComplete();
    }

    @Test
    void shouldCreateBostedsadresseWhenNoNorskAdresseExists() {

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(FNR_IDENT, InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(LocalDateTime.of(2020, 1, 1, 0, 0))
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getBostedsadresse(), hasSize(1));
                    assertThat(target.getPerson().getBostedsadresse().getFirst().getVegadresse(), is(notNullValue()));
                    assertThat(target.getPerson().getBostedsadresse().getFirst().getGyldigFraOgMed(),
                            is(equalTo(LocalDateTime.of(2020, 1, 1, 0, 0))));
                    verify(bostedAdresseService).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateBostedsadresseWhenValidNorskAdresseAlreadyExists() {

        var innflyttingsdato = LocalDateTime.of(2020, 1, 1, 0, 0);

        var existingAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(innflyttingsdato.minusDays(10))
                .id(1)
                .build();

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(innflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(existingAdresse)))
                        .build())
                .build();

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getBostedsadresse(), hasSize(1));
                    verify(bostedAdresseService, never()).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateBostedsadresseWhenNorskAdresseHasNoExpiry() {

        var innflyttingsdato = LocalDateTime.of(2020, 1, 1, 0, 0);

        var existingAdresse = BostedadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(innflyttingsdato.minusYears(2))
                .id(1)
                .build();

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(innflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(existingAdresse)))
                        .build())
                .build();

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getBostedsadresse(), hasSize(1));
                    verify(bostedAdresseService, never()).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldCreateBostedsadresseWhenExistingNorskAdresseHasExpiredBeforeInnflytting() {

        var innflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var expiredAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(innflyttingsdato.minusYears(2))
                .gyldigTilOgMed(innflyttingsdato.minusMonths(1))
                .id(1)
                .build();

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(innflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(expiredAdresse)))
                        .build())
                .build();

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getBostedsadresse(), hasSize(2));
                    verify(bostedAdresseService).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotCreateBostedsadresseWhenOnlyUtenlandskAdresseExists() {

        var innflyttingsdato = LocalDateTime.of(2020, 1, 1, 0, 0);

        var utenlandskAdresse = BostedadresseDTO.builder()
                .utenlandskAdresse(UtenlandskAdresseDTO.builder().landkode("SWE").build())
                .gyldigFraOgMed(innflyttingsdato.minusDays(10))
                .id(1)
                .build();

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(innflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(utenlandskAdresse)))
                        .build())
                .build();

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getBostedsadresse(), hasSize(2));
                    verify(bostedAdresseService).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldSkipInnflyttingThatIsNotNew() {

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .isNew(false)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .build())
                .build();

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    assertThat(target.getPerson().getInnflytting().getFirst().getFraflyttingsland(), is(equalTo("SWE")));
                    verify(kodeverkConsumer, never()).getTilfeldigLand();
                    verify(bostedAdresseService, never()).convert(any(DbPerson.class), eq(false));
                })
                .verifyComplete();
    }

    @Test
    void shouldAssignNewBostedsadresseIdBasedOnExistingMaxId() {

        var innflyttingsdato = LocalDateTime.of(2020, 6, 1, 0, 0);

        var expiredAdresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .gyldigFraOgMed(innflyttingsdato.minusYears(2))
                .gyldigTilOgMed(innflyttingsdato.minusMonths(1))
                .id(5)
                .build();

        var innflytting = InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .innflyttingsdato(innflyttingsdato)
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .bostedsadresse(new ArrayList<>(List.of(expiredAdresse)))
                        .build())
                .build();

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> {
                    var newAdresse = target.getPerson().getBostedsadresse().getFirst();
                    assertThat(newAdresse.getId(), is(equalTo(6)));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnSameDbPersonInstance() {

        when(bostedAdresseService.convert(any(DbPerson.class), eq(false))).thenReturn(Mono.just(new DbPerson()));

        var dbPerson = buildDbPerson(FNR_IDENT, InnflyttingDTO.builder()
                .fraflyttingsland("SWE")
                .isNew(true)
                .build());

        StepVerifier.create(innflyttingService.convert(dbPerson))
                .assertNext(target -> assertThat(target, is(dbPerson)))
                .verifyComplete();
    }

    private DbPerson buildDbPerson(String ident, InnflyttingDTO innflytting) {
        return DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(ident)
                        .innflytting(new ArrayList<>(List.of(innflytting)))
                        .build())
                .build();
    }
}