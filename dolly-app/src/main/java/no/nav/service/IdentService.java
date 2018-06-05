package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.api.resultSet.RsTestident;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.repository.GruppeRepository;
import no.nav.repository.IdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class IdentService {

	@Autowired
	IdentRepository identRepository;
	
	@Autowired
	GruppeRepository gruppeRepository;

	@Autowired
	TestgruppeService testgruppeService;

	@Autowired MapperFacade mapperFacade;
	
	public void persisterTestidenter(Long gruppeId, List<RsTestident> personIdentListe) {
		List<Testident> testidentList = mapperFacade.mapAsList(personIdentListe, Testident.class);
//		Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

//		for (RsTestident personIdent : personIdentListe) {
//			testidentList.add();
//		}

		testidentList.forEach(testident -> identRepository.save(testident));
		
	}
	
	@Transactional
	public void slettTestidenter(List<RsTestident> personIdentSet) {
		personIdentSet.forEach(testident -> identRepository.deleteTestidentByIdent(testident.getIdent()));
	}
}
