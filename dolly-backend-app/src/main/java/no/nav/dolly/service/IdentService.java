package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.entity.testident.RsTestident;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class IdentService {

    private final IdentRepository identRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final MapperFacade mapperFacade;

    @Transactional
    public void persisterTestidenter(List<RsTestident> personIdentListe) {

        List<Testident> testidentList = mapperFacade.mapAsList(personIdentListe, Testident.class);
        try {
            identRepository.saveAll(testidentList);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("En Ident DB constraint er brutt! Kan ikke lagre Identer. Error: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Testident saveIdentTilGruppe(String ident, Testgruppe testgruppe, Master master) {

        Testident testident = identRepository.findByIdent(ident);
        if (isNull(testident)) {
            testident = new Testident();
        }
        testident.setIdent(ident);
        testident.setTestgruppe(testgruppe);
        testident.setMaster(master);
        return identRepository.save(testident);
    }

    public int slettTestident(String ident) {

        transaksjonMappingRepository.deleteAllByIdent(ident);
        return identRepository.deleteTestidentByIdent(ident);
    }

    public int slettTestidenterByGruppeId(Long gruppeId) {

        return identRepository.deleteTestidentByTestgruppeId(gruppeId);
    }

    @Transactional
    public Testident saveIdentIBruk(String ident, boolean iBruk) {

        Testident testident = identRepository.findByIdent(ident);
        if (nonNull(testident)) {
            testident.setIBruk(iBruk);
            return identRepository.save(testident);
        } else {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }

    @Transactional
    public Testident saveIdentBeskrivelse(String ident, String beskrivelse) {

        Testident testident = identRepository.findByIdent(ident);
        if (nonNull(testident)) {
            testident.setBeskrivelse(beskrivelse);
            return identRepository.save(testident);
        } else {
            throw new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident));
        }
    }

    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {
        identRepository.swapIdent(oldIdent, newIdent);
    }

    public List<GruppeBestillingIdent> getBestillingerFromGruppe(Testgruppe gruppe) {

        return identRepository.getBestillingerFromGruppe(gruppe);
    }

    public Page<Testident> getBestillingerFromGruppePaginert(Long gruppeId, Integer pageNo, Integer pageSize) {

        return identRepository.getTestidentByTestgruppeIdOrderByBestillingProgressIdDesc(gruppeId, PageRequest.of(pageNo, pageSize));
    }

    public Optional<Integer> getPaginertIdentIndex(String ident, Long gruppeId) {

        return identRepository.getPaginertTestidentIndex(ident, gruppeId);
    }

    public Testident getTestIdent(String ident) {

        return identRepository.findByIdent(ident);
    }
}