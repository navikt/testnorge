package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import no.nav.dolly.domain.jpa.TransaksjonMapping;

public interface TransaksjonMappingRepository extends Repository<TransaksjonMapping, Long> {

    TransaksjonMapping save(TransaksjonMapping transaksjonMapping);

    Optional<List<TransaksjonMapping>> findAllByIdent(String ident);

    Optional<List<TransaksjonMapping>> findAllBySystemAndIdent(String system, String ident);

    Optional<List<TransaksjonMapping>> findAllByBestillingId(Long bestillingId);

    Optional<List<TransaksjonMapping>> findAllBySystemAndBestillingId(String system, Long bestillingId);
}