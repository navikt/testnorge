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
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestidentRepositoryTest {

    @Autowired
    BrukerRepository brukerRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TestGruppeRepository testGruppeRepository;

    @Autowired
    IdentRepository identRepository;

    @Test
    public void saveTestidentTilGruppe() {
        Testident testident = TestidentBuilder.builder().ident(12345L).build().convertToRealTestident();
        Bruker bruker = brukerRepository.save(BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker());

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.now())
                .eier(bruker)
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .navn("gruppe")
                .teamtilhoerighet(team)
                .testidenter(new HashSet<>(Arrays.asList(testident)))
                .build()
                .convertToRealTestgruppe();

        team.setGrupper(new HashSet<>(Arrays.asList(testgruppe)));
        testident.setTestgruppe(testgruppe);

        teamRepository.save(team);

        Testident ident = identRepository.findByIdent(12345L);

        assertThat(ident.getIdent(), is(12345L));
        assertThat(ident.getTestgruppe().getNavn(), is("gruppe"));

    }

}
