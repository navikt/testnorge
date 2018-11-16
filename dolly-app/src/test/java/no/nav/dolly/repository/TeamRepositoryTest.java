package no.nav.dolly.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
public class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    GruppeRepository gruppeRepository;

    @Test
    public void saveTeamWithoutGruppe() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(Arrays.asList(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAll();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(new HashSet<>(Arrays.asList(brukere.get(1))))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        teamRepository.save(team);

        Team foundTeam = teamRepository.findAll().get(0);

        assertThat(foundTeam.getNavn(), is("team"));
        assertThat(foundTeam.getEier().getNavIdent(), is("eier"));
        assertThat(foundTeam.getMedlemmer().contains(brukere.get(1)), is(true));
        assertThat(foundTeam.getBeskrivelse(), is("besk"));
    }

    @Test
    public void saveTeamWithGruppe() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(Arrays.asList(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAll();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(new HashSet<>(Arrays.asList(brukere.get(1))))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        teamRepository.save(team);
        Team foundTeam = teamRepository.findAll().get(0);

        assertThat(foundTeam.getNavn(), is("team"));
        assertThat(foundTeam.getEier().getNavIdent(), is("eier"));
        assertThat(foundTeam.getMedlemmer().contains(brukere.get(1)), is(true));
        assertThat(foundTeam.getBeskrivelse(), is("besk"));

        /*---  Med gruppe  ---*/
        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .navn("Testgruppe")
                .hensikt("hensikt")
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .teamtilhoerighet(foundTeam)
                .build()
                .convertToRealTestgruppe();

        gruppeRepository.save(testgruppe);

        foundTeam.setGrupper(new HashSet<>(Arrays.asList(testgruppe)));
        teamRepository.save(foundTeam);

        foundTeam = teamRepository.findAll().get(0);
        Testgruppe foundTestgruppe = gruppeRepository.findAll().get(0);

        assertThat(foundTeam.getGrupper().contains(foundTestgruppe), is(true));
        assertThat(foundTestgruppe.getTeamtilhoerighet(), is(foundTeam));
    }

    @Test
    public void findTeamByEier() {
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(Arrays.asList(brukerEier));
        List<Bruker> brukere = brukerRepository.findAll();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(new HashSet<>(Arrays.asList(brukere.get(0))))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        teamRepository.save(team);

        List<Team> teamByBruker = teamRepository.findTeamsByEier(brukere.get(0));

        assertThat(teamByBruker.get(0).getNavn(), is("team"));
        assertThat(teamByBruker.get(0).getBeskrivelse(), is("besk"));
        assertThat(teamByBruker.get(0).getEier(), is(brukere.get(0)));
    }

    @Test
    public void findFlereTeamsByMedlem() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(Arrays.asList(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAll();

        Team teamONE = TeamBuilder.builder()
                .navn("teamONE")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(new HashSet<>(Arrays.asList(brukere.get(1))))
                .build()
                .convertToRealTeam();

        Team teamTWO = TeamBuilder.builder()
                .navn("teamTWO")
                .datoOpprettet(LocalDate.of(2002, 2, 2))
                .eier(brukere.get(0))
                .medlemmer(new HashSet<>(Arrays.asList(brukere.get(1), brukere.get(0))))
                .build()
                .convertToRealTeam();

        teamRepository.saveAll(Arrays.asList(teamONE, teamTWO));

        List<Team> teams = teamRepository.findByMedlemmer_NavIdent(brukere.get(1).getNavIdent());

        assertThat(teams.size(), is(2));

        Team teamONEresponse = teams.get(0);
        Team teamTWOresponse = teams.get(1);

        assertThat(teamONEresponse.getNavn(), is("teamONE"));
        assertThat(teamONEresponse.getDatoOpprettet().getDayOfMonth(), is(1));
        assertThat(teamONEresponse.getDatoOpprettet().getMonthValue(), is(1));
        assertThat(teamONEresponse.getDatoOpprettet().getYear(), is(2000));
        assertThat(teamONEresponse.getMedlemmer().contains(brukere.get(1)), is(true));

        assertThat(teamTWOresponse.getNavn(), is("teamTWO"));
        assertThat(teamTWOresponse.getDatoOpprettet().getDayOfMonth(), is(2));
        assertThat(teamTWOresponse.getDatoOpprettet().getMonthValue(), is(2));
        assertThat(teamTWOresponse.getDatoOpprettet().getYear(), is(2002));
        assertThat(teamTWOresponse.getMedlemmer().containsAll(Arrays.asList(brukere.get(1), brukere.get(0))), is(true));
    }
}