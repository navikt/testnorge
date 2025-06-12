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
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppePage;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    private final GetAuthenticatedUserId getUserInfo;
    private final PdlDataConsumer pdlDataConsumer;
    private final BrukerServiceConsumer brukerServiceConsumer;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final BestillingRepository bestillingRepository;
    private final BrukerFavoritterRepository brukerFavoritterRepository;

    public Mono<Testgruppe> opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {

        return getAuthenticatedUserId.call()
                .map(brukerService::fetchBruker)
                .map(bruker -> saveGruppeTilDB(Testgruppe.builder()
                        .navn(rsTestgruppe.getNavn())
                        .hensikt(rsTestgruppe.getHensikt())
                        .datoEndret(LocalDate.now())
                        .opprettetAv(bruker)
                        .sistEndretAv(bruker)
                        .build()));
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
                        .map(testgruppeRepository::findAllByOpprettetAv_BrukerIdIn)
                        .map(page -> page.stream().anyMatch(gruppe -> gruppe.equals(gruppeId))))
                .switchIfEmpty(Mono.just(true));
    }

//    public Testgruppe fetchTestgruppeById(Long gruppeId) {
//        return testgruppeRepository.findById(gruppeId).orElseThrow(() ->
//                new NotFoundException(GRUPPE_MELDING.formatted(gruppeId)));
//    }

    public Optional<Testgruppe> fetchTestgruppeById(long gruppeId) {

        return testgruppeRepository.findByIdOrderById(gruppeId)
                .map(gruppe -> {
                    gruppe.setBestillinger(bestillingRepository.getBestillingByGruppe_Id(gruppeId));
//                    gruppe.setFavorisertAv(brukerFavoritterRepository.getAllByGruppeId(gruppeId));
                    return gruppe;
                });


    }

    public Mono<Page<Testgruppe>> getAllTestgrupper(Integer pageNo, Integer pageSize) {

        var bruker = brukerService.fetchOrCreateBruker();
//        if (bruker.getBrukertype() == BANKID) {
//            return brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
//                    .map(TilgangDTO::getBrukere)
//                    .map(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
//                            PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
//        } else {
//            return Mono.just(testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
//        }
        return Mono.empty(); // TBD
    }

    public Page<Testgruppe> fetchTestgrupperByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        var bruker = brukerService.fetchBruker(brukerId);

        return testgruppeRepository.findAllByOpprettetAv(bruker, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
    }

    public Testgruppe saveGruppeTilDB(Testgruppe testgruppe) {
        try {
            return testgruppeRepository.save(testgruppe);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    public List<Testgruppe> saveGrupper(Collection<Testgruppe> testgrupper) {
        try {
            return testgrupper.stream()
                    .map(testgruppeRepository::save)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    @Transactional
    public Long deleteGruppeById(Long gruppeId) {
//        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);
//        var testIdenter = mapperFacade.mapAsList(testgruppe.getTestidenter(), TestidentDTO.class);
//
//        transaksjonMappingRepository.deleteByGruppeId(gruppeId);
//
//        bestillingService.slettBestillingerByGruppeId(gruppeId);
//        identService.slettTestidenterByGruppeId(gruppeId);
//
//        personService.recyclePersoner(testIdenter);
//        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
//        testgruppeRepository.deleteAllById(gruppeId);
//
//        return gruppeId;
        return null; //TBD
    }

    public Testgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {
//        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);
//
//        testgruppe.setHensikt(endreGruppe.getHensikt());
//        testgruppe.setNavn(endreGruppe.getNavn());
//        testgruppe.setSistEndretAv(brukerService.fetchBruker(getUserId(getUserInfo)));
//        testgruppe.setDatoEndret(LocalDate.now());
//
//        return saveGruppeTilDB(testgruppe);
        return null; //TBD
    }

    public RsTestgruppePage getTestgruppeByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        var bruker = brukerService.fetchOrCreateBruker(brukerId);

        Page<Testgruppe> paginertGruppe;

//        if (isBlank(brukerId) && bruker.getBrukertype() == BANKID) {
//            paginertGruppe = brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
//                    .map(TilgangDTO::getBrukere)
//                    .map(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
//                            PageRequest.of(pageNo, pageSize, Sort.by("id").descending())))
//                    .block();
//
//        } else if (isBlank(brukerId)) {
//            paginertGruppe = testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize));
//
//        } else {
//            paginertGruppe = fetchTestgrupperByBrukerId(pageNo, pageSize, brukerId);
//        }
//
//        return RsTestgruppePage.builder()
//                .pageNo(paginertGruppe.getNumber())
//                .antallPages(paginertGruppe.getTotalPages())
//                .pageSize(paginertGruppe.getSize())
//                .antallElementer(paginertGruppe.getTotalElements())
//                .contents(mapperFacade.mapAsList(paginertGruppe.getContent(), RsTestgruppe.class))
//                .favoritter(mapperFacade.mapAsList(bruker.getFavoritter(), RsTestgruppe.class))
//                .build();
        return null; // TBD
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

//        var testgruppe = fetchTestgruppeById(gruppeId);
//        identService.saveIdentTilGruppe(ident, testgruppe, master, null);
//        return pdlDataConsumer.putStandalone(ident, true);
        return Mono.empty();
    }

    public Page<Testident> getIdenter(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

                        return identService.getTestidenterFromGruppePaginert(gruppeId, pageNo, pageSize, sortColumn, sortRetning);

    }
}
