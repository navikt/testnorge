package no.nav.service;

import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.repository.GruppeRepository;
import no.nav.repository.IdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestgroupService {
	
	@Autowired
	IdentRepository identRepository;
	
	@Autowired
	GruppeRepository gruppeRepository;
	
	public void persisterTestidenter(Long gruppeId, List<Long> personIdentListe) {
		List<Testident> testidentList = new ArrayList<>();
		Testgruppe testgruppe= gruppeRepository.findById(gruppeId);
		if (testgruppe != null) {
			for (Long personIdent : personIdentListe) {
				testidentList.add(new Testident(personIdent, testgruppe));
			}
			testidentList.forEach(testident -> identRepository.save(testident));
		} else {
			throw new RuntimeException("Testgruppe " + gruppeId+ " finnes ikke i DollyDB.");
		}
	}
}
