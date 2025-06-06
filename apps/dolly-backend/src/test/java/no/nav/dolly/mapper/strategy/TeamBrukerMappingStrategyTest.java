package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeam;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamBrukerMappingStrategyTest {

    @Mock
    private BrukerService brukerService;

    private MapperFacade mapperFacade;

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new TeamBrukerMappingStrategy(brukerService));
    }

    @Test
    void shouldMapTeamToRsTeam() {
        var opprettetAv = Bruker.builder()
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

        var rsTeam = mapperFacade.map(team, RsTeam.class);

        System.out.println(rsTeam);

        assertThat(rsTeam, is(notNullValue()));
        assertThat(rsTeam.getNavn(), is(equalTo("Test Team")));
        assertThat(rsTeam.getBeskrivelse(), is(equalTo("Test team beskrivelse")));
        assertThat(rsTeam.getOpprettetAv(), is(notNullValue()));
        assertThat(rsTeam.getOpprettetAv().getBrukerId(), is(equalTo("bruker123")));
        assertThat(rsTeam.getBrukere(), hasSize(2));
    }

    @Test
    void shouldMapRsTeamToTeamConvertingBrukerIdsToEntities() {
        var currentUser = Bruker.builder()
                .brukerId("current123")
                .brukernavn("Current User")
                .build();

        var bruker1 = Bruker.builder()
                .brukerId("user1")
                .brukernavn("User One")
                .build();

        var bruker2 = Bruker.builder()
                .brukerId("user2")
                .brukernavn("User Two")
                .build();

        var rsTeam = RsTeam.builder()
                .id(1L)
                .navn("Test Team")
                .beskrivelse("Test team description")
                .brukere(new HashSet<>(Set.of("user1", "user2")))
                .build();

        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(currentUser);
        when(brukerService.fetchBrukerWithoutTeam("user1")).thenReturn(bruker1);
        when(brukerService.fetchBrukerWithoutTeam("user2")).thenReturn(bruker2);

        var team = mapperFacade.map(rsTeam, Team.class);

        assertThat(team, is(notNullValue()));
        assertThat(team.getNavn(), is(equalTo("Test Team")));
        assertThat(team.getBeskrivelse(), is(equalTo("Test team description")));
        assertThat(team.getOpprettetAv(), is(equalTo(currentUser)));
        assertThat(team.getBrukere(), hasSize(2));
        assertThat(team.getBrukere(), containsInAnyOrder(bruker1, bruker2));
    }
}