package no.nav.service;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import no.nav.api.request.CreateTeamRequest;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.repository.BrukerRepository;
import no.nav.repository.TeamRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {
	@Mock
	TeamRepository teamRepository;

	@Mock
	BrukerRepository brukerRepository;
	@InjectMocks
	TeamService teamService;

	Bruker eier = new Bruker("eierId");
	Team team = Team.builder().eier(eier).navn("teamnavn").datoOpprettet(LocalDateTime.now()).build();
	CreateTeamRequest createTeamRequest = new CreateTeamRequest(team.getNavn(), team.getBeskrivelse(), team.getEier()
			.getNavIdent());
	
	@Before
	public void setupMocks() {
		when(brukerRepository.findBrukerByNavIdent(anyString())).thenReturn(eier);
	}
	
	@Test
	public void shouldMapOgOppretteTeam() {
		teamService.opprettTeam(createTeamRequest);
		ArgumentCaptor<Team> argument = ArgumentCaptor.forClass(Team.class);
		Mockito.verify(teamRepository).save(argument.capture());
		assertEquals(team.getNavn(),argument.getValue().getNavn());
		assertEquals(team.getBeskrivelse(),argument.getValue().getBeskrivelse());
		assertEquals(team.getEier(),argument.getValue().getEier());
//		assertThat(argument.getValue().getDatoOpprettet(),is(leq(LocalDateTime.now()))); //FIXME
	}
}