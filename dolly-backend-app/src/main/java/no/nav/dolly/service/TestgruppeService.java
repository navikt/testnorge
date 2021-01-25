package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsLockTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TestgruppeService {

    private static final int PAGE_SIZE = 10;

    private final TestgruppeRepository testgruppeRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final BrukerService brukerService;
    private final IdentService identService;
    private final BestillingService bestillingService;
    private final PersonService personService;

    public Testgruppe opprettTestgruppe(RsOpprettEndreTestgruppe rsTestgruppe) {
        Bruker bruker = brukerService.fetchBruker(getUserId());

        return saveGruppeTilDB(Testgruppe.builder()
                .navn(rsTestgruppe.getNavn())
                .hensikt(rsTestgruppe.getHensikt())
                .datoEndret(LocalDate.now())
                .opprettetAv(bruker)
                .sistEndretAv(bruker)
                .build()
        );
    }

    public Testgruppe fetchTestgruppeById(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() ->
                new NotFoundException(format("Gruppe med id %s ble ikke funnet.", gruppeId)));
    }

    public Page<Testgruppe> getAllTestgrupper(Integer pageNo) {

        return testgruppeRepository.findAllByOrderByNavn(PageRequest.of(pageNo, PAGE_SIZE));
    }

    public List<Testgruppe> fetchGrupperByIdsIn(Collection<Long> grupperIDer) {
        List<Testgruppe> grupper = testgruppeRepository.findAllById(grupperIDer);
        if (!grupper.isEmpty()) {
            return grupper;
        }
        throw new NotFoundException("Finner ikke grupper basert på IDer : " + grupperIDer);
    }

    public List<Testgruppe> fetchTestgrupperByBrukerId(String brukerId) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        List<Bruker> eidAvBruker = brukerService.fetchEidAv(bruker);
        eidAvBruker.add(bruker);

        Set<Testgruppe> testgrupper = eidAvBruker.stream().map(Bruker::getTestgrupper).flatMap(Collection::stream).collect(Collectors.toSet());
        Set<Testgruppe> favoritter = eidAvBruker.stream().map(Bruker::getFavoritter).flatMap(Collection::stream).collect(Collectors.toSet());
        testgrupper.addAll(favoritter);

        return testgrupper.stream().collect(Collectors.toList());
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
                    .map(gruppe -> testgruppeRepository.save(gruppe))
                    .collect(Collectors.toList());
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    public void deleteGruppeById(Long gruppeId) {
        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);

        bestillingService.slettBestillingerByGruppeId(gruppeId);
        transaksjonMappingRepository.deleteAllByIdentIn(testgruppe.getTestidenter().stream()
                .map(Testident::getIdent)
                .collect(Collectors.toList()));
        identService.slettTestidenterByGruppeId(gruppeId);
        brukerService.sletteBrukerFavoritterByGroupId(gruppeId);
        testgruppeRepository.deleteTestgruppeById(gruppeId);
        personService.recyclePersoner(testgruppe.getTestidenter().stream().map(Testident::getIdent).collect(Collectors.toList()));
    }

    public Testgruppe oppdaterTestgruppe(Long gruppeId, RsOpprettEndreTestgruppe endreGruppe) {
        Testgruppe testgruppe = fetchTestgruppeById(gruppeId);

        testgruppe.setHensikt(endreGruppe.getHensikt());
        testgruppe.setNavn(endreGruppe.getNavn());
        testgruppe.setSistEndretAv(brukerService.fetchBruker(getUserId()));
        testgruppe.setDatoEndret(LocalDate.now());

        return saveGruppeTilDB(testgruppe);
    }

    public List<Testgruppe> getTestgruppeByBrukerId(String brukerId) {

        return isBlank(brukerId) ? testgruppeRepository.findAllByOrderByNavn() : fetchTestgrupperByBrukerId(brukerId);
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
}
