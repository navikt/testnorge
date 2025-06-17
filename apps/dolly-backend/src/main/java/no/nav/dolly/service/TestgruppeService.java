package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.dto.TestidentDTO;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;

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
    private final BrukerFavoritterRepository brukerFavoritterRepository;
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
    public Mono<RsTestgruppeMedBestillingId> fetchPaginertTestgruppeById(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        return harTilgang(gruppeId)
                .map(tilgang -> {
                    if (isTrue(tilgang)) {
                        return this.fetchTestgruppeById(gruppeId)
                                .map(testgruppeUtenIdenter -> {
                                    var testidentPage = identService.getTestidenterFromGruppePaginert(gruppeId, pageNo, pageSize, sortColumn, sortRetning);
                                    var testgruppe = Testgruppe.builder()
                                            .id(testgruppeUtenIdenter.getId())
                                            .navn(testgruppeUtenIdenter.getNavn())
                                            .hensikt(testgruppeUtenIdenter.getHensikt())
                                            .favorisertAv(testgruppeUtenIdenter.getFavorisertAv())
                                            .bestillinger(testgruppeUtenIdenter.getBestillinger())
                                            .opprettetAv(testgruppeUtenIdenter.getOpprettetAv())
                                            .datoEndret(testgruppeUtenIdenter.getDatoEndret())
                                            .sistEndretAv(testgruppeUtenIdenter.getSistEndretAv())
                                            .erLaast(testgruppeUtenIdenter.getErLaast())
                                            .laastBeskrivelse(testgruppeUtenIdenter.getLaastBeskrivelse())
//                                            .tags(testgruppeUtenIdenter.getTags())
                                            .testidenter(testidentPage.toList())
                                            .build();
                                    var rsTestgruppe = mapperFacade.map(testgruppe, RsTestgruppeMedBestillingId.class);
                                    rsTestgruppe.setAntallIdenter((int) testidentPage.getTotalElements());

                                    var bestillingerPage = bestillingService.getBestillingerFromGruppeIdPaginert(testgruppe.getId(), 0, 1);
                                    rsTestgruppe.setAntallBestillinger(bestillingerPage.getTotalElements());
                                    return rsTestgruppe;
                                })
                                .orElseThrow(() -> new NotFoundException(GRUPPE_MELDING.formatted(gruppeId)));
                    } else {
                        throw new NotFoundException(GRUPPE_MELDING.formatted(gruppeId));
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

    public Flux<Testgruppe> getAllTestgrupper(Integer pageNo, Integer pageSize) {

        return brukerService.fetchOrCreateBruker()
                .flatMapMany(bruker -> {
                    if (bruker.getBrukertype() == Brukertype.BANKID) {
                        return brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                .map(TilgangDTO::getBrukere)
                                .flatMapMany(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
                                        PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
                    } else {
                        return testgruppeRepository.findAllOrderByIdDesc(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()))))
                        ;
                    }
                });
    }

    public Flux<Testgruppe> fetchTestgrupperByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        return brukerService.fetchBruker(brukerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Finner ikke bruker med id = " + brukerId)))
                .flatMapMany(bruker -> testgruppeRepository.findAllByOpprettetAvId(bruker.getId(),
                        PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
    }

    public Flux<Testgruppe> saveGrupper(Collection<Testgruppe> testgrupper) {

        return Flux.fromIterable(testgrupper)
                .flatMap(testgruppeRepository::save);
    }

    @Transactional
    public Long deleteGruppeById(Long gruppeId) {

        fetchTestgruppeById(gruppeId)
        var testIdenter = mapperFacade.mapAsList(testgruppe.getTestidenter(), TestidentDTO.class);

        transaksjonMappingRepository.deleteByGruppeId(gruppeId);

        bestillingService.slettBestillingerByGruppeId(gruppeId);
        identService.slettTestidenterByGruppeId(gruppeId);

        personService.recyclePersoner(testIdenter);
        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
        testgruppeRepository.deleteAllById(gruppeId);

        return gruppeId;
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

        var test = brukerService.fetchOrCreateBruker(brukerId)
                .flatMap(bruker ->
                        (bruker.getBrukertype() == Brukertype.BANKID
                                ? brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                                        .map(TilgangDTO::getBrukere)
                                        .flatMapMany(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
                                                PageRequest.of(pageNo, pageSize, Sort.by("id").descending())))
                                : testgruppeRepository.findAllOrderByIdDesc(PageRequest.of(pageNo, pageSize))
                                .flatMap(testgruppe -> Mono.zip(Mono.just(testgruppe),
                                        identRepository.countByGruppeId(testgruppe.getId()),
                                        bestillingRepository.countByGruppeId(testgruppe.getId()),
                                        identRepository.countByGruppeIdAndIBruk(testgruppe.getId(), true)))
                                .map(tuple -> {
                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("bruker", bruker);
                                    context.setProperty("antallIdenter", tuple.getT2());
                                    context.setProperty("antallBestillinger", tuple.getT3());
                                    context.setProperty("antallIBruk", tuple.getT4());
                                    return mapperFacade.map(tuple.getT1(), RsTestgruppe.class, context);
                                })
                                .map(tuple -> {
                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("bruker", bruker);
                                    return mapperFacade.map(tuple.getT1(), RsTestgruppe.class, context)
                                            .withAntallIdenter(tuple.getT2())
                                            .withAntallBestillinger(tuple.getT3())
                                            .withAntallIBruk(tuple.getT4());
                                })));



                                .flatMap(tuple -> Mono.zip(Mono.just(tuple),
                        testgruppeRepository.countAll(),
                        brukerFavoritterRepository.getAllByBrukerId(tuple.getT2().getId())
                                .collectList()),
                bestillingRepository.countByGruppeId())
                .map(tuple -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("bruker", tuple.getT1().getT2());
                    return RsTestgruppePage.builder()
                            .pageNo(pageNo)
                            .antallPages(tuple.getT2().intValue() / pageSize + 1)
                            .pageSize(pageSize)
                            .antallElementer((long) tuple.getT1().getT1().size())
                            .contents(mapperFacade.mapAsList(tuple.getT1().getT1(), RsTestgruppe.class, context))
                            .favoritter(mapperFacade.mapAsList(tuple.getT3(), RsTestgruppe.class, context))
                            .build();
                });
    }

    public Testgruppe oppdaterTestgruppeMedLaas(Long gruppeId, RsLockTestgruppe lockTestgruppe) {

        Testgruppe testgruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke testgruppe med id = " + gruppeId));
        if (isTrue(lockTestgruppe.getErLaast())) {
            testgruppe.setErLaast(true);
            testgruppe.setLaastBeskrivelse(lockTestgruppe.getLaastBeskrivelse());

        } else {
            testgruppe.setErLaast(false);
            testgruppe.setLaastBeskrivelse(null);
        }

        return testgruppe;
    }

    public Mono<String> leggTilIdent(Long gruppeId, String ident, Testident.Master master) {

        var testgruppe = fetchTestgruppeById(gruppeId);
        identService.saveIdentTilGruppe(ident, testgruppe, master, null);
        return pdlDataConsumer.putStandalone(ident, true);
    }

    public Page<Testident> getIdenter(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        return identService.getTestidenterFromGruppePaginert(gruppeId, pageNo, pageSize, sortColumn, sortRetning);

    }
}
