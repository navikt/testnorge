package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.data.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class FolkeregisterPersonstatusServiceTest {

    private static final String FNR_IDENT = "12044512345";
    private static final LocalDateTime GYLDIG_FRA_OG_MED = LocalDateTime.of(1970, 1, 1, 0, 0);

    @InjectMocks
    private FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    @Test
    void whenValueProvided_thenKeepValue() {

        var target = folkeregisterPersonstatusService
                .convert(
                        PersonDTO
                                .builder()
                                .ident(FNR_IDENT)
                                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO
                                        .builder()
                                        .status(FORSVUNNET)
                                        .isNew(true)
                                        .gyldigFraOgMed(LocalDateTime.now())
                                        .build()))
                                .build())
                .getFirst();

        assertThat(target.getStatus(), is(equalTo(FORSVUNNET)));
    }

    @Test
    void whenDoedsfallExists_thenUseDoedsfall() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .doedsfall(List.of(
                                DoedsfallDTO.builder()
                                        .doedsdato(LocalDateTime.now())
                                        .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(DOED)));
    }

    @Test
    void whenOppholdExists_thenUseOpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .opphold(List.of(OppholdDTO.builder()
                                .type(OppholdDTO.OppholdType.OPPLYSNING_MANGLER)
                                .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(MIDLERTIDIG)));
    }

    @Test
    void whenUtflyttingExists_thenUseUpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .utflytting(List.of(UtflyttingDTO.builder()
                                .tilflyttingsland("FRA")
                                .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(UTFLYTTET)));
    }

    @Test
    void whenBostedsadresseVegadresseExists_thenUseVegadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .vegadresse(new VegadresseDTO())
                                .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseMatrikkeladresseExists_thenUseMatrikkeladresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .matrikkeladresse(new MatrikkeladresseDTO())
                                .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseUtenlandsadresseExists_thenUseUtenlandsadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .utenlandskAdresse(new UtenlandskAdresseDTO())
                                .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(IKKE_BOSATT)));
    }

    @Test
    void whenNoOtherInformation_thenUseDefault() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .folkeregisterPersonstatus(
                                List.of(FolkeregisterPersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .build()).getFirst();

        assertThat(target.getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
    }

    @Test
    void whenFraDatoIsIdenticalOnTwoStatuses_FixIt() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
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
                        .build());

        assertThat(target.getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
        assertThat(target.getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(2))));
        assertThat(target.getFirst().getGyldigTilOgMed(), is((nullValue())));
        assertThat(target.getLast().getStatus(), is(equalTo(BOSATT)));
        assertThat(target.getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
        assertThat(target.getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(1))));
    }

    @Test
    void whenFraDatoIsArrangedProperly_DoNothing() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
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
                        .build());

        assertThat(target.getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
        assertThat(target.getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusYears(10))));
        assertThat(target.getFirst().getGyldigTilOgMed(), is((nullValue())));
        assertThat(target.getLast().getStatus(), is(equalTo(BOSATT)));
        assertThat(target.getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
        assertThat(target.getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusYears(10).minusDays(1))));
    }

    @Test
    void whenFraDatoDiffersByOneDay_Fixit() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
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
                        .build());

        assertThat(target.getFirst().getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
        assertThat(target.getFirst().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(2))));
        assertThat(target.getFirst().getGyldigTilOgMed(), is((nullValue())));
        assertThat(target.getLast().getStatus(), is(equalTo(BOSATT)));
        assertThat(target.getLast().getGyldigFraOgMed(), is(equalTo(GYLDIG_FRA_OG_MED)));
        assertThat(target.getLast().getGyldigTilOgMed(), is(equalTo(GYLDIG_FRA_OG_MED.plusDays(1))));
    }
}