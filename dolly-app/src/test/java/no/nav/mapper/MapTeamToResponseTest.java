package no.nav.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class MapTeamToResponseTest {
	
	Team team = createTeam();
	
	
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
		team.getBrukere().forEach(bruker ->
				assertTrue("brukerId", teamResponse.getBrukernesNavIdent().contains(bruker.getNavIdent())));
//	TODO	team.getGrupper().forEach(testgruppe ->				assertTrue("brukerId", teamResponse.().contains(bruker.getNavIdent())));
	}
	
	private Team createTeam() {
		Set<Bruker> brukere = new HashSet<>();
		brukere.add(new Bruker("bruker1"));
		brukere.add(new Bruker("bruker2"));
		
		Set<Testgruppe> testgrupper = new HashSet<>();
		
		return Team.builder()
				.id(1L)
				.navn("teamnavn")
				.beskrivelse("beskrivelse her")
				.datoOpprettet(LocalDateTime.now())
				.eier(new Bruker("eierId"))
				.brukere(brukere)
//				.grupper() TODO
				.build();
	}
}