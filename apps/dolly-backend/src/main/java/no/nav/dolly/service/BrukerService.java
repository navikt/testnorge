package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getAuthUser;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Service
@RequiredArgsConstructor
public class BrukerService {

    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;
    private final GetUserInfo getUserInfo;

    public Bruker fetchBruker(String brukerId) {

        return brukerRepository.findBrukerByBrukerId(brukerId)
                .orElseGet(() -> brukerRepository.findBrukerByNavIdent(brukerId.toUpperCase())
                        .orElseThrow(() -> new NotFoundException("Bruker ikke funnet")));
    }

    public Bruker fetchOrCreateBruker(String brukerId) {

        try {
            Bruker bruker = fetchBruker(brukerId);
            List<Bruker> brukere = brukerRepository.fetchEidAv(bruker);
            bruker.getFavoritter().addAll(brukere.stream().map(Bruker::getFavoritter)
                    .flatMap(Collection::stream).collect(Collectors.toSet()));

            oppdaterBrukernavn(bruker);

            return bruker;

        } catch (NotFoundException e) {
            return brukerRepository.save(getAuthUser(getUserInfo));
        }
    }

    @Transactional
    public Bruker leggTilFavoritt(Long gruppeId) {

        Bruker bruker = fetchBruker(getUserId(getUserInfo));
        List<Bruker> brukere = brukerRepository.fetchEidAv(bruker);
        brukere.add(bruker);
        if (brukere.stream().map(Bruker::getFavoritter)
                .flatMap(Collection::stream)
                .noneMatch(testgruppe -> testgruppe.getId().equals(gruppeId))) {

            Testgruppe gruppe = fetchTestgruppe(gruppeId);
            gruppe.getFavorisertAv().add(bruker);
            bruker.getFavoritter().add(gruppe);
        }

        return brukerRepository.save(bruker);
    }

    public Bruker fjernFavoritt(Long gruppeId) {

        Bruker bruker = fetchBruker(getUserId(getUserInfo));
        Testgruppe gruppe = fetchTestgruppe(gruppeId);

        List<Bruker> brukere = brukerRepository.fetchEidAv(bruker);
        brukere.stream()
                .filter(bruker1 -> gruppe.getFavorisertAv().stream().anyMatch(bruker2 -> bruker2.equals(bruker1)))
                .forEach(bruker1 -> {
                    gruppe.getFavorisertAv().remove(bruker1);
                    bruker1.getFavoritter().remove(gruppe);
                    brukerRepository.save(bruker1);
                });

        bruker.getFavoritter().remove(gruppe);
        saveGruppe(gruppe);
        gruppe.getFavorisertAv().remove(bruker);
        return brukerRepository.save(bruker);
    }

    public List<Bruker> fetchBrukere() {

        List<Bruker> brukere = brukerRepository.findAllByOrderById();
        Map<Long, Bruker> brukereMap = brukere.stream().collect(Collectors.toMap(Bruker::getId, bruker -> bruker));
        brukereMap.values().stream()
                .filter(bruker -> nonNull(bruker.getEidAv()))
                .forEach(bruker -> brukereMap.get(bruker.getEidAv().getId()).getFavoritter().addAll(bruker.getFavoritter()));
        return brukereMap.values().stream().filter(bruker -> isNull(bruker.getEidAv())).collect(Collectors.toList());
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

        fetchOrCreateBruker(brukerId);
        brukerRepository.saveBrukerIdMigrert(brukerId);
        return brukerRepository.saveNavIdentToBruker(navIdenter, brukerId);
    }

    public int fjernMigreringAvBruker(String brukerId) {

        Bruker bruker = fetchOrCreateBruker(brukerId);
        if (isFalse(bruker.getMigrert())) {
            throw new DollyFunctionalException(format("Bruker %s er ikke migrert enda", bruker.getBrukernavn()));
        }
        brukerRepository.deleteBrukerIdMigrert(brukerId);
        return brukerRepository.deleteNavIdentToBruker(bruker);
    }

    public List<Bruker> fetchEidAv(Bruker bruker) {
        return brukerRepository.fetchEidAv(bruker);
    }

    private void oppdaterBrukernavn(Bruker bruker) {

        String hentetBrukernavn = getAuthUser(getUserInfo).getBrukernavn();
        if (!bruker.getBrukernavn().equals(hentetBrukernavn)) {
            bruker.setBrukernavn(hentetBrukernavn);
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
