package no.nav.dolly.service;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;

@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;

    public List<TransaksjonMapping> getTransaksjonMapping(SystemTyper system, String ident) {

        return isNotBlank(system.name()) ?
                transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident).orElse(emptyList()) :
                transaksjonMappingRepository.findAllByIdent(ident).orElse(emptyList());
    }

    public boolean existAlready(SystemTyper system, String ident, String miljoe) {

        return transaksjonMappingRepository.findAllBySystemAndIdent(system.name(), ident)
                .orElse(emptyList())
                .stream()
                .anyMatch(mapping -> miljoe.equals(mapping.getMiljoe()));
    }

    public void saveAll(Collection<TransaksjonMapping> entries) {

        entries.forEach(this::save);
    }

    @Transactional
    public void save(TransaksjonMapping entry) {

        transaksjonMappingRepository.save(entry);
    }
}
