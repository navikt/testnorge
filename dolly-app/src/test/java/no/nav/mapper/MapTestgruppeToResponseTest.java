package no.nav.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.nav.api.response.TestgruppeResponse;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MapTestgruppeToResponseTest {
	
	private Testgruppe testgruppe = createTestgruppe();
	private MapTestgruppeToResponse mapTestgruppeToResponse = new MapTestgruppeToResponse();
	
	
	@Test
	public void shouldMapTestgruppeToResponse() {
		testgruppe.getTestidenter();
		TestgruppeResponse response = mapTestgruppeToResponse.map(testgruppe);
		
		assertTestgruppeResponse(response);
	}
	
	private void assertTestgruppeResponse(TestgruppeResponse response) {
		assertEquals(testgruppe.getId(), response.getId());
		assertEquals(testgruppe.getNavn(), response.getNavn());
		assertEquals(testgruppe.getTeamtilhoerighet().getNavn(), response.getTilhoererTeamnavn());
		assertEquals(testgruppe.getDatoEndret(), response.getDatoEndret());
		assertEquals(testgruppe.getOpprettetAv().getNavIdent(), response.getOpprettetAvNavIdent());
		assertEquals(testgruppe.getSistEndretAv().getNavIdent(), response.getSistEndretAvNavIdent());
		
		System.out.println(testgruppe.getTestidenter());
		Set<Long> expectedTestidenter = testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors
				.toSet());
		assertTrue("testidenter", response.getTestidenterID().containsAll(expectedTestidenter));
	}
	
	@Test
	public void shouldMapTestgruppeContainingNullsWithoutThrowingNullpointerException() {
		mapTestgruppeToResponse.map(new Testgruppe());
	}
	
	private Testgruppe createTestgruppe() {
		Testgruppe testgruppe = Testgruppe.builder()
				.id(1L)
				.navn("testgr navn")
				.teamtilhoerighet(Team.builder().navn("teamnavn").id(2L).build())
				.datoEndret(LocalDateTime.now())
				.opprettetAv(new Bruker("opprettet av"))
				.sistEndretAv(new Bruker("sist endret"))
				.build();
		Set<Testgruppe> set = new HashSet<>();
		set.add(testgruppe);
		testgruppe.getTeamtilhoerighet().setGrupper(set);
		testgruppe.setTestidenter(createTestidenter(testgruppe));
		return testgruppe;
	}
	
	private Set<Testident> createTestidenter(Testgruppe testgruppe) {
		Set<Testident> testidenter = new HashSet<>();
		testidenter.add(new Testident(11111111111L, testgruppe));
		testidenter.add(new Testident(22222222222L, testgruppe));
		return testidenter;
	}
	
}