package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class IdentService {

    private static final String IDENT_NOT_FOUND = "Testperson med ident %s ble ikke funnet";

    private final IdentRepository identRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final MapperFacade mapperFacade;

    @Transactional(readOnly = true)
    public boolean exists(String ident) {
        return identRepository.existsByIdent(ident);
    }

    @Transactional
    public Testident saveIdentTilGruppe(String ident, Testgruppe testgruppe, Master master, String beskrivelse) {

        Testident testident = identRepository.findByIdent(ident)
                .orElse(new Testident());

        testident.setIdent(ident);
        testident.setTestgruppe(testgruppe);
        testident.setMaster(master);
        testident.setBeskrivelse(beskrivelse);

        return identRepository.save(testident);
    }

    @Transactional
    public int slettTestident(String ident) {

        return identRepository.deleteTestidentByIdent(ident);
    }

    public int slettTestidenterByGruppeId(Long gruppeId) {

        return identRepository.deleteTestidentByTestgruppeId(gruppeId);
    }

    @Transactional
    public Testident saveIdentIBruk(String ident, boolean iBruk) {

        Testident testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));

        testident.setIBruk(iBruk);
        return identRepository.save(testident);
    }

    @Transactional
    public Testident saveIdentBeskrivelse(String ident, String beskrivelse) {

        Testident testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));

        testident.setBeskrivelse(beskrivelse);
        return identRepository.save(testident);
    }

    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {

        if (identRepository.findByIdent(newIdent).isPresent()) {
            identRepository.deleteTestidentByIdent(oldIdent);

        } else {
            identRepository.swapIdent(oldIdent, newIdent);
        }
    }

    public List<GruppeBestillingIdent> getBestillingerFromGruppe(Testgruppe gruppe) {

        return identRepository.getBestillingerFromGruppe(gruppe);
    }

    public List<GruppeBestillingIdent> getBestillingerFromIdent(String ident) {

        return identRepository.getBestillingerByIdent(ident);
    }

    public Page<Testident> getTestidenterFromGruppePaginert(Long gruppeId, Integer pageNo, Integer pageSize, String sortColumn, String sortRetning) {
        if (StringUtils.isBlank(sortColumn)) {
            sortColumn = "id";
        }
        var retning = "asc".equals(sortRetning) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var page = PageRequest.of(pageNo, pageSize,
                Sort.by(
                        new Sort.Order(retning, sortColumn, Sort.NullHandling.NULLS_LAST),
                        new Sort.Order(Sort.Direction.DESC, "id", Sort.NullHandling.NULLS_LAST)
                )
        );
        return identRepository
                .getTestidentByTestgruppeIdOrderByBestillingProgressIdDesc(gruppeId, page);
    }

    public Optional<Integer> getPaginertIdentIndex(String ident, Long gruppeId) {

        return identRepository.getPaginertTestidentIndex(ident, gruppeId);
    }

    public Testident getTestIdent(String ident) {

        return identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format(IDENT_NOT_FOUND, ident)));
    }
}
