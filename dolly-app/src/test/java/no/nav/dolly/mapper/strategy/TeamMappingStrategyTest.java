package no.nav.dolly.mapper.strategy;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.testdata.builder.TestidentBuilder;

public class TeamMappingStrategyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void mappingFromTeamToRsTeam(){
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Bruker brukerEier = Bruker.builder().navIdent("eier").build();
        Testident testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        Set<Testident> identer = newHashSet(singletonList(testident));

        Testgruppe testgruppe = Testgruppe.builder()
                .navn("Testgruppe")
                .datoEndret(LocalDate.of(2000, 1, 1))
                .id(1L)
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .testidenter(identer)
                .build();

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(bruker)
                .id(1L)
                .medlemmer(newHashSet(singletonList(brukerEier)))
                .beskrivelse("besk")
                .grupper(newHashSet(singletonList(testgruppe)))
                .build();

        Team team2 = Team.builder()
                .navn("team2")
                .datoOpprettet(LocalDate.of(2010, 1 , 1))
                .eier(bruker)
                .id(2L)
                .medlemmer(newHashSet(asList(brukerEier, bruker)))
                .beskrivelse("besk2")
                .grupper(newHashSet(singletonList(testgruppe)))
                .build();

        testgruppe.setTeamtilhoerighet(team);

        RsTeam rs = mapper.map(team, RsTeam.class);

        assertThat(rs.getNavn(), is("team"));
        assertThat(rs.getBeskrivelse(), is("besk"));
        assertThat(rs.getEierNavIdent(), is("ident"));

        List<RsTeamUtvidet> rsList = mapper.mapAsList(asList(team, team2), RsTeamUtvidet.class);

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