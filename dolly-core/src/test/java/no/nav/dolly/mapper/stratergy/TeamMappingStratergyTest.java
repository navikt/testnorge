package no.nav.dolly.mapper.stratergy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultSet.RsTeam;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.mapper.utils.MapperTestUtils;

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
public class TeamMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mappingFromTeamToRsTeam(){
        Bruker bruker = BrukerBuilder.builder().navIdent("ident").build().convertToRealBruker();
        Bruker brukerEier = BrukerBuilder.builder().navIdent("eier").build().convertToRealBruker();
        Testident testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        Set<Testident> identer = new HashSet<>(Arrays.asList(testident));

        Testgruppe testgruppe = TestgruppeBuilder.builder()
                .navn("Testgruppe")
                .datoEndret(LocalDate.of(2000, 1, 1))
                .id(1L)
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .testidenter(identer)
                .build()
                .convertToRealTestgruppe();

        Team team = TeamBuilder.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(bruker)
                .id(1L)
                .medlemmer(new HashSet<>(Arrays.asList(brukerEier)))
                .beskrivelse("besk")
                .grupper(new HashSet<>(Arrays.asList(testgruppe)))
                .build()
                .convertToRealTeam();

        Team team2 = TeamBuilder.builder()
                .navn("team2")
                .datoOpprettet(LocalDate.of(2010, 1 , 1))
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
        assertThat(rs.getEierNavIdent(), is("ident"));

        List<RsTeam> rsList = mapper.mapAsList(Arrays.asList(team, team2), RsTeam.class);

        assertThat(rsList.get(0).getNavn(), is("team"));
        assertThat(rsList.get(0).getBeskrivelse(), is("besk"));
        assertThat(rsList.get(0).getEierNavIdent(), is("ident"));
        assertThat(rsList.get(0).getMedlemmer().size(), is(1));
        assertThat(rsList.get(0).getDatoOpprettet().getYear(), is(2000));
        assertThat(rsList.get(0).getDatoOpprettet().getMonthValue(), is(1));
        assertThat(rsList.get(0).getDatoOpprettet().getDayOfMonth(), is(1));

        assertThat(rsList.get(1).getNavn(), is("team2"));
        assertThat(rsList.get(1).getBeskrivelse(), is("besk2"));
        assertThat(rsList.get(1).getEierNavIdent(), is("ident"));
        assertThat(rsList.get(1).getMedlemmer().size(), is(2));
        assertThat(rsList.get(1).getDatoOpprettet().getYear(), is(2010));
        assertThat(rsList.get(1).getDatoOpprettet().getMonthValue(), is(1));
        assertThat(rsList.get(1).getDatoOpprettet().getDayOfMonth(), is(1));
    }

}