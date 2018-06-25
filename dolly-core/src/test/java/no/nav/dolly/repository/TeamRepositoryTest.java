package no.nav.dolly.repository;

import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TestGruppeRepository testGruppeRepository;

    @Test
    public void saveTeamWithoutGruppe() {
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();

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
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();

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
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .teamtilhoerighet(foundTeam)
                .build()
                .convertToRealTestgruppe();

        foundTeam.setGrupper(new HashSet<>(Arrays.asList(testgruppe)));
        teamRepository.save(foundTeam);

        foundTeam = teamRepository.findAll().get(0);
        Testgruppe foundTestgruppe = testGruppeRepository.findAll().get(0);

        assertThat(foundTeam.getGrupper().contains(foundTestgruppe), is(true));
        assertThat(foundTestgruppe.getNavn(), is("Testgruppe"));
        assertThat(foundTestgruppe.getTeamtilhoerighet(), is(foundTeam));
    }

    @Test
    public void findTeamByEier() {
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();

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

        Team teamByBruker = teamRepository.findTeamByEier(brukere.get(0));

        assertThat(teamByBruker.getNavn(), is("team"));
        assertThat(teamByBruker.getBeskrivelse(), is("besk"));
        assertThat(teamByBruker.getEier(), is(brukere.get(0)));
    }

    @Test
    public void findFlereTeamsByMedlem() {
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();

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