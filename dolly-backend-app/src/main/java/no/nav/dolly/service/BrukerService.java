package no.nav.dolly.service;

import static no.nav.dolly.util.CurrentNavIdentFetcher.getLoggedInNavIdent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;

@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;

    public Bruker fetchBruker(String navIdent) {
        Bruker bruker = brukerRepository.findBrukerByBrukerId(navIdent.toUpperCase());
        if (bruker == null) {
            throw new NotFoundException("Bruker ikke funnet");
        }
        return bruker;
    }

    public Bruker fetchOrCreateBruker(String navIdent) {
        try {
            return fetchBruker(navIdent);
        } catch (NotFoundException e) {
            return brukerRepository.save(Bruker.builder().brukerId(navIdent.toUpperCase()).build());
        }
    }

    public Bruker leggTilFavoritt(Long gruppeId) {
        Testgruppe grupper = fetchTestgruppe(gruppeId);

        Bruker bruker = fetchBruker(getLoggedInNavIdent());
        bruker.getFavoritter().addAll(new HashSet<>(Collections.singleton(grupper)));
        return brukerRepository.save(bruker);
    }

    public Bruker fjernFavoritt(Long gruppeIDer) {
        Testgruppe testgruppe = fetchTestgruppe(gruppeIDer);

        Bruker bruker = fetchBruker(getLoggedInNavIdent());
        bruker.getFavoritter().remove(testgruppe);
        testgruppe.getFavorisertAv().remove(bruker);

        saveGruppe(testgruppe);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {
        return brukerRepository.findAllByOrderByBrukerId();
    }

    public int sletteBrukerFavoritterByGroupId(Long groupId) {
        return brukerRepository.deleteBrukerFavoritterByGroupId(groupId);
    }

    public Bruker saveBrukerTilDB(Bruker b) {
        try {
            return brukerRepository.save(b);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Bruker DB constraint er brutt! Kan ikke lagre bruker. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }

    private Testgruppe fetchTestgruppe(Long gruppeId) {
        return testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
    }

    private Testgruppe saveGruppe(Testgruppe testgruppe) {
        try {
            return testgruppeRepository.save(testgruppe);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Testgruppe DB constraint er brutt! Kan ikke lagre testgruppe. Error: " + e.getMessage(), e);
        } catch (NonTransientDataAccessException e) {
            throw new DollyFunctionalException(e.getMessage(), e);
        }
    }
}
