package no.nav.mapper;

import static no.nav.testdata.CreateJpaObjects.createBrukere;
import static no.nav.testdata.CreateJpaObjects.createTeam;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import org.junit.Test;

import java.util.stream.Collectors;

public class MapTeamToResponseTest {
	
	Team team = createTeam(new Bruker("eierId"), createBrukere());
	
	@Test
	public void shouldMapTeamContainingNullsWithoutThrowingNullpointerException() {
		MapTeamToResponse.map(new Team());
	}
	
	@Test
	public void shouldMapTeamToTeamResponse() {
		TeamResponse teamResponse = MapTeamToResponse.map(team);
		
		assertTeamResponse(teamResponse);
	}
	
	private void assertTeamResponse(TeamResponse teamResponse) {
		assertEquals(team.getId(), teamResponse.getId());
		assertEquals(team.getNavn(), teamResponse.getNavn());
		assertEquals(team.getBeskrivelse(), teamResponse.getBeskrivelse());
		assertEquals(team.getDatoOpprettet(), teamResponse.getDatoOpprettet());
		assertEquals(team.getEier().getNavIdent(), teamResponse.getEierensNavIdent());
		assertTrue("brukere", teamResponse.getBrukernesNavIdent()
				.containsAll(team.getBrukere().stream().map(Bruker::getNavIdent).collect(Collectors.toSet())));
		assertNotNull("testgrupper", teamResponse.getGrupper());
		assertFalse("testgruppe", teamResponse.getGrupper().isEmpty());
	}
	
}