package no.nav.dolly.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;

@Service
public class BestillingService {

    @Autowired
    private BestillingRepository bestillingRepository;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

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
        return bestillingRepository.findBestillingByGruppeOrderByGruppe(testgruppeService.fetchTestgruppeById(gruppeId));
    }

    public Bestilling cancelBestilling(Long bestillingId) {
        Optional<BestillingKontroll> bestillingKontroll = bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId);
        if (!bestillingKontroll.isPresent()) {
            bestillingKontrollRepository.save(BestillingKontroll.builder()
                    .bestillingId(bestillingId)
                    .stoppet(true)
                    .build());
        }

        Bestilling bestilling = fetchBestillingById(bestillingId);
        bestilling.setStoppet(true);
        bestilling.setFerdig(true);
        bestilling.setSistOppdatert(LocalDateTime.now());
        saveBestillingToDB(bestilling);
        identRepository.deleteTestidentsByBestillingId(bestillingId);
        bestillingProgressRepository.deleteByBestillingId(bestillingId);
        return bestilling;
    }

    public boolean isStoppet(Long bestillingId) {
        return bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId).orElse(BestillingKontroll.builder().stoppet(false).build()).isStoppet();
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