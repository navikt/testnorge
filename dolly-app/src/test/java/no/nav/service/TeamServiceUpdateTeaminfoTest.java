package no.nav.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.api.request.CreateTeamRequest;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import org.junit.Before;
import org.junit.Test;

public class TeamServiceUpdateTeaminfoTest extends AbstractTeamServiceTest {
	CreateTeamRequest teamRequest;
	Bruker nyEier = new Bruker("nyeierId");
	Long teamId = 123L;
	Team nyttTeam;
	
	@Before
	public void setupRequest() {
		teamRequest = new CreateTeamRequest("navn", "beskrivelse", "nyeierId");
		when(brukerRepository.findBrukerByNavIdent(teamRequest.getEierensNavIdent())).thenReturn(nyEier);
		when(teamRepository.findTeamById(teamId)).thenReturn(team);
		
		nyttTeam = Team.builder()
				.id(team.getId())
				.datoOpprettet(team.getDatoOpprettet())
				.medlemmer(team.getMedlemmer())
				.grupper(team.getGrupper())
				.navn(teamRequest.getNavn())
				.beskrivelse(teamRequest.getBeskrivelse())
				.eier(nyEier)
				.build();
	}
	
	@Test
	public void shouldUpdateTeamInfo() {
		teamService.updateTeamInfo(teamId, teamRequest);
		
		verify(teamRepository).save(eq(nyttTeam));
	}
}