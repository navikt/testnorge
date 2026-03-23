package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.DOED;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FOEDSELSREGISTRERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.FORSVUNNET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.IKKE_BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
class FolkeregisterPersonstatusServiceTest {

    private static final String FNR_IDENT = "12044512345";
    private static final LocalDateTime GYLDIG_FRA_OG_MED = LocalDateTime.of(1970, 1, 1, 0, 0);

    @InjectMocks
    private FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    @Test
    void whenValueProvided_thenKeepValue() {

        StepVerifier.create(folkeregisterPersonstatusService
                        .convert(DbPerson.builder()
                                .person(PersonDTO
                                        .builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO
                                                .builder()
                                                .status(FORSVUNNET)
                                                .isNew(true)
                                                .gyldigFraOgMed(LocalDateTime.now())
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->

                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(FORSVUNNET))))
                .verifyComplete();
    }

    @Test
    void whenDoedsfallExists_thenUseDoedsfall() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(DbPerson.builder()
                        .person(PersonDTO.builder()
                                .ident(FNR_IDENT)
                                .folkeregisterPersonstatus(
                                        List.of(FolkeregisterPersonstatusDTO.builder()
                                                .isNew(true)
                                                .build()))
                                .doedsfall(List.of(
                                        DoedsfallDTO.builder()
                                                .doedsdato(LocalDateTime.now())
                                                .build()))
                                .build())
                        .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(DOED))))
                .verifyComplete();
    }

    @Test
    void whenOppholdExists_thenUseOpphold() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .opphold(List.of(OppholdDTO.builder()
                                                .type(OppholdDTO.OppholdType.OPPLYSNING_MANGLER)
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(MIDLERTIDIG))))
                .verifyComplete();
    }

    @Test
    void whenUtflyttingExists_thenUseUpphold() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .utflytting(List.of(UtflyttingDTO.builder()
                                                .tilflyttingsland("FRA")
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(UTFLYTTET))))
                .verifyComplete();
    }

    @Test
    void whenBostedsadresseVegadresseExists_thenUseVegadresse() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                                .vegadresse(new VegadresseDTO())
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(BOSATT))))
                .verifyComplete();
    }

    @Test
    void whenBostedsadresseMatrikkeladresseExists_thenUseMatrikkeladresse() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                                .matrikkeladresse(new MatrikkeladresseDTO())
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(BOSATT))))
                .verifyComplete();
    }

    @Test
    void whenBostedsadresseUtenlandsadresseExists_thenUseUtenlandsadresse() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                                .utenlandskAdresse(new UtenlandskAdresseDTO())
                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(IKKE_BOSATT))))
                .verifyComplete();
    }

    @Test
    void whenNoOtherInformation_thenUseDefault() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                        .isNew(true)
                                                        .build()))
                                        .build())
                                .build()))
                .assertNext(target ->
                        assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT))))
                .verifyComplete();
    }

    @Test
    void whenFraDatoIsIdenticalOnTwoStatuses_FixIt() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                                .isNew(true)
                                                .build()))
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                                .status(FOEDSELSREGISTRERT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED)
                                                                .id(2)
                                                                .build(),
                                                        FolkeregisterPersonstatusDTO.builder()
                                                                .status(BOSATT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED)
                                                                .id(1)
                                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target -> {

                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(2))));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigTilOgMed(), is((nullValue())));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getStatus(), is(equalTo(BOSATT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(1))));
                })
                .verifyComplete();
    }

    @Test
    void whenFraDatoIsArrangedProperly_DoNothing() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                                .isNew(true)
                                                .build()))
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                                .status(FOEDSELSREGISTRERT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED.plusYears(10))
                                                                .id(2)
                                                                .build(),
                                                        FolkeregisterPersonstatusDTO.builder()
                                                                .status(BOSATT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED)
                                                                .id(1)
                                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target -> {

                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusYears(10))));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigTilOgMed(), is((nullValue())));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getStatus(), is(equalTo(BOSATT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusYears(10).minusDays(1))));
                })
                .verifyComplete();
    }

    @Test
    void whenFraDatoDiffersByOneDay_Fixit() {

        StepVerifier.create(folkeregisterPersonstatusService.convert(
                        DbPerson.builder()
                                .person(PersonDTO.builder()
                                        .ident(FNR_IDENT)
                                        .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                                .isNew(true)
                                                .build()))
                                        .folkeregisterPersonstatus(
                                                List.of(FolkeregisterPersonstatusDTO.builder()
                                                                .status(FOEDSELSREGISTRERT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED.plusDays(1))
                                                                .id(2)
                                                                .build(),
                                                        FolkeregisterPersonstatusDTO.builder()
                                                                .status(BOSATT)
                                                                .gyldigFraOgMed(GYLDIG_FRA_OG_MED)
                                                                .id(1)
                                                                .build()))
                                        .build())
                                .build()))
                .assertNext(target -> {

                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(2))));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getFirst().getGyldigTilOgMed(), is((nullValue())));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getStatus(), is(equalTo(BOSATT)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
                    assertThat(target.getPerson().getFolkeregisterPersonstatus().getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(1))));
                })
                .verifyComplete();
    }
}