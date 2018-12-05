package no.nav.dolly.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;

@Service
public class BestillingService {

    @Autowired
    private BestillingRepository bestillingRepository;

    @Autowired
    private TestgruppeService testgruppeService;

    public Bestilling fetchBestillingById(Long bestillingsId) {
        return bestillingRepository.findById(bestillingsId).orElseThrow(() -> new NotFoundException("Fant ikke bestillingId"));
    }

    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public List<Bestilling> fetchBestillingerByGruppeId(Long gruppeId) {
        return bestillingRepository.findBestillingByGruppe(testgruppeService.fetchTestgruppeById(gruppeId));
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling saveBestillingByGruppeIdAndAntallIdenter(Long gruppeId, int antallIdenter, List<String> miljoer) {
        Testgruppe gruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        StringBuilder miljoeliste = new StringBuilder();
        miljoer.forEach(miljoe -> miljoeliste
                .append(miljoe)
                .append(','));
        return saveBestillingToDB(new Bestilling(gruppe, antallIdenter, LocalDateTime.now(), miljoeliste.substring(0, miljoeliste.length() - 1)));
    }
}