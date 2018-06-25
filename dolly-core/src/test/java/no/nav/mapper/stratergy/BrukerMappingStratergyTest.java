package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.mapper.utils.MapperTestUtils;
import no.nav.resultSet.RsBruker;
import no.nav.resultSet.RsTestgruppe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class BrukerMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void testy(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Testident testident = TestidentBuilder.builder().ident(1L).build().convertToRealTestident();
        Set<Testident> identer = new HashSet<>(Arrays.asList(testident));

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(bruker)
                .id(1L)
                .medlemmer(new HashSet<>(Arrays.asList(bruker)))
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .testidenter(identer)
                .navn("gruppe")
                .teamtilhoerighet(team)
                .build()
                .convertToRealTestgruppe();

        bruker.setFavoritter(new HashSet<>(Arrays.asList(testgruppe)));

        RsBruker rs = mapper.map(bruker, RsBruker.class);

        ArrayList<RsTestgruppe> favoritterInBruker = new ArrayList(rs.getFavoritter());

        assertThat(bruker.getNavIdent(), is("ident"));
        assertThat(rs.getNavIdent(), is("ident"));
        assertThat(favoritterInBruker.get(0).getNavn(), is("gruppe"));
        assertThat(favoritterInBruker.get(0).getId(), is(2L));
        assertThat(favoritterInBruker.get(0).getTeamTilhoerlighetNavn(), is(team.getNavn()));

    }
}