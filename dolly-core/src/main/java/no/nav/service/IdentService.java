package no.nav.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.resultSet.RsTestident;
import no.nav.jpa.Testident;
import no.nav.dolly.repository.TestGruppeRepository;
import no.nav.dolly.repository.IdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdentService {

	@Autowired
	private IdentRepository identRepository;
	
	@Autowired
	private TestGruppeRepository testGruppeRepository;

	@Autowired
	private TestgruppeService testgruppeService;

	@Autowired
	private MapperFacade mapperFacade;
	
	public void persisterTestidenter(Long gruppeId, List<RsTestident> personIdentListe) {
		List<Testident> testidentList = mapperFacade.mapAsList(personIdentListe, Testident.class);

		identRepository.saveAll(testidentList);
	}
	
	@Transactional
	public void slettTestidenter(List<RsTestident> personIdentSet) {
		personIdentSet.forEach(testident -> identRepository.deleteTestidentByIdent(testident.getIdent()));
	}
}
