package no.nav.dolly.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestident;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.repository.IdentRepository;

@Service
public class IdentService {

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private MapperFacade mapperFacade;

    @Transactional
    public void persisterTestidenter(List<RsTestident> personIdentListe) {
        List<Testident> testidentList = mapperFacade.mapAsList(personIdentListe, Testident.class);
        try {
            identRepository.saveAll(testidentList);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Ident DB constraint er brutt! Kan ikke lagre Identer. Error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Testident saveIdentTilGruppe(String hovedPersonIdent, Testgruppe testgruppe) {
        Testident testident = new Testident();
        testident.setIdent(hovedPersonIdent);
        testident.setTestgruppe(testgruppe);
        return identRepository.save(testident);
    }

    public int slettTestident(String ident) {
        return identRepository.deleteTestidentByIdent(ident);
    }

    public int slettTestidenterByGruppeId(Long gruppeId) {
        return identRepository.deleteTestidentByTestgruppeId(gruppeId);
    }
}