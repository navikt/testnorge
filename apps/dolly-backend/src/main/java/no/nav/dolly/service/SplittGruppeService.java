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
                        .filter(bestilling -> isNull(bestilling.getFeil()))
                        .forEach(bestilling -> kopierOgLagre(bestilling, testident.getIdent(), testgruppe)));
    }

    private Bestilling kopierOgLagre(Bestilling bestilling, String ident, Testgruppe testgruppe) {

        var progresser = bestilling.getProgresser().stream()
                .map(SerializationUtils::clone)
                .toList();
        var transaksjonMappinger = bestilling.getTransaksjonmapping().stream()
                .map(SerializationUtils::clone)
                .toList();

        var nyBestilling = SerializationUtils.clone(bestilling);
        nyBestilling.setId(null);
        nyBestilling.setGruppe(testgruppe);
        nyBestilling.setProgresser(null);
        nyBestilling.setTransaksjonmapping(null);
        nyBestilling.setKontroller(null);
        nyBestilling.setAntallIdenter(1);
        nyBestilling.setFerdig(true);
        nyBestilling.setStoppet(false);

        var oppdatertBestilling = bestillingRepository.save(nyBestilling);

        progresser.stream()
                .filter(progress -> ident.equals(progress.getIdent()))
                .forEach(progress -> {
                    bestillingProgressRepository.deleteById(progress.getId());
                    progress.setId(null);
                    progress.setBestilling(oppdatertBestilling);
                    bestillingProgressRepository.save(progress);
                });

        transaksjonMappinger.stream()
                .filter(transaksjonMapping -> ident.equals(transaksjonMapping.getIdent()))
                .forEach(transaksjonMapping -> {
                    transaksjonMappingRepository.deleteById(transaksjonMapping.getId());
                    transaksjonMapping.setId(null);
                    transaksjonMapping.setBestillingId(oppdatertBestilling.getId());
                    transaksjonMappingRepository.save(transaksjonMapping);
                });

        return oppdatertBestilling;
    }
}