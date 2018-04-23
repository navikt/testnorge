package no.nav.mapper;

import static no.nav.testdata.CreateJpaObjects.createBrukere;
import static no.nav.testdata.CreateJpaObjects.createTeam;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import org.junit.Test;

import java.util.stream.Collectors;

public class MapTeamToResponseTest {
	
	Team team = createTeam(new Bruker("eierId"), createBrukere());
	
	private MapTestgruppeToResponse mockMapTestgruppeToResponse = mock(MapTestgruppeToResponse.class);
	private TeamCustomMapper teamCustomMapper = new TeamCustomMapper(mockMapTestgruppeToResponse);
	private	MapTeamToResponse mapTeamToResponse = new MapTeamToResponse(teamCustomMapper);
	
	@Test
	public void shouldMapTeamContainingNullsWithoutThrowingNullpointerException() {
		mapTeamToResponse.map(new Team());
	}
	
	@Test
	public void shouldMapTeamToTeamResponse() {
		TeamResponse teamResponse = mapTeamToResponse.map(team);
		
		assertTeamResponse(teamResponse);
	}
	
	private void assertTeamResponse(TeamResponse teamResponse) {
		assertEquals(team.getId(), teamResponse.getId());
		assertEquals(team.getNavn(), teamResponse.getNavn());
		assertEquals(team.getBeskrivelse(), teamResponse.getBeskrivelse());
		assertEquals(team.getDatoOpprettet(), teamResponse.getDatoOpprettet());
		assertEquals(team.getEier().getNavIdent(), teamResponse.getEierensNavIdent());
		assertTrue("medlemmer", teamResponse.getMedlemmenesNavIdent()
				.containsAll(team.getMedlemmer().stream().map(Bruker::getNavIdent).collect(Collectors.toSet())));
		assertNotNull("testgrupper", teamResponse.getGrupper());
		assertFalse("testgruppe", teamResponse.getGrupper().isEmpty());
		team.getGrupper().forEach(verify(mockMapTestgruppeToResponse)::map);
	}
	
}