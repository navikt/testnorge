package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.projection.GruppeBestillingIdent;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String IDENT_NOT_FOUND = "Testperson med ident %s ble ikke funnet";

    private final IdentRepository identRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingRepository bestillingRepository;
    private final MapperFacade mapperFacade;
    private final PersonService personService;
    private final BestillingService bestillingService;

    public Mono<Testident> fetchTestident(String ident) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

    @Transactional(readOnly = true)
    public Mono<Boolean> exists(String ident) {

        return identRepository.existsByIdent(ident);
    }

    @Transactional
    public Mono<Testident> saveIdentTilGruppe(String ident, Long gruppeId, Master master, String beskrivelse) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.just(Testident.builder()
                                .ident(ident)
                                .gruppeId(gruppeId)
                                .master(master)
                                .beskrivelse(beskrivelse)
                                .iBruk(false)
                                .build())
                        .flatMap(identRepository::save))
                .map(testident -> {
                    testident.setGruppeId(gruppeId);
                    testident.setMaster(master);
                    testident.setBeskrivelse(beskrivelse);
                    return testident;
                })
                .flatMap(identRepository::save);
    }

    public Mono<Void> slettTestident(String ident) {

        return fetchTestident(ident)
                .map(testident -> mapperFacade.map(testident, TestidentDTO.class))
                .flatMap(testident -> personService.recyclePersoner(List.of(testident)))
                .then(transaksjonMappingRepository.deleteAllByIdent(ident))
                .then(bestillingService.slettBestillingByTestIdent(ident))
                .then(identRepository.deleteTestidentByIdent(ident));
    }

    @Transactional
    public Mono<Testident> saveIdentIBruk(String ident, boolean iBruk) {

        return fetchTestident(ident)
                .map(testident -> {
                    testident.setIBruk(iBruk);
                    return testident;
                })
                .flatMap(identRepository::save);
    }

    @Transactional
    public Mono<Testident> saveIdentBeskrivelse(String ident, String beskrivelse) {

        return fetchTestident(ident)
                .map(testident -> {
                    testident.setBeskrivelse(beskrivelse);
                    return testident;
                })
                .flatMap(identRepository::save);
    }

    @Transactional
    public Mono<Testident> swapIdent(String oldIdent, String newIdent) {

        return identRepository.swapIdent(oldIdent, newIdent);
    }

    public Flux<Testident> getTestidenterByGruppeId(Long gruppeId) {

        return identRepository.findByGruppeId(gruppeId, Pageable.unpaged());
    }

    public Mono<Page<Testident>> getTestidenterFromGruppePaginert(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        if (StringUtils.isBlank(sortColumn)) {
            sortColumn = "id";
        }
        var retning = "asc".equals(sortRetning) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var page = PageRequest.of(pageNo, pageSize,
                Sort.by(
                        new Sort.Order(retning, sortColumn, Sort.NullHandling.NULLS_LAST),
                        new Sort.Order(Sort.Direction.DESC, "id", Sort.NullHandling.NULLS_LAST)
                )
        );

        return identRepository.findByGruppeId(gruppeId, page)
                .collectList()
                .flatMap(testidenter -> {
                    var identer = testidenter.stream()
                            .map(Testident::getIdent)
                            .toList();

                    return bestillingProgressRepository.findByIdentIn(identer)
                            .collectList()
                            .flatMap(progresser -> Flux.fromIterable(progresser)
                                    .map(BestillingProgress::getBestillingId)
                                    .collectList()
                                    .flatMap(bestillingIds -> bestillingRepository.findByIdIn(bestillingIds)
                                            .collectList()
                                            .zipWith(Mono.just(progresser))))
                            .flatMap(tuple ->
                                    Flux.fromIterable(tuple.getT1())
                                            .collectMap(Bestilling::getId, bestilling -> bestilling)
                                            .zipWith(Flux.fromIterable(tuple.getT2())
                                                    .reduce(new HashMap<String, List<BestillingProgress>>(), (map, bestillingProgress) -> {
                                                        map.computeIfAbsent(bestillingProgress.getIdent(), k -> new ArrayList<>())
                                                                .add(bestillingProgress);
                                                        return map;
                                                    }))
                                            .zipWith(Flux.fromIterable(tuple.getT2())
                                                    .reduce(new HashMap<Long, List<BestillingProgress>>(), (map, bestillingProgress) -> {
                                                        map.computeIfAbsent(bestillingProgress.getBestillingId(), k -> new ArrayList<>())
                                                                .add(bestillingProgress);
                                                        return map;
                                                    })))
                            .flatMap(tuple -> {

                                tuple.getT1().getT1().keySet().forEach(bestillingId -> {
                                    tuple.getT1().getT1().get(bestillingId).setProgresser(tuple.getT2().get(bestillingId));
                                    tuple.getT2().get(bestillingId)
                                            .forEach(bestillingProgress ->
                                                    bestillingProgress.setBestilling(tuple.getT1().getT1().get(bestillingId)));
                                });
                                testidenter.forEach(testident ->
                                        testident.setBestillingProgress(tuple.getT1().getT2().get(testident.getIdent())));

                                return Mono.just(testidenter);
                            });
                })
                .flatMap(testidenter -> identRepository.countByGruppeId(gruppeId)
                        .map(count -> new PageImpl<>(testidenter, page, count)));
    }

    public Mono<Integer> getPaginertIdentIndex(String ident, Long gruppeId) {

        return identRepository.getPaginertTestidentIndex(ident, gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

    public Mono<Testident> getTestIdent(String ident) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

    public Mono<FinnesDTO> exists(List<String> identer) {

        return identRepository.findByIdentIn(identer)
                .map(Testident::getIdent)
                .collectList()
                .flatMap(finnes -> Flux.fromIterable(identer)
                        .collect(Collectors.toMap(ident -> ident, finnes::contains)))
                .map(finnesMap -> FinnesDTO.builder()
                        .iBruk(finnesMap)
                        .build());
    }
}
