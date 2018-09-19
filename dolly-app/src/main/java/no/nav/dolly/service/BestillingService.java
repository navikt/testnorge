package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class BestillingService {

    @Autowired
    private BestillingRepository bestillingRepository;

    @Autowired
    private TestgruppeService testgruppeService;

    public Bestilling fetchBestillingById(Long bestillingsId){
        return bestillingRepository.findById(bestillingsId).orElseThrow(() -> new NotFoundException("Fant ikke bestillingId"));
    }

    public Bestilling saveBestillingToDB(Bestilling b){
        try{
            return bestillingRepository.save(b);
        } catch (DataIntegrityViolationException e){
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage());
        }
    }

    public List<Bestilling> fetchBestillingerByGruppeId(Long gruppeId){
        return bestillingRepository.findBestillingByGruppe(testgruppeService.fetchTestgruppeById(gruppeId));
    }

    public Bestilling saveBestillingByGruppeIdAndAntallIdenter(Long gruppeId, int antallIdenter, List<String> miljoer){
        Testgruppe gruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        StringBuilder sb = new StringBuilder();
        miljoer.forEach(e -> sb.append(e).append(","));
        return saveBestillingToDB(new Bestilling(gruppe, antallIdenter, LocalDateTime.now(), sb.toString().substring(0, (sb.toString().length() - 1) )));
    }
}
