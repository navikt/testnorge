package no.nav.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import no.nav.api.resultSet.RsTeam;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import org.junit.Before;
import org.junit.Test;

public class TeamServiceUpdateTeaminfoTest extends AbstractTeamServiceTest {
//	RsTeam teamRequest;
//	Bruker nyEier = new Bruker("nyeierId");
//	Team nyttTeam;
	
	@Before
	public void setupRequest() {
//		teamRequest = new CreateTeamRequest("navn", "beskrivelse", "nyeierId");
//		when(brukerRepository.findBrukerByNavIdent(teamRequest.getEier())).thenReturn(nyEier);
//		when(teamRepository.findTeamById(team.getId())).thenReturn(team);
//
//		nyttTeam = Team.builder()
//				.id(team.getId())
//				.datoOpprettet(team.getDatoOpprettet())
//				.medlemmer(team.getMedlemmer())
//				.grupper(team.getGrupper())
//				.navn(teamRequest.getNavn())
//				.beskrivelse(teamRequest.getBeskrivelse())
//				.eier(nyEier)
//				.build();
	}
	
	@Test
	public void shouldUpdateTeamInfo() {
//		teamService.updateTeamInfo(team.getId(), teamRequest);
//
//		verify(teamRepository).save(eq(nyttTeam));
	}
}