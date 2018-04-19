package no.nav.mapper;

import static no.nav.testdata.CreateJpaObjects.createBrukere;
import static no.nav.testdata.CreateJpaObjects.createTeam;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import no.nav.api.response.BrukerResponse;
import no.nav.jpa.Bruker;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class MapBrukerToResponseTest {
	Bruker eier = createEier();
	
	private Bruker createEier() {
		Bruker eier = new Bruker("eierId");
		createTeam(eier, createBrukere());
		Set<Bruker> medlem = new HashSet<>();
		medlem.add(eier);
		createTeam(new Bruker("annenId"), medlem);
		
		return eier;
	}
	
	@Test
	public void shouldMapBrukerContainingNullsWithoutThrowingNullpointerException() {
		MapBrukerToResponse.map(new Bruker());
	}
	
	@Test
	public void shouldMapBrukerToResponse() {
		BrukerResponse brukerResponse = MapBrukerToResponse.map(eier);
		
		assertBrukerResponseMapping(eier, brukerResponse);
	}
	
	private void assertBrukerResponseMapping(Bruker bruker, BrukerResponse brukerResponse) {
		assertEquals(bruker.getNavIdent(), brukerResponse.getNavIdent());
		assertNotNull("brukerens teameierskap", brukerResponse.getTeamEierskap());
		assertFalse("er brukerens teameierskap tom?", bruker.getTeamEierskap().isEmpty());
		assertNotNull("brukerens teammedlemsskap", brukerResponse.getTeamMedlemskap());
		assertFalse("er brukerens teammedlemskap tom?", bruker.getTeamMedlemskap().isEmpty());
	}
}