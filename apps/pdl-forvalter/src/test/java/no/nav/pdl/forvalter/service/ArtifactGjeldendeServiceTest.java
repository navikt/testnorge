package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.BOSATT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.OPPHOERT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO.FolkeregisterPersonstatus.UTFLYTTET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactGjeldendeServiceTest {

    private static final String TEST_IDENT_1 = "11111111111";
    private static final String TEST_IDENT_2 = "22222222222";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private ArtifactGjeldendeService artifactGjeldendeService;

    @Test
    void whenBosatt_thenSisteInfoErGjeldene() {

        var person = PersonDTO.builder()
                .ident(TEST_IDENT_1)
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

        when(personRepository.findByIdent(TEST_IDENT_1))
                .thenReturn(Optional.of(DbPerson.builder().person(person).build()));

        artifactGjeldendeService.setGjeldene(TEST_IDENT_1);

        assertThat(person.getNavn().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person.getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
    }

    @Test
    void whenOpphoert_thenAnnenInfoErIkkeGjeldene() {

        var person = PersonDTO.builder()
                .ident(TEST_IDENT_1)
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

        when(personRepository.findByIdent(TEST_IDENT_1))
                .thenReturn(Optional.of(DbPerson.builder().person(person).build()));

        artifactGjeldendeService.setGjeldene(TEST_IDENT_1);

        assertThat(person.getNavn().get(0).getGjeldende(), is(equalTo(false)));
        assertThat(person.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person.getFolkeregisterPersonstatus().get(0).getGjeldende(), is(equalTo(true)));
    }

    @Test
    void whenRelasjon_thenGjeldendeBlirOgsaaOppdatert() {

        var person1 = PersonDTO.builder()
                .ident(TEST_IDENT_1)
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
                .ident(TEST_IDENT_2)
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

        when(personRepository.findByIdent(TEST_IDENT_1))
                .thenReturn(Optional.of(DbPerson.builder()
                        .person(person1)
                        .relasjoner(List.of(DbRelasjon.builder()
                                .relatertPerson(DbPerson.builder()
                                        .ident(TEST_IDENT_2)
                                        .person(person2)
                                        .build())
                                .build()))
                        .build()));

        when(personRepository.findByIdentIn(ArgumentMatchers.anyList(), eq(Pageable.unpaged())))
                .thenReturn(List.of(DbPerson.builder()
                        .ident(TEST_IDENT_2)
                        .person(person2)
                .build()));

        artifactGjeldendeService.setGjeldene(TEST_IDENT_1);

        assertThat(person1.getNavn().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person1.getNavn().get(1).getGjeldende(), is(equalTo(false)));
        assertThat(person2.getBostedsadresse().get(0).getGjeldende(), is(equalTo(true)));
        assertThat(person2.getBostedsadresse().get(1).getGjeldende(), is(equalTo(false)));
    }
}