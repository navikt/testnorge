package no.nav.service;

import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.repository.GruppeRepository;
import no.nav.repository.IdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class IdentService {
	@Autowired
	IdentRepository identRepository;
	
	@Autowired
	GruppeRepository gruppeRepository;
	
	public void persisterTestidenter(Long gruppeId, Set<Long> personIdentListe) {
		List<Testident> testidentList = new ArrayList<>();
		Testgruppe testgruppe = gruppeRepository.findById(gruppeId);
		if (testgruppe == null) {
			throw new DollyFunctionalException("Testgruppe " + gruppeId + " finnes ikke i DollyDB.");
		}
		for (Long personIdent : personIdentListe) {
			testidentList.add(new Testident(personIdent, testgruppe));
		}
		testidentList.forEach(testident -> identRepository.save(testident));
		
	}
	
	@Transactional
	public void slettTestidenter(Set<Long> personIdentSet) {
		personIdentSet.forEach(testident -> identRepository.deleteTestidentByIdent(testident));
	}
}
