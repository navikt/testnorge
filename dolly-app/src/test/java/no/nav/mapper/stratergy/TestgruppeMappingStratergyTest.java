package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsTestgruppe;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.mapper.utils.MapperTestUtils;
import no.nav.testdata.BrukerBuilder;
import no.nav.testdata.TeamBuilder;
import no.nav.testdata.TestgruppeBuilder;
import no.nav.testdata.TestidentBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStratergy());
    }

    @Test
    public void testy(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Set<Testident> identer = new HashSet<>(Arrays.asList(TestidentBuilder.builder().ident(1L).build().convertToRealTestident()));

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDateTime.now())
                .eier(bruker)
                .id(1L)
                .beskrivelse("besk")
                .build()
                .convertToRealTeam();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .sistEndretAv(bruker)
                .opprettetAv(bruker)
                .id(2L)
                .testidenter(identer)
                .navn("gruppe")
                .teamtilhoerighet(team)
                .build()
                .convertToRealTestgruppe();

        RsTestgruppe rs = mapper.map(testgruppe, RsTestgruppe.class);

        assertThat(rs.getNavn(), is("gruppe"));
        assertThat(rs.getTestidenter().size(), is(1));

    }
}