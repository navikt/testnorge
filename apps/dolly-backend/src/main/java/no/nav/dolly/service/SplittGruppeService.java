package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
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

    public void flyttIdenterTilDenneGruppe(Long gruppeId, List<String> identer) {

        var testgruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException(format(GRUPPE_IKKE_FUNNET, gruppeId)));

        var testidenter = identRepository.findByIdentIn(identer);

        kopierBestillinger(testgruppe, testidenter);

        testidenter
                .forEach(testident -> testident.setTestgruppe(testgruppe));
    }

    private void kopierBestillinger(Testgruppe testgruppe, List<Testident> testidenter) {

        testidenter
                .forEach(testident -> bestillingRepository.findBestillingerByIdent(testident.getIdent()).stream()
                        .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()))
                        .filter(bestilling -> isNull(bestilling.getOpprettetFraId()))
                        .forEach(bestilling -> saveCopy(bestilling, testgruppe)));
    }

    private Bestilling saveCopy(Bestilling bestilling, Testgruppe testgruppe) {

        var progresser = bestilling.getProgresser().stream()
                .map(SerializationUtils::clone)
                .toList();
        var transaksjonMappinger = bestilling.getTransaksjonmapping().stream()
                .map(SerializationUtils::clone)
                .toList();
        var kontroller = bestilling.getKontroller().stream()
                .map(SerializationUtils::clone)
                .toList();

        var nyBestilling = SerializationUtils.clone(bestilling);
        nyBestilling.setId(null);
        nyBestilling.setGruppe(testgruppe);
        nyBestilling.setProgresser(null);
        nyBestilling.setTransaksjonmapping(null);
        nyBestilling.setKontroller(null);

        var oppdatertBestilling = bestillingRepository.save(nyBestilling);

        progresser
                .forEach(progress -> {
                    bestillingProgressRepository.deleteById(progress.getId());
                    progress.setId(null);
                    progress.setBestilling(oppdatertBestilling);
                    bestillingProgressRepository.save(progress);
                });

        transaksjonMappinger
                .forEach(transaksjonMapping -> {
                    transaksjonMappingRepository.deleteById(transaksjonMapping.getId());
                    transaksjonMapping.setId(null);
                    transaksjonMapping.setBestillingId(oppdatertBestilling.getId());
                    transaksjonMappingRepository.save(transaksjonMapping);
                });

        kontroller
                .forEach(kontroll -> {
                    bestillingKontrollRepository.deleteById(kontroll.getId());
                    kontroll.setId(null);
                    kontroll.setBestillingId(oppdatertBestilling.getId());
                    bestillingKontrollRepository.save(kontroll);
                });

        return oppdatertBestilling;
    }
}