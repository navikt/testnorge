package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.dto.DeleteZIdentResponse;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.projection.TestgruppeUtenIdenter;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsLockTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppePage;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestgruppeService {

    private final MapperFacade mapperFacade;
    private final TestgruppeRepository testgruppeRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BrukerService brukerService;
    private final IdentService identService;
    private final BestillingService bestillingService;
    private final PersonService personService;
    private final GetUserInfo getUserInfo;
    private final PdlDataConsumer pdlDataConsumer;

    public Testgruppe opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {
        Bruker bruker = brukerService.fetchBruker(getUserId(getUserInfo));

        return saveGruppeTilDB(Testgruppe.builder()
                .navn(rsTestgruppe.getNavn())
                .hensikt(rsTestgruppe.getHensikt())
                .datoEndret(LocalDate.now())
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .build()
        );
    }

    public RsTestgruppeMedBestillingId fetchPaginertTestgruppeById(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        TestgruppeUtenIdenter testgruppeUtenIdenter = testgruppeRepository.findByIdOrderById(gruppeId)
                .orElseThrow(() -> new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId)));

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
                .tags(testgruppeUtenIdenter.getTags())
                .testidenter(testidentPage.toList())
                .build();

        RsTestgruppeMedBestillingId rsTestgruppe = mapperFacade.map(testgruppe, RsTestgruppeMedBestillingId.class);
        rsTestgruppe.setAntallIdenter((int) testidentPage.getTotalElements());

        var bestillingerPage = bestillingService.getBestillingerFromGruppePaginert(testgruppe, 0, 1);
        rsTestgruppe.setAntallBestillinger(bestillingerPage.getTotalElements());
        return rsTestgruppe;
    }

    public Testgruppe fetchTestgruppeById(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() ->
                new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId)));
    }

    public Page<Testgruppe> getAllTestgrupper(Integer pageNo, Integer pageSize) {

        return testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
    }

    public List<Testgruppe> fetchGrupperByIdsIn(Collection<Long> grupperIDer) {
        List<Testgruppe> grupper = testgruppeRepository.findAllById(grupperIDer);
        if (!grupper.isEmpty()) {
            return grupper;
        }
        throw new NotFoundException("Finner ikke grupper basert på IDer : " + grupperIDer);
    }

    public Page<Testgruppe> fetchTestgrupperByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        List<Bruker> eidAvBruker = brukerService.fetchEidAv(bruker);
        eidAvBruker.add(bruker);

        return testgruppeRepository.findAllByOpprettetAvIn(eidAvBruker, PageRequest.of(pageNo, pageSize, Sort.by("id").descending()));
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

    public Long deleteGruppeById(Long gruppeId) {
        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);
        var testIdenter = mapperFacade.mapAsList(testgruppe.getTestidenter(), TestidentDTO.class);

        transaksjonMappingRepository.deleteByGruppeId(gruppeId);

        bestillingService.slettBestillingerByGruppeId(gruppeId);
        identService.slettTestidenterByGruppeId(gruppeId);

        personService.recyclePersoner(testIdenter);
        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
        testgruppeRepository.deleteTestgruppeById(gruppeId);

        return gruppeId;
    }

    public Testgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {
        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);

        testgruppe.setHensikt(endreGruppe.getHensikt());
        testgruppe.setNavn(endreGruppe.getNavn());
        testgruppe.setSistEndretAv(brukerService.fetchBruker(getUserId(getUserInfo)));
        testgruppe.setDatoEndret(LocalDate.now());

        return saveGruppeTilDB(testgruppe);
    }

    public RsTestgruppePage getTestgruppeByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        var bruker = isBlank(brukerId) ? null : brukerService.fetchBruker(brukerId);
        var paginertGruppe = isBlank(brukerId)
                ? testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize))
                : fetchTestgrupperByBrukerId(pageNo, pageSize, brukerId);

        return RsTestgruppePage.builder()
                .pageNo(paginertGruppe.getNumber())
                .antallPages(paginertGruppe.getTotalPages())
                .pageSize(paginertGruppe.getSize())
                .antallElementer(paginertGruppe.getTotalElements())
                .contents(mapperFacade.mapAsList(paginertGruppe.getContent(), RsTestgruppe.class))
                .favoritter(nonNull(bruker) ? mapperFacade.mapAsList(bruker.getFavoritter(), RsTestgruppe.class) : emptyList())
                .build();
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

    public void leggTilIdent(Long gruppeId, String ident, Testident.Master master) {

        var testgruppe = fetchTestgruppeById(gruppeId);
        identService.saveIdentTilGruppe(ident, testgruppe, master, null);
        pdlDataConsumer.putStandalone(ident, true)
                .subscribe(response -> log.info("Lagt til ident {} som standalone i PDL-forvalter", ident));
    }

    public List<DeleteZIdentResponse> sletteGrupperForIkkemigrerteNavIdenter(Set<String> brukere) {

        return brukere.stream()
                .map(bruker -> DeleteZIdentResponse.builder()
                        .bruker(bruker)
                        .grupper(
                                testgruppeRepository.getIkkemigrerteTestgrupperByNavId(bruker).stream()
                                        .map(testgruppe -> {
                                            var gruppe = DeleteZIdentResponse.Gruppe.builder()
                                                    .id(testgruppe.getId())
                                                    .identer(testgruppe.getTestidenter().stream()
                                                            .map(Testident::getIdent)
                                                            .toList())
                                                    .build();
                                            deleteGruppeById(testgruppe.getId());
                                            log.info("Slettet gruppe {} for bruker {}", testgruppe.getId(), bruker);
                                            return gruppe;
                                        })
                                        .toList())
                        .build())
                .toList();
    }
}
