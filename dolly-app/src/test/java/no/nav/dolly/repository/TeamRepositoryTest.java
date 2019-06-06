package no.nav.dolly.repository;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static no.nav.dolly.util.ListUtil.listOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import no.nav.dolly.LocalAppStarter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

        brukerRepository.saveAll(asList(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAllByOrderByNavIdent();

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(singletonList(brukere.get(1)))
                .beskrivelse("besk")
                .build();

        teamRepository.save(team);

        Team foundTeam = teamRepository.findAllByOrderByNavn().get(0);

        assertThat(foundTeam.getNavn(), is("team"));
        assertThat(foundTeam.getEier().getNavIdent(), is("eier"));
        assertThat(foundTeam.getMedlemmer().contains(brukere.get(1)), is(true));
        assertThat(foundTeam.getBeskrivelse(), is("besk"));
    }

    @Test
    public void saveTeamWithGruppe() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(listOf(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAllByOrderByNavIdent();

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(listOf(brukere.get(1)))
                .beskrivelse("besk")
                .build();

        teamRepository.save(team);
        Team foundTeam = teamRepository.findAllByOrderByNavn().get(0);

        assertThat(foundTeam.getNavn(), is("team"));
        assertThat(foundTeam.getEier().getNavIdent(), is("eier"));
        assertThat(foundTeam.getMedlemmer().contains(brukere.get(1)), is(true));
        assertThat(foundTeam.getBeskrivelse(), is("besk"));

        /*---  Med gruppe  ---*/
        Testgruppe testgruppe = Testgruppe.builder()
                .navn("Testgruppe")
                .hensikt("hensikt")
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .teamtilhoerighet(foundTeam)
                .build();

        gruppeRepository.save(testgruppe);

        foundTeam.setGrupper(listOf(testgruppe));
        teamRepository.save(foundTeam);

        foundTeam = teamRepository.findAllByOrderByNavn().get(0);
        Testgruppe foundTestgruppe = gruppeRepository.findAllByOrderByNavn().get(0);

        assertThat(foundTeam.getGrupper().contains(foundTestgruppe), is(true));
        assertThat(foundTestgruppe.getTeamtilhoerighet(), is(foundTeam));
    }

    @Test
    public void findTeamByEier() {
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(singletonList(brukerEier));
        List<Bruker> brukere = brukerRepository.findAllByOrderByNavIdent();

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(singletonList(brukere.get(0)))
                .beskrivelse("besk")
                .build();

        teamRepository.save(team);

        List<Team> teamByBruker = teamRepository.findTeamsByEierOrderByNavn(brukere.get(0));

        assertThat(teamByBruker.get(0).getNavn(), is("team"));
        assertThat(teamByBruker.get(0).getBeskrivelse(), is("besk"));
        assertThat(teamByBruker.get(0).getEier(), is(brukere.get(0)));
    }

    @Test
    public void findFlereTeamsByMedlem() {
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();

        brukerRepository.saveAll(asList(bruker, brukerEier));
        List<Bruker> brukere = brukerRepository.findAllByOrderByNavIdent();

        Team teamONE = Team.builder()
                .navn("teamONE")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(brukere.get(0))
                .medlemmer(singletonList(brukere.get(1)))
                .build();

        Team teamTWO = Team.builder()
                .navn("teamTWO")
                .datoOpprettet(LocalDate.of(2002, 2, 2))
                .eier(brukere.get(0))
                .medlemmer(asList(brukere.get(1), brukere.get(0)))
                .build();

        teamRepository.saveAll(asList(teamONE, teamTWO));

        List<Team> teams = teamRepository.findByMedlemmerNavIdentOrderByNavn(brukere.get(1).getNavIdent());

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
        assertThat(teamTWOresponse.getMedlemmer().containsAll(asList(brukere.get(1), brukere.get(0))), is(true));
    }
}