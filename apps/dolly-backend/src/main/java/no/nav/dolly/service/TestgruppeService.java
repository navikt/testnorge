package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Bruker.Brukertype;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsLockTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppePage;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestgruppeService {

    private static final String GRUPPE_MELDING = "Gruppe med id %s ble ikke funnet.";

    private final MapperFacade mapperFacade;
    private final TestgruppeRepository testgruppeRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BrukerService brukerService;
    private final IdentService identService;
    private final BestillingService bestillingService;
    private final PersonService personService;
    private final PdlDataConsumer pdlDataConsumer;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final BestillingRepository bestillingRepository;
    private final IdentRepository identRepository;

    public Mono<Testgruppe> opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {

        return getAuthenticatedUserId.call()
                .flatMap(brukerService::fetchBruker)
                .map(bruker -> Testgruppe.builder()
                        .navn(rsTestgruppe.getNavn())
                        .hensikt(rsTestgruppe.getHensikt())
                        .datoEndret(LocalDate.now())
                        .opprettetAvId(bruker.getId())
                        .opprettetAv(bruker)
                        .sistEndretAvId(bruker.getId())
                        .sistEndretAv(bruker)
                        .build())
                .flatMap(testgruppeRepository::save);
    }

    @Transactional(readOnly = true)
    public Mono<RsTestgruppeMedBestillingId> fetchPaginertTestgruppeById(Long gruppeId, Integer pageNo, Integer pageSize,
                                                                         String sortColumn, String sortRetning) {
        return harTilgang(gruppeId)
                .flatMap(tilgang -> {
                    if (isTrue(tilgang)) {
                        return brukerService.fetchOrCreateBruker()
                                .flatMap(bruker -> this.fetchTestgruppeById(gruppeId)
                                        .flatMap(testgruppe -> Mono.zip(
                                                Mono.just(testgruppe),
                                                Mono.just(bruker),
                                                identRepository.countByGruppeId(testgruppe.getId()),
                                                bestillingRepository.countByGruppeId(testgruppe.getId()),
                                                identRepository.countByGruppeIdAndIBruk(testgruppe.getId(), true),
                                                identService.getTestidenterFromGruppePaginert(gruppeId, pageNo,
                                                        pageSize, sortColumn, sortRetning)))
                                        .flatMap(tuple -> {
                                            var context = MappingContextUtils.getMappingContext();
                                            context.setProperty("bruker", tuple.getT2());
                                            context.setProperty("antallIdenter", tuple.getT3());
                                            context.setProperty("antallBestillinger", tuple.getT4());
                                            context.setProperty("antallIBruk", tuple.getT5());
                                            return Mono.just(mapperFacade.map(tuple.getT1(), RsTestgruppe.class, context))
                                                    .zipWith(Mono.just(tuple.getT6()));
                                        })
                                        .map(tuple -> {
                                            var context = MappingContextUtils.getMappingContext();
                                            context.setProperty("identer", tuple.getT2());
                                            return mapperFacade.map(tuple.getT1(), RsTestgruppeMedBestillingId.class, context);
                                        }));
                    } else {
                        return Mono.error(new NotFoundException(GRUPPE_MELDING.formatted(gruppeId)));
                    }
                });
    }

    private Mono<Boolean> harTilgang(Long gruppeId) {

        return brukerService.fetchOrCreateBruker()
                .filter(bruker -> Brukertype.BANKID == bruker.getBrukertype())
                .flatMap(bruker -> brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                        .map(TilgangDTO::getBrukere)
                        .flatMap(brukere -> testgruppeRepository.findAllIdsByOpprettetAv_BrukerIdIn(brukere)
                                .collectList())
                        .map(grupper -> grupper.stream().anyMatch(gruppe -> gruppe.equals(gruppeId))))
                .switchIfEmpty(Mono.just(true));
    }

    public Mono<Testgruppe> fetchTestgruppeById(Long gruppeId) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(GRUPPE_MELDING.formatted(gruppeId))));
    }

    public Mono<RsTestgruppePage> getAllTestgrupper(Integer pageNo, Integer pageSize) {

        return brukerService.fetchOrCreateBruker()
                .flatMap(bruker ->
                        (bruker.getBrukertype() == Brukertype.BANKID
                                ? brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .flatMap(brukere -> testgruppeRepository.findByOpprettetAv_BrukerIdIn(brukere,
                                                PageRequest.of(pageNo, pageSize, Sort.by("id").descending()))
                                        .collectList()
                                        .zipWith(testgruppeRepository.countByOpprettetAv_BrukerIdIn(brukere)))
                                :
                                testgruppeRepository.findByOrderByIdDesc(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()))
                                        .collectList()
                                        .zipWith(testgruppeRepository.countBy()))
                                .flatMap(tuple ->
                                        getRsTestgruppePage(pageNo, pageSize, bruker, tuple.getT1(), tuple.getT2())));
    }

    public Flux<Testgruppe> fetchTestgrupperByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        return brukerService.fetchBruker(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke bruker med id = " + brukerId)))
                .flatMapMany(bruker -> testgruppeRepository.findByOpprettetAvId(bruker.getId(),
                        PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
    }

    public Flux<Testgruppe> saveGrupper(Collection<Testgruppe> testgrupper) {

        return Flux.fromIterable(testgrupper)
                .flatMap(testgruppeRepository::save);
    }

    @Transactional
    public Mono<Void> deleteGruppeById(Long gruppeId) {

        return fetchTestgruppeById(gruppeId)
                .then(transaksjonMappingRepository.deleteByGruppeId(gruppeId))
                .then(bestillingService.slettBestillingerByGruppeId(gruppeId))
                .then(identRepository.findByGruppeId(gruppeId, Pageable.unpaged())
                        .collectList()
                        .map(testidenter -> mapperFacade.mapAsList(testidenter, TestidentDTO.class))
                        .flatMap(personService::recyclePersoner))
                .then(identRepository.deleteByGruppeId(gruppeId))
                .then(brukerService.sletteBrukerFavoritterByGroupId(gruppeId))
                .then(testgruppeRepository.deleteById(gruppeId));
    }

    public Mono<Testgruppe> oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {

        return fetchTestgruppeById(gruppeId)
                .zipWith(getAuthenticatedUserId.call()
                        .flatMap(brukerService::fetchBruker))
                .map(tuple -> {
                    tuple.getT1().setHensikt(endreGruppe.getHensikt());
                    tuple.getT1().setNavn(endreGruppe.getNavn());
                    tuple.getT1().setSistEndretAvId(tuple.getT2().getId());
                    tuple.getT1().setSistEndretAv(tuple.getT2());
                    tuple.getT1().setDatoEndret(LocalDate.now());
                    return tuple.getT1();
                })
                .flatMap(testgruppeRepository::save);
    }

    public Mono<RsTestgruppePage> getTestgruppeByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        return brukerService.fetchOrCreateBruker(brukerId)
                .flatMap(bruker ->
                        (bruker.getBrukertype() == Brukertype.BANKID
                                ?
                                brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                        .map(TilgangDTO::getBrukere)
                                        .flatMap(brukere -> testgruppeRepository.findByOpprettetAv_BrukerIdIn(brukere,
                                                        PageRequest.of(pageNo, pageSize, Sort.by("id").descending()))
                                                .collectList()
                                                .zipWith(testgruppeRepository.countByOpprettetAv_BrukerIdIn(brukere)))
                                :
                                testgruppeRepository.findByOpprettetAvId(bruker.getId(), PageRequest.of(pageNo, pageSize))
                                        .collectList()
                                        .zipWith(testgruppeRepository.countByOpprettetAvId(bruker.getId())))
                                .flatMap(tuple ->
                                        getRsTestgruppePage(pageNo, pageSize, bruker, tuple.getT1(), tuple.getT2())));
    }

    private Mono<RsTestgruppePage> getRsTestgruppePage(Integer pageNo, Integer pageSize, Bruker bruker, List<Testgruppe> testgrupper, Long antall) {

        return Flux.fromIterable(testgrupper)
                .flatMap(testgruppe -> Mono.zip(
                        Mono.just(testgruppe),
                        identRepository.countByGruppeId(testgruppe.getId()),
                        bestillingRepository.countByGruppeId(testgruppe.getId()),
                        identRepository.countByGruppeIdAndIBruk(testgruppe.getId(), true)))
                .map(tuple2 -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("bruker", bruker);
                    context.setProperty("antallIdenter", tuple2.getT2());
                    context.setProperty("antallBestillinger", tuple2.getT3());
                    context.setProperty("antallIBruk", tuple2.getT4());
                    return mapperFacade.map(tuple2.getT1(), RsTestgruppe.class, context);
                })
                .collectList()
                .map(contents -> RsTestgruppePage.builder()
                        .pageNo(pageNo)
                        .antallPages(antall.intValue() / pageSize + 1)
                        .pageSize(pageSize)
                        .antallElementer(antall)
                        .contents(contents)
                        .build());
    }

    public Mono<Testgruppe> oppdaterTestgruppeMedLaas(Long gruppeId, RsLockTestgruppe lockTestgruppe) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke testgruppe med id = " + gruppeId)))
                .zipWith(getAuthenticatedUserId.call()
                        .flatMap(brukerService::fetchBruker))
                .map(tuple -> {
                    tuple.getT1().setSistEndretAvId(tuple.getT2().getId());
                    tuple.getT1().setSistEndretAv(tuple.getT2());
                    tuple.getT1().setDatoEndret(LocalDate.now());
                    tuple.getT1().setErLaast(isTrue(lockTestgruppe.getErLaast()));
                    tuple.getT1().setLaastBeskrivelse(isTrue(lockTestgruppe.getErLaast()) ?
                            lockTestgruppe.getLaastBeskrivelse() : null);
                    return tuple.getT1();
                })
                .flatMap(testgruppeRepository::save);
    }

    public Mono<String> leggTilIdent(Long gruppeId, String ident, Testident.Master master) {

        return fetchTestgruppeById(gruppeId)
                .flatMap(testgruppe -> identService.saveIdentTilGruppe(ident, testgruppe.getId(), master, null))
                .flatMap(testIdent -> pdlDataConsumer.putStandalone(ident, true));
    }

    public Mono<Page<Testident>> getIdenter(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        return identService.getTestidenterFromGruppePaginert(gruppeId, pageNo, pageSize, sortColumn, sortRetning);
    }
}
