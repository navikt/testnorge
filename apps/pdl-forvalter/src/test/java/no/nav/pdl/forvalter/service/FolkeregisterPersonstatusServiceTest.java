package no.nav.pdl.forvalter.service;

import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.UtflyttingDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.BOSATT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.DOED;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.FOEDSELSREGISTRERT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.FORSVUNNET;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.IKKE_BOSATT;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.MIDLERTIDIG;
import static no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.FolkeregisterpersonstatusDTO.Folkeregisterpersonstatus.UTFLYTTET;
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
                PersonDTO.builder()
                        .folkeregisterpersonstatus(List.of(FolkeregisterpersonstatusDTO.builder()
                                .status(FORSVUNNET)
                                .isNew(true)
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(FORSVUNNET)));
    }

    @Test
    void whenDoedsfallExists_thenUseDoedsfall() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .doedsfall(List.of(
                                DoedsfallDTO.builder()
                                        .doedsdato(LocalDateTime.now())
                                        .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(DOED)));
    }

    @Test
    void whenOppholdExists_thenUseOpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .opphold(List.of(OppholdDTO.builder()
                                .type(OppholdDTO.OppholdType.OPPLYSNING_MANGLER)
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(MIDLERTIDIG)));
    }

    @Test
    void whenUtflyttingExists_thenUseUpphold() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .utflytting(List.of(UtflyttingDTO.builder()
                                .tilflyttingsland("FRA")
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(UTFLYTTET)));
    }

    @Test
    void whenBostedsadresseVegadresseExists_thenUseVegadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .vegadresse(new VegadresseDTO())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseMatrikkeladresseExists_thenUseMatrikkeladresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .matrikkeladresse(new MatrikkeladresseDTO())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(BOSATT)));
    }

    @Test
    void whenBostedsadresseUtenlandsadresseExists_thenUseUtenlandsadresse() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .bostedsadresse(List.of(BostedadresseDTO.builder()
                                .utenlandskAdresse(new UtenlandskAdresseDTO())
                                .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(IKKE_BOSATT)));
    }

    @Test
    void whenNoOtherInformation_thenUseDefault() {

        var target = folkeregisterPersonstatusService.convert(
                PersonDTO.builder()
                        .folkeregisterpersonstatus(
                                List.of(FolkeregisterpersonstatusDTO.builder()
                                        .isNew(true)
                                        .build()))
                        .build()).get(0);

        assertThat(target.getStatus(), is(equalTo(FOEDSELSREGISTRERT)));
    }
}