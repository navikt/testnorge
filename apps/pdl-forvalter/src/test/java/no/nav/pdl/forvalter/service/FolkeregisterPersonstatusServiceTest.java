package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.PdlBostedadresse;
import no.nav.pdl.forvalter.domain.PdlDoedsfall;
import no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus;
import no.nav.pdl.forvalter.domain.PdlMatrikkeladresse;
import no.nav.pdl.forvalter.domain.PdlOpphold;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.domain.PdlUtenlandskAdresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.RsUtflytting;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.BOSATT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.DOED;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.FORSVUNNET;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.IKKE_BOSATT;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.MIDLERTIDIG;
import static no.nav.pdl.forvalter.domain.PdlFolkeregisterpersonstatus.Folkeregisterpersonstatus.UTFLYTTET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class FolkeregisterPersonstatusServiceTest {

    @InjectMocks
    private FolkeregisterPersonstatusService folkeregisterPersonstatusService;

    @Test
    void whenValueProvided_thenKeepValue() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(List.of(PdlFolkeregisterpersonstatus.builder()
                                .status(FORSVUNNET)
                                .isNew(true)
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(FORSVUNNET)));
    }

    @Test
    void whenDoedsfallExists_thenUseDoedsfall() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .doedsfall(List.of(
                                PdlDoedsfall.builder()
                                        .doedsdato(LocalDateTime.now())
                                        .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(DOED)));
    }

    @Test
    void whenOppholdExists_thenUseOpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .opphold(List.of(PdlOpphold.builder()
                                .type(PdlOpphold.OppholdType.OPPLYSNING_MANGLER)
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(MIDLERTIDIG)));
    }

    @Test
    void whenUtflyttingExists_thenUseUpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .utflytting(List.of(RsUtflytting.builder()
                                .tilflyttingsland("FRA")
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(UTFLYTTET)));
    }

    @Test
    void whenBostedsadresseVegadresseExists_thenUseVegadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(PdlBostedadresse.builder()
                                .vegadresse(new PdlVegadresse())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseMatrikkeladresseExists_thenUseMatrikkeladresse() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(PdlBostedadresse.builder()
                                .matrikkeladresse(new PdlMatrikkeladresse())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseUtenlandsadresseExists_thenUseUtenlandsadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(PdlBostedadresse.builder()
                                .utenlandskAdresse(new PdlUtenlandskAdresse())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(IKKE_BOSATT)));
    }

    @Test
    void whenNoOtherInformation_thenUseDefault() {

        var target = folkeregisterPersonstatusService.convert(
                PdlPerson.builder()
                        .folkeregisterpersonstatus(
                                List.of(PdlFolkeregisterpersonstatus.builder()
                                        .isNew(true)
                                        .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
    }
}