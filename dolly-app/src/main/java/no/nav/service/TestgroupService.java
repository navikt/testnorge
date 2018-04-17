package no.nav.service;

import static no.nav.utilities.TeamUtility.findBrukerOrEierInTeam;

import no.nav.api.request.CreateTestgruppeRequest;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.repository.BrukerRepository;
import no.nav.repository.GruppeRepository;
import no.nav.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TestgroupService {
	
	@Autowired
	GruppeRepository gruppeRepository;
	
	@Autowired
	BrukerRepository brukerRepository;
	
	@Autowired
	TeamRepository teamRepository;
	
	public void opprettTestgruppe(CreateTestgruppeRequest createTestgruppeRequest) {
		Testgruppe testgruppe = konstruerTestgruppe(createTestgruppeRequest);
		gruppeRepository.save(testgruppe);
	}
	
	private Testgruppe konstruerTestgruppe(CreateTestgruppeRequest createTestgruppeRequest) {
		Team team = teamRepository.findTeamById(createTestgruppeRequest.getTilhoererTeamId());
		Bruker opprettetAv = findBrukerOrEierInTeam(createTestgruppeRequest.getOpprettetAvNavIdent(), team);
		if (opprettetAv == null) {
			throw new DollyFunctionalException("Finner ikke navIdent " + createTestgruppeRequest.getOpprettetAvNavIdent() + " i Dolly DB.");
		}
		if (team == null) {
			throw new DollyFunctionalException("Finner ikke team med ID=" + createTestgruppeRequest.getTilhoererTeamId() + " i Dolly DB.");
		}
		return Testgruppe.builder()
				.navn(createTestgruppeRequest.getNavn())
				.opprettetAv(opprettetAv)
				.datoEndret(LocalDateTime.now())
				.sistEndretAv(opprettetAv)
				.teamtilhoerighet(team)
				.build();
	}
	
	
}
