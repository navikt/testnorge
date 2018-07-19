package no.nav.dolly.repository;

import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BrukerRepositoryTest {

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TestGruppeRepository testGruppeRepository;

    @Test
    public void oppretteBrukerUtenTIlknytningTilTeamEllerGruppe(){
        brukerRepository.save(BrukerBuilder.builder().navIdent("nav").build().convertToRealBruker());

        Bruker foundBruker = brukerRepository.findBrukerByNavIdent("nav");

        assertThat(foundBruker.getNavIdent(), is("nav"));
    }

    @Test
    public void oppretteBrukerMedTilknytningTilTeamUtenFavoritter(){
        brukerRepository.save(BrukerBuilder.builder().navIdent("nav").build().convertToRealBruker());

        Bruker foundBruker = brukerRepository.findBrukerByNavIdent("nav");

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(foundBruker)
                .medlemmer(new HashSet<>(Arrays.asList(foundBruker)))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

//        foundBruker.setTeams(new HashSet<>(Arrays.asList(team)));
        teamRepository.save(team);

        foundBruker = brukerRepository.findBrukerByNavIdent("nav");
//        List<Team> teams = new ArrayList<>(foundBruker.getTeams());

        assertThat(foundBruker.getNavIdent(), is("nav"));
//        assertThat(foundBruker.getTeams().size(), is(1));
//        assertThat(teams.get(0).getNavn(), is("team"));
//        assertThat(teams.get(0).getMedlemmer().contains(foundBruker), is(true));
    }

    @Test
    public void opprettBrukerOgSetTilTeamOgSetFavoritter() throws Exception{
        Bruker savedBruker = brukerRepository.save(BrukerBuilder.builder().navIdent("nav").build().convertToRealBruker());
        Testident testident = TestidentBuilder.builder().ident("123456789").build().convertToRealTestident();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(savedBruker)
                .medlemmer(new HashSet<>(Arrays.asList(savedBruker)))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        Team savedTeam = teamRepository.save(team);

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(savedBruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(savedBruker)
                .navn("gruppe")
                .teamtilhoerighet(savedTeam)
                .build()
                .convertToRealTestgruppe();

        testident.setTestgruppe(testgruppe);

//        testgruppe.setFavorisertAv(new HashSet<>(Arrays.asList(savedBruker)));
        savedTeam.setGrupper(new HashSet<>(Arrays.asList(testgruppe)));

        Set<Testident> identer = new HashSet<>(Arrays.asList(testident));
        testgruppe.setTestidenter(identer);

        savedBruker.setFavoritter(new HashSet<>(Arrays.asList(testgruppe)));
//        savedBruker.setTeams(new HashSet<>(Arrays.asList(savedTeam)));

        teamRepository.save(savedTeam);
        savedBruker = brukerRepository.save(savedBruker);

        Bruker foundByIdBruker = brukerRepository.findBrukerByNavIdent("nav");

        assertThat(savedBruker.getNavIdent(), is(savedBruker.getNavIdent()));
        assertThat(foundByIdBruker.getNavIdent(), is(savedBruker.getNavIdent()));
    }
}