package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;
    private final MapperFacade mapperFacade;

    @Transactional(readOnly = true)
    public List<RsTransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        return transaksjonMappingRepository.findAllByBestillingIdAndIdent(bestillingId, ident).stream()
                .filter(transaksjon -> isNull(system) || system.equals(transaksjon.getSystem()))
                .map(transasjon -> mapperFacade.map(transasjon, RsTransaksjonMapping.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RsTransaksjonMapping> getTransaksjonMapping(String ident) {

        return transaksjonMappingRepository.findAllByBestillingIdAndIdent(null, ident).stream()
                .map(transasjon -> mapperFacade.map(transasjon, RsTransaksjonMapping.class))
                .toList();
    }

    public boolean existAlready(SystemTyper system, String ident, String miljoe, Long bestillingId) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .stream()
                .anyMatch(mapping -> (isBlank(miljoe) || miljoe.equals(mapping.getMiljoe())) &&
                        (isNull(bestillingId) || bestillingId.equals(mapping.getBestillingId())));
    }

    public void saveAll(Collection<TransaksjonMapping> entries) {

        entries.forEach(this::save);
    }

    @Transactional
    public void save(TransaksjonMapping entry) {

        transaksjonMappingRepository.save(entry);
    }

    @Transactional
    public void slettTransaksjonMappingByTestident(String ident) {

        transaksjonMappingRepository.deleteAllByIdent(ident);
    }
}
