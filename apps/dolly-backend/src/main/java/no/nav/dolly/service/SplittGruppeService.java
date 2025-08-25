package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.BestillingMapper;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
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

    public Mono<Void> flyttIdenterTilDenneGruppe(Long gruppeId, Set<String> identer) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(format(GRUPPE_IKKE_FUNNET, gruppeId))))
                .flatMap(testgruppe -> bestillingRepository.findBestillingerByIdentIn(identer)
                        .filter(bestilling ->
                                isNull(bestilling.getOpprettetFraGruppeId()) &&
                                        isNull(bestilling.getOpprettetFraId()) &&
                                        isNull(bestilling.getGjenopprettetFraIdent()))
                        .flatMap(bestilling -> moveBestilling(bestilling, identer, testgruppe))
                        .collectList()
                        .thenReturn(testgruppe))
                .flatMap(testgruppe -> moveIdenter(identer, testgruppe));
    }

    private Mono<Void> moveIdenter(Set<String> identer, Testgruppe testgruppe) {

        return identRepository.findByIdentIn(identer)
                .sort(Comparator.comparing(Testident::getId))
                .flatMap(testident -> {
                    var nyTestident = SerializationUtils.clone(testident);
                    nyTestident.setGruppeId(testgruppe.getId());
                    nyTestident.setId(null);
                    return identRepository.deleteById(testident.getId())
                            .then(identRepository.save(nyTestident));
                })
                .collectList()
                .then();
    }

    private Mono<Bestilling> moveBestilling(Bestilling bestilling, Set<String> identer, Testgruppe testgruppe) {

        var nyBestilling = BestillingMapper.shallowCopyBestilling(bestilling);
        nyBestilling.setId(null);
        nyBestilling.setGruppeId(testgruppe.getId());
        return bestillingRepository.save(nyBestilling)
                .flatMap(oppdatertBestilling ->
                        transaksjonMappingRepository.findAllByBestillingId(oppdatertBestilling.getId())
                                .flatMap(transaksjonMapping -> {
                                    if (identer.contains(transaksjonMapping.getIdent())) {
                                        var nyTransaksjonMapping = SerializationUtils.clone(transaksjonMapping);
                                        nyTransaksjonMapping.setId(null);
                                        nyTransaksjonMapping.setBestillingId(oppdatertBestilling.getId());
                                        return transaksjonMappingRepository.deleteById(transaksjonMapping.getId())
                                                .then(transaksjonMappingRepository.save(nyTransaksjonMapping));
                                    }
                                    return Mono.empty();
                                })
                                .collectList()
                                .then(bestillingProgressRepository.findByBestillingId(bestilling.getId())
                                        .flatMap(progress -> {
                                            if (identer.contains(progress.getIdent())) {
                                                var nyProgress = SerializationUtils.clone(progress);
                                                nyProgress.setId(null);
                                                nyProgress.setBestillingId(oppdatertBestilling.getId());
                                                return bestillingProgressRepository.deleteById(progress.getId())
                                                        .then(bestillingProgressRepository.save(nyProgress));
                                            }
                                            return Mono.empty();
                                        })
                                        .collectList()
                                        .flatMap(progresser ->
                                                deleteKildeBestilling(oppdatertBestilling)
                                                        .thenReturn(oppdatertBestilling)
                                                        .flatMap(oppdatert -> {
                                                            oppdatertBestilling.setAntallIdenter(nyBestilling.getAntallIdenter() - progresser.size());
                                                            return bestillingRepository.save(oppdatertBestilling);
                                                        }))));
    }

    private Mono<Void> deleteKildeBestilling(Bestilling bestilling) {

        return bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestilling.getId())
                        .then(bestillingRepository.deleteById(bestilling.getId()));
    }
}