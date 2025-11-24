package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
class TeamBrukerMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new TeamBrukerMappingStrategy());
    }

    @Test
    void shouldMapTeamToRsTeamWithBrukere() {

        var opprettetAv = Bruker.builder()
                .id(1L)
                .brukerId("bruker123")
                .brukernavn("Oppretter")
                .build();

        var bruker1 = Bruker.builder()
                .brukerId("bruker1")
                .brukernavn("Bruker En")
                .build();

        var bruker2 = Bruker.builder()
                .brukerId("bruker2")
                .brukernavn("Bruker To")
                .build();

        var team = Team.builder()
                .id(1L)
                .navn("Test Team")
                .beskrivelse("Test team beskrivelse")
                .opprettetAv(opprettetAv)
                .brukere(Set.of(bruker1, bruker2))
                .build();

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("brukerId", "bruker123");

        var rsTeam = mapperFacade.map(team, RsTeamWithBrukere.class, context);

        assertThat(rsTeam, is(notNullValue()));
        assertThat(rsTeam.getNavn(), is(equalTo("Test Team")));
        assertThat(rsTeam.getBeskrivelse(), is(equalTo("Test team beskrivelse")));
        assertThat(rsTeam.getOpprettetAv(), is(notNullValue()));
        assertThat(rsTeam.getOpprettetAv().getBrukerId(), is(equalTo("bruker123")));
        assertThat(rsTeam.getBrukere(), hasSize(2));
    }
}