package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsTeam;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.mapper.utils.MapperTestUtils;
import no.nav.testdata.BrukerBuilder;
import no.nav.testdata.TeamBuilder;
import no.nav.testdata.TestgruppeBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TeamMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TeamMappingStratergy());
    }

    @Test
    public void testy(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .navn("Testgruppe")
                .datoEndret(LocalDateTime.now())
                .id(1L)
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .build()
                .convertToRealTestgruppe();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDateTime.now())
                .eier(bruker)
                .id(1L)
                .medlemmer(new HashSet<>(Arrays.asList(brukerEier)))
                .beskrivelse("besk")
                .grupper(new HashSet<>(Arrays.asList(testgruppe)))
                .build()
                .convertToRealTeam();

        Team team2 = TeamBuilder.builder()
                .navn("team2")
                .datoOpprettet(LocalDateTime.now())
                .eier(bruker)
                .id(2L)
                .medlemmer(new HashSet<>(Arrays.asList(brukerEier, bruker)))
                .beskrivelse("besk2")
                .grupper(new HashSet<>(Arrays.asList(testgruppe)))
                .build()
                .convertToRealTeam();

        testgruppe.setTeamtilhoerighet(team);

        RsTeam rs = mapper.map(team, RsTeam.class);

        assertThat(rs.getNavn(), is("team"));
        assertThat(rs.getBeskrivelse(), is("besk"));
        assertThat(rs.getEier().getNavIdent(), is("ident"));

        List<RsTeam> rsList = mapper.mapAsList(Arrays.asList(team, team2), RsTeam.class);

        assertThat(rsList.get(0).getNavn(), is("team"));
        assertThat(rsList.get(0).getBeskrivelse(), is("besk"));
        assertThat(rsList.get(0).getEier().getNavIdent(), is("ident"));
        assertThat(rsList.get(0).getMedlemmer().size(), is(1));

        assertThat(rsList.get(1).getNavn(), is("team2"));
        assertThat(rsList.get(1).getBeskrivelse(), is("besk2"));
        assertThat(rsList.get(1).getEier().getNavIdent(), is("ident"));
        assertThat(rsList.get(1).getMedlemmer().size(), is(2));
    }

}