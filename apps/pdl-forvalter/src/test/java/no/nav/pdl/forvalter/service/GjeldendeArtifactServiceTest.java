package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
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

        var target = gjeldendeArtifactService.setGjeldene(DbPerson.builder()
                .person(person)
                .build());

        assertThat(target.getPerson().getNavn().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(target.getPerson().getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(target.getPerson().getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
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

        var target = gjeldendeArtifactService.setGjeldene(DbPerson.builder()
                .person(person)
                .build());

        assertThat(target.getPerson().getNavn().get(0).getGjeldende(), is(equalTo(false)));
        assertThat(target.getPerson().getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(target.getPerson().getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
    }
}