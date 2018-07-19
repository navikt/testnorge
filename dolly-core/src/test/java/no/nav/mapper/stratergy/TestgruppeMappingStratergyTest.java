package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.resultSet.RsTestgruppe;
import no.nav.resultSet.RsTestident;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.mapper.utils.MapperTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TestgruppeMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mappingFromTesgruppeToRsTestgruppe(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Testident testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        Set<Testident> identer = new HashSet<>(Arrays.asList(testident));

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.now())
                .eier(bruker)
                .id(1L)
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

        testident.setTestgruppe(testgruppe);

        List<RsTestident> rsIdenter = mapper.mapAsList(identer, RsTestident.class);
        RsTestgruppe rs = mapper.map(testgruppe, RsTestgruppe.class);

        assertThat(rs.getNavn(), is("gruppe"));
        assertThat(rs.getTestidenter().size(), is(1));
        assertThat(rs.getDatoEndret().getYear(), is(2000));
        assertThat(rs.getDatoEndret().getMonthValue(), is(1));
        assertThat(rs.getDatoEndret().getDayOfMonth(), is(1));
        assertThat(rs.getOpprettetAvNavIdent(), is(bruker.getNavIdent()));
        assertThat(rs.getSistEndretAvNavIdent(), is(bruker.getNavIdent()));

        assertThat(rsIdenter.size(), is(1));
        assertThat(rsIdenter.get(0).getIdent(), is("1"));
    }
}