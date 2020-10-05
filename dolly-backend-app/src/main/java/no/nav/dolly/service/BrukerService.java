package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singleton;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;

    public Bruker fetchBruker(String brukerId) {
        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseGet(() -> brukerRepository.findBrukerByNavIdent(brukerId.toUpperCase())
                        .orElseThrow(() -> new NotFoundException("Bruker ikke funnet")));
    }

    public Bruker fetchOrCreateBruker(String brukerId) {
        try {
            return fetchBruker(brukerId);
        } catch (NotFoundException e) {
            return brukerRepository.save(getAuthUser());
        }
    }

    public Bruker leggTilFavoritt(Long gruppeId) {
        Testgruppe grupper = fetchTestgruppe(gruppeId);

        Bruker bruker = fetchOrCreateBruker(getUserId());
        bruker.getFavoritter().addAll(singleton(grupper));
        return brukerRepository.save(bruker);
    }

    public Bruker fjernFavoritt(Long gruppeIDer) {
        Testgruppe testgruppe = fetchTestgruppe(gruppeIDer);

        Bruker bruker = fetchOrCreateBruker(getUserId());
        bruker.getFavoritter().remove(testgruppe);
        testgruppe.getFavorisertAv().remove(bruker);

        saveGruppe(testgruppe);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {
        return brukerRepository.findAllByOrderById();
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

    public int migrerBruker(Collection<String> navIdenter, String brukerId) {
        Optional<Bruker> bruker = brukerRepository.findBrukerByBrukerId(brukerId);
        if (bruker.isPresent() && isTrue(bruker.get().getMigrert())) {
            throw new DollyFunctionalException(format("Bruker %s er allerede migrert", bruker.get().getBrukernavn()));
        }
        brukerRepository.saveBrukerIdMigrert(brukerId);
        return brukerRepository.saveNavIdentToBruker(navIdenter, brukerId);
    }

    public List<Bruker> fetchEidAv(Long id) {
        return brukerRepository.fetchEidAv(id);
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
