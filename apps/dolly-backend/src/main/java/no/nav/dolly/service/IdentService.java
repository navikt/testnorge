package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.testnav.libs.dto.dolly.v1.FinnesDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String IDENT_NOT_FOUND = "Testperson med ident %s ble ikke funnet";

    private final IdentRepository identRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
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
    public void swapIdent(String oldIdent, String newIdent) {

        if (identRepository.findByIdent(newIdent).isPresent()) {
            identRepository.deleteTestidentByIdent(oldIdent);

        } else {
            identRepository.swapIdent(oldIdent, newIdent);
        }
    }

    public List<GruppeBestillingIdent> getBestillingerFromGruppe(Testgruppe gruppe) {

        return identRepository.getBestillingerFromGruppe(gruppe);
    }

    public List<GruppeBestillingIdent> getBestillingerFromIdent(String ident) {

        return identRepository.getBestillingerByIdent(ident);
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

        return identRepository.findAllByTestgruppeId(gruppeId, page)
                .flatMap(ident -> bestillingProgressRepository.findByIdent(ident.getIdent())
                        .collectList()
                        .map(bestillingProgress -> {
                            ident.setBestillingProgress(bestillingProgress);
                            return ident;
                        }))
                .collectList()
                .zipWith(identRepository.countByTestgruppeId(gruppeId))
                .map(tuple -> new PageImpl<>(tuple.getT1(), page, tuple.getT2()));
    }

    public Mono<Integer> getPaginertIdentIndex(String ident, Long gruppeId) {

        return identRepository.getPaginertTestidentIndex(ident, gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

    public Mono<Testident> getTestIdent(String ident) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format(IDENT_NOT_FOUND, ident))));
    }

//    public List<Testident> getTestidenterByGruppe(Long id) {
//
//        return identRepository.findByTestgruppe(id);
//    }

    public FinnesDTO exists(List<String> identer) {

        var finnes = FinnesDTO.builder()
                .iBruk(identRepository.findByIdentIn(identer).stream()
                        .map(Testident::getIdent)
                        .collect(Collectors.toMap(ident -> ident, ident -> true)))
                .build();

        identer.forEach(ident -> {
            if (!finnes.getIBruk().containsKey(ident)) {
                finnes.getIBruk().put(ident, false);
            }
        });

        return finnes;
    }
}
