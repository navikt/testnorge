package no.nav.dolly.mapper.stratergy;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsTestgruppe;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.testdata.builder.TestidentBuilder;

public class BrukerMappingStratergyTest {

    private MapperFacade mapper;

    @Before
    public void setUpHappyPath() {
        mapper = MapperTestUtils.createMapperFacadeForMappingStrategy(new TestgruppeMappingStrategy(), new TeamMappingStrategy());
    }

    @Test
    public void testy(){
        Bruker bruker = Bruker.builder().navIdent("ident").build();
        Testident testident = TestidentBuilder.builder().ident("1").build().convertToRealTestident();
        Set<Testident> identer = newHashSet(singletonList(testident));

        Team team = Team.builder()
                .navn("team")
                .datoOpprettet(LocalDate.of(2000, 1, 1))
                .eier(bruker)
                .id(1L)
                .medlemmer(newHashSet(singletonList(bruker)))
                .beskrivelse("besk")
                .build();

        Testgruppe testgruppe = Testgruppe.builder()
                .sistEndretAv(bruker)
                .datoEndret(LocalDate.of(2000, 1, 1))
                .opprettetAv(bruker)
                .id(2L)
                .testidenter(identer)
                .navn("gruppe")
                .teamtilhoerighet(team)
                .build();

        bruker.setFavoritter(newHashSet(singletonList(testgruppe)));

        RsBruker rsBruker = mapper.map(bruker, RsBruker.class);

        ArrayList<RsTestgruppe> favoritterInBruker = new ArrayList(rsBruker.getFavoritter());

        assertThat(bruker.getNavIdent(), is("ident"));
        assertThat(rsBruker.getNavIdent(), is("ident"));
        assertThat(favoritterInBruker.get(0).getNavn(), is("gruppe"));
        assertThat(favoritterInBruker.get(0).getId(), is(2L));
        assertThat(favoritterInBruker.get(0).getTeam().getNavn(), is(team.getNavn()));

    }
}