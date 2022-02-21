package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class GjeldendeArtifactServiceTest {

    @InjectMocks
    private GjeldendeArtifactService gjeldendeArtifactService;

    @Test
    void whenBosatt_thenSisteInfoErGjeldene() {

        var person = PersonDTO.builder()
                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO.builder()
                        .status(BOSATT)
                        .isNew(true)
                        .build()))
                .navn(List.of(
                        NavnDTO.builder()
                                .isNew(true)
                                .build(),
                        NavnDTO.builder()
                                .isNew(true)
                                .build()))
                .build();

        gjeldendeArtifactService.setGjeldene(DbPerson.builder()
                .person(person)
                .build());

        assertThat(person.getNavn().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person.getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
    }

    @Test
    void whenOpphoert_thenAnnenInfoErIkkeGjeldene() {

        var person = PersonDTO.builder()
                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO.builder()
                        .status(OPPHOERT)
                        .isNew(true)
                        .build()))
                .navn(List.of(
                        NavnDTO.builder()
                                .isNew(true)
                                .build(),
                        NavnDTO.builder()
                                .isNew(true)
                                .build()))
                .build();

        gjeldendeArtifactService.setGjeldene(DbPerson.builder()
                .person(person)
                .build());

        assertThat(person.getNavn().get(0).getGjeldende(), is(equalTo(false)));
        assertThat(person.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person.getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
    }

    @Test
    void whenRelasjon_thenGjeldendeBlirOgsaaOppdatert() {

        var person1 = PersonDTO.builder()
                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO.builder()
                        .status(UTFLYTTET)
                        .isNew(true)
                        .build()))
                .navn(List.of(
                        NavnDTO.builder()
                                .isNew(true)
                                .build(),
                        NavnDTO.builder()
                                .isNew(true)
                                .build()))
                .build();

        var person2 = PersonDTO.builder()
                .folkeregisterPersonstatus(List.of(FolkeregisterPersonstatusDTO.builder()
                        .status(BOSATT)
                        .isNew(true)
                        .build()))
                .bostedsadresse(List.of(
                        BostedadresseDTO.builder()
                                .isNew(true)
                                .build(),
                        BostedadresseDTO.builder()
                                .isNew(true)
                                .build()))
                .build();

        gjeldendeArtifactService.setGjeldene(DbPerson.builder()
                .person(person1)
                .relasjoner(List.of(DbRelasjon.builder()
                        .person(DbPerson.builder()
                                .person(person2)
                                .build())
                        .build()))
                .build());

        assertThat(person1.getNavn().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person1.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person2.getBostedsadresse().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person2.getBostedsadresse().get(1).getGjeldende(), is(equalTo(false)));
    }
}