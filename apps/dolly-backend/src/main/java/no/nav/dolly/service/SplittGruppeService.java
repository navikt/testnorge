package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class SplittGruppeService {

    private static final String GRUPPE_IKKE_FUNNET = "Testgruppe med id #%d ble ikke funnet.";

    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final TestgruppeRepository testgruppeRepository;
    private final IdentRepository identRepository;
    private final BestillingKontrollRepository bestillingKontrollRepository;

    public void flyttIdenterTilDenneGruppe(Long gruppeId, Set<String> identer) {

        var testgruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException(format(GRUPPE_IKKE_FUNNET, gruppeId)));

        bestillingRepository.findBestillingerByIdentIn(identer).stream()
                .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()))
                .filter(bestilling -> isNull(bestilling.getOpprettetFraId()))
                .filter(bestilling -> isNull(bestilling.getFeil()))
                .forEach(bestilling -> moveBestillinger(bestilling, identer, testgruppe));

        moveIdenter(identer, testgruppe);
    }

    private void moveIdenter(Set<String> identer, Testgruppe testgruppe) {

        identRepository.findByIdentIn(identer)
                .forEach(testident -> {
                    var nyTestident = SerializationUtils.clone(testident);
                    identRepository.deleteById(testident.getId());
                    nyTestident.setTestgruppe(testgruppe);
                    nyTestident.setId(null);
                    identRepository.save(nyTestident);
                });
    }

    private Bestilling moveBestillinger(Bestilling bestilling, Set<String> identer, Testgruppe testgruppe) {

        var nyeProgresser = bestilling.getProgresser().stream()
                .filter(progress -> identer.contains(progress.getIdent()))
                .map(SerializationUtils::clone)
                .toList();
        var nyeTransaksjonMappinger = bestilling.getTransaksjonmapping().stream()
                .filter(mapping -> identer.contains(mapping.getIdent()))
                .map(SerializationUtils::clone)
                .toList();

        var nyBestilling = SerializationUtils.clone(bestilling);
        nyBestilling.setId(null);
        nyBestilling.setGruppe(testgruppe);
        nyBestilling.setProgresser(null);
        nyBestilling.setTransaksjonmapping(null);
        nyBestilling.setKontroller(null);
        nyBestilling.setAntallIdenter(nyeProgresser.size());
        nyBestilling.setFerdig(true);
        nyBestilling.setStoppet(false);

        var oppdatertBestilling = bestillingRepository.save(nyBestilling);

        nyeProgresser.stream()
                .forEach(progress -> {
                    bestillingProgressRepository.deleteById(progress.getId());
                    progress.setId(null);
                    progress.setBestilling(oppdatertBestilling);
                    bestillingProgressRepository.save(progress);
                });

        nyeTransaksjonMappinger.stream()
                .forEach(transaksjonMapping -> {
                    transaksjonMappingRepository.deleteById(transaksjonMapping.getId());
                    transaksjonMapping.setId(null);
                    transaksjonMapping.setBestillingId(oppdatertBestilling.getId());
                    transaksjonMappingRepository.save(transaksjonMapping);
                });

        deleteKildeBestilling(bestilling, nyeProgresser);

        return oppdatertBestilling;
    }

    private void deleteKildeBestilling(Bestilling bestilling, List<BestillingProgress> progresser) {

        if (bestilling.getProgresser().size() == progresser.size()) {
            bestilling.getKontroller()
                    .forEach(bestillingKontroll -> bestillingKontrollRepository.deleteById(bestilling.getId()));
            bestillingRepository.deleteById(bestilling.getId());

        } else {
            bestilling.setAntallIdenter(bestilling.getProgresser().size() - progresser.size());
        }
    }
}