package no.nav.service;

import no.nav.dolly.repository.BestillingRepository;
import no.nav.exceptions.ConstraintViolationException;
import no.nav.exceptions.NotFoundException;
import no.nav.jpa.Bestilling;
import no.nav.jpa.Testgruppe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        Optional<Bestilling> bestilling = bestillingRepository.findById(bestillingsId);

        if(bestilling.isPresent()){
            return bestilling.get();
        }

        throw new NotFoundException("Fant ikke bestillingsId");
    }

    public Bestilling saveBestillingToDB(Bestilling b){
        try{
            return bestillingRepository.save(b);
        } catch (DataIntegrityViolationException e){
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage());
        }
    }

    public List<Bestilling> fetchBestillingerByGruppeId(Long gruppeId){
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        return bestillingRepository.findBestillingByGruppe(testgruppe);
    }

    public Bestilling saveBestillingByGruppeIdAndAntallIdenter(Long gruppeId, int antallIdenter, List<String> miljoer){
        Testgruppe gruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        StringBuilder sb = new StringBuilder();
        miljoer.forEach(e -> sb.append(e).append(","));
        return saveBestillingToDB(new Bestilling(gruppe, antallIdenter, LocalDateTime.now(), sb.toString().substring(0, sb.toString().length() - 1)));
    }
}
