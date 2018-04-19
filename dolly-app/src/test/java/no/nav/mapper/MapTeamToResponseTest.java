package no.nav.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import no.nav.api.response.TeamResponse;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MapTeamToResponseTest {
	
	Team team = createTeam();
	
	@Test
	public void shouldMapTeamContainingNullsWithoutThrowingNullpointerException() {
		Team emptyteam = new Team();
		MapTeamToResponse.map(emptyteam);
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
	
	private Team createTeam() {
		Set<Bruker> brukere = new HashSet<>();
		brukere.add(new Bruker("bruker1"));
		brukere.add(new Bruker("bruker2"));
		
		Set<Testgruppe> testgrupper = new HashSet<>();
		testgrupper.add(Testgruppe.builder().navn("testgr").build());
		Team team = Team.builder()
				.id(1L)
				.navn("teamnavn")
				.beskrivelse("beskrivelse her")
				.datoOpprettet(LocalDateTime.now())
				.eier(new Bruker("eierId"))
				.brukere(brukere)
				.grupper(testgrupper)
				.build();
		Set<Team> eierskap = new HashSet<>();
		eierskap.add(team);
		team.getEier().setTeamEierskap(eierskap);
		return team;
	}
}