package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.TilgangDTO;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.BANKID;
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
    private final BrukerServiceConsumer brukerServiceConsumer;

    public Testgruppe opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {
        var bruker = brukerService.fetchBrukerOrTeamBruker(getUserId(getUserInfo));

        return saveGruppeTilDB(Testgruppe.builder()
                .navn(rsTestgruppe.getNavn())
                .hensikt(rsTestgruppe.getHensikt())
                .datoEndret(LocalDate.now())
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .build()
        );
    }

    @Transactional(readOnly = true)
    public RsTestgruppeMedBestillingId fetchPaginertTestgruppeById(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {

        sjekkTilgang(gruppeId);

        var testgruppeUtenIdenter = testgruppeRepository.findByIdOrderById(gruppeId)
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

        var rsTestgruppe = mapperFacade.map(testgruppe, RsTestgruppeMedBestillingId.class);
        rsTestgruppe.setAntallIdenter((int) testidentPage.getTotalElements());

        var bestillingerPage = bestillingService.getBestillingerFromGruppeIdPaginert(testgruppe.getId(), 0, 1);
        rsTestgruppe.setAntallBestillinger(bestillingerPage.getTotalElements());
        return rsTestgruppe;
    }

    public Testgruppe fetchTestgruppeById(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() ->
                new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId)));
    }

    public Mono<Page<Testgruppe>> getAllTestgrupper(Integer pageNo, Integer pageSize) {

        var bruker = brukerService.fetchOrCreateBruker();
        if (bruker.getBrukertype() == BANKID) {
            return brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                    .map(TilgangDTO::getBrukere)
                    .map(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
                            PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
        } else {
            return Mono.just(testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize, Sort.by("id").descending())));
        }
    }

    public Page<Testgruppe> fetchTestgrupperByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        var bruker = brukerService.fetchBrukerWithoutTeam(brukerId);

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
        var testgruppe = fetchTestgruppeById(gruppeId);
        var testIdenter = mapperFacade.mapAsList(testgruppe.getTestidenter(), TestidentDTO.class);

        transaksjonMappingRepository.deleteByGruppeId(gruppeId);

        bestillingService.slettBestillingerByGruppeId(gruppeId);
        identService.slettTestidenterByGruppeId(gruppeId);

        personService.recyclePersoner(testIdenter);
        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
        testgruppeRepository.deleteAllById(gruppeId);

        return gruppeId;
    }

    public Testgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {
        var testgruppe = fetchTestgruppeById(gruppeId);

        testgruppe.setHensikt(endreGruppe.getHensikt());
        testgruppe.setNavn(endreGruppe.getNavn());
        testgruppe.setSistEndretAv(brukerService.fetchBrukerOrTeamBruker(getUserId(getUserInfo)));
        testgruppe.setDatoEndret(LocalDate.now());

        return saveGruppeTilDB(testgruppe);
    }

    public RsTestgruppePage getTestgruppeByBrukerId(Integer pageNo, Integer pageSize, String brukerId) {

        var bruker = brukerService.fetchBrukerWithoutTeam(brukerId);

        Page<Testgruppe> paginertGruppe;

        if (isBlank(brukerId) && bruker.getBrukertype() == BANKID) {
            paginertGruppe = brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                    .map(TilgangDTO::getBrukere)
                    .map(brukere -> testgruppeRepository.findAllByOpprettetAv_BrukerIdIn(brukere,
                            PageRequest.of(pageNo, pageSize, Sort.by("id").descending())))
                    .block();

        } else if (isBlank(brukerId)) {
            paginertGruppe = testgruppeRepository.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize));

        } else {
            paginertGruppe = fetchTestgrupperByBrukerId(pageNo, pageSize, brukerId);
        }

        return RsTestgruppePage.builder()
                .pageNo(paginertGruppe.getNumber())
                .antallPages(paginertGruppe.getTotalPages())
                .pageSize(paginertGruppe.getSize())
                .antallElementer(paginertGruppe.getTotalElements())
                .contents(mapperFacade.mapAsList(paginertGruppe.getContent(), RsTestgruppe.class))
                .favoritter(mapperFacade.mapAsList(bruker.getFavoritter(), RsTestgruppe.class))
                .build();
    }

    public Testgruppe oppdaterTestgruppeMedLaas(Long gruppeId, RsLockTestgruppe lockTestgruppe) {

        var testgruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke testgruppe med id = " + gruppeId));
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

    private void sjekkTilgang(Long gruppeId) {

        var bruker = brukerService.fetchOrCreateBruker();
        if (bruker.getBrukertype() == BANKID) {
            brukerServiceConsumer.getKollegaerIOrganisasjon(bruker.getBrukerId())
                    .map(TilgangDTO::getBrukere)
                    .map(testgruppeRepository::findAllByOpprettetAv_BrukerIdIn)
                    .filter(page -> page.stream().anyMatch(gruppe -> gruppe.equals(gruppeId)))
                    .switchIfEmpty(Mono.error(new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId))))
                    .block();
        }
    }
}
