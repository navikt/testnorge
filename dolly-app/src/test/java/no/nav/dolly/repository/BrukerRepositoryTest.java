package no.nav.dolly.repository;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.dolly.LocalAppStarter;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LocalAppStarter.class)
@ActiveProfiles(value = "test")
@Transactional
public class BrukerRepositoryTest {

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    GruppeRepository gruppeRepository;

    @Autowired
    IdentRepository identRepository;

    @Test
    @Rollback
    public void oppretteBrukerUtenTIlknytningTilTeamEllerGruppe() {
        brukerRepository.save(Bruker.builder().navIdent("nav").build());

        Bruker foundBruker = brukerRepository.findBrukerByNavIdent("nav");

        assertThat(foundBruker.getNavIdent(), is("nav"));
    }

    @Test
    public void oppretteBrukerMedTilknytningTilTeamUtenFavoritter() {
        brukerRepository.save(Bruker.builder().navIdent("nav").build());

        Bruker foundBruker = brukerRepository.findBrukerByNavIdent("nav");

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(foundBruker)
                .medlemmer(singletonList(foundBruker))
                .beskrivelse("besk")
                .build();

        foundBruker.setTeams(newHashSet(singletonList(team)));
        teamRepository.save(team);

        foundBruker = brukerRepository.findBrukerByNavIdent("nav");
        List<Team> teams = new ArrayList<>(foundBruker.getTeams());

        assertThat(foundBruker.getNavIdent(), is("nav"));
        assertThat(foundBruker.getTeams().size(), is(1));
        assertThat(teams.get(0).getNavn(), is("team"));
        assertThat(teams.get(0).getMedlemmer().contains(foundBruker), is(true));
    }

    @Test
    public void opprettBrukerOgSetTilTeamOgSetFavoritter() throws Exception {
        Bruker savedBruker = brukerRepository.save(Bruker.builder().navIdent("nav").build());

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(savedBruker)
                .medlemmer(singletonList(savedBruker))
                .beskrivelse("besk")
                .build();

        Team savedTeam = teamRepository.save(team);


        Testgruppe testgruppe = Testgruppe.builder()
                .sistEndretAv(savedBruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(savedBruker)
                .teamtilhoerighet(savedTeam)
                .hensikt("hensikt")
                .navn("gruppe")
                .favorisertAv(newHashSet(singletonList(savedBruker)))
                .build();

        gruppeRepository.save(testgruppe);

        Testgruppe foundGruppe = gruppeRepository.findByNavn("gruppe");

        Testident testident = TestidentBuilder.builder().ident("123456789").build().convertToRealTestident();
        testident.setTestgruppe(foundGruppe);
        identRepository.save(testident);

        savedBruker.setFavoritter(newHashSet(singletonList(testgruppe)));
        savedBruker = brukerRepository.save(savedBruker);

        Bruker foundByIdBruker = brukerRepository.findBrukerByNavIdent("nav");
        foundGruppe = gruppeRepository.findByNavn("gruppe");
        Testident ident = identRepository.findByIdent("123456789");

        assertThat(savedBruker.getNavIdent(), is(savedBruker.getNavIdent()));
        assertThat(foundByIdBruker.getNavIdent(), is(savedBruker.getNavIdent()));
        assertThat(foundByIdBruker.getFavoritter().contains(foundGruppe), is(true));

        assertThat(ident.getIdent(), is("123456789"));

        assertThat(foundGruppe.getFavorisertAv().size(), is(1));
        assertThat(foundGruppe.getHensikt(), is("hensikt"));
    }
}