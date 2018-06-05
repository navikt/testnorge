package no.nav.junk;

import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CreateJpaObjects {
	public static Set<Bruker> createBrukere() {
		Set<Bruker> brukere = new HashSet<>();
//		brukere.add(new Bruker("bruker1"));
//		brukere.add(new Bruker("bruker2"));
		return brukere;
	}
	
	public static Team createTeam(Bruker eier, Set<Bruker> brukere) {
		
//		Set<Testgruppe> testgrupper = new HashSet<>();
//		testgrupper.add(Testgruppe.builder().navn("testgr").build());
//		Team team = Team.builder()
//				.id(1L)
//				.navn("teamnavn")
//				.beskrivelse("beskrivelse her")
//				.datoOpprettet(LocalDateTime.now())
//				.eier(eier)
//				.medlemmer(brukere)
//				.grupper(testgrupper)
//				.build();
//
//		Set<Team> eierskap = new HashSet<>();
//		eierskap.add(team);

//		team.getEier().setTeamEierskap(eierskap);
		
//		Set<Team> medlemskap = new HashSet<>();
//		medlemskap.add(team);
//		team.getMedlemmer().forEach(bruker -> bruker.setTeamMedlemskap(medlemskap));
		
		return new Team();
	}
}
