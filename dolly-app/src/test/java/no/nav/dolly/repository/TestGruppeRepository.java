package no.nav.dolly.repository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import no.nav.dolly.LocalAppStarter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LocalAppStarter.class)
@ActiveProfiles(value = "test")
@Transactional
public class TestGruppeRepository {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    GruppeRepository gruppeRepository;

    @Autowired
    IdentRepository identRepository;

    @Test
    public void saveTestgruppeUtenIdenterOgUtenFavorisertAv() {
        Bruker bruker = brukerRepository.save(Bruker.builder().navIdent("ident").build());

        Team team = teamRepository.save(TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.now())
                .eier(bruker)
                .beskrivelse("besk")
                .build()
                .convertToRealTeam());

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .navn("gruppe")
                .hensikt("hensikt")
                .teamtilhoerighet(team)
                .build()
                .convertToRealTestgruppe();

        Testgruppe savedGruppe = gruppeRepository.save(testgruppe);
        Testgruppe foundGruppe = gruppeRepository.findById(savedGruppe.getId()).get();
        Team foundTeam = teamRepository.findAll().get(0);

        assertThat(foundGruppe.getNavn(), is("gruppe"));
        assertThat(foundGruppe.getTeamtilhoerighet().getNavn(), is("team"));
        assertThat(foundGruppe.getOpprettetAv().getNavIdent(), is("ident"));
    }

}