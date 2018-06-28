package no.nav.appserivces.tpsf.service;

import no.nav.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.IdentRepository;
import no.nav.jpa.Testident;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DollyTpsfService {

    @Autowired
    TpsfApiService tpsfApiService;

    @Autowired
    IdentRepository identRepository;

    public void opprettPersonerByKriterier(Long gruppeId){
        Object res = tpsfApiService.opprettPersoner();

        List<Testident> identer = null;
        identRepository.saveAll(identer);
    }
}
