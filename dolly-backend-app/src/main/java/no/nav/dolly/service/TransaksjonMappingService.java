package no.nav.dolly.service;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.repository.TransaksjonMappingRepository;

@Service
@RequiredArgsConstructor
public class TransaksjonMappingService {

    private final TransaksjonMappingRepository transaksjonMappingRepository;

    public List<TransaksjonMapping> getTransaksjonMapping(String system, String ident, Long bestillingId) {

        if (nonNull(ident)) {
            return isNotBlank(system) ? transaksjonMappingRepository.findAllBySystemAndIdent(system, ident).orElse(emptyList()) : transaksjonMappingRepository.findAllByIdent(ident).orElse(emptyList());
        } else if (nonNull(bestillingId)) {
            return isNotBlank(system) ? transaksjonMappingRepository.findAllBySystemAndBestillingId(system, bestillingId).orElse(emptyList())
                    : transaksjonMappingRepository.findAllByBestillingId(bestillingId).orElse(emptyList());
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Søket trenger enten Ident eller BestillingId");
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
