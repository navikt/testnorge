package no.nav.dolly.repository.oracle;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.oracle.OraTransaksjonMapping;

public interface OraTransaksjonMappingRepository extends PagingAndSortingRepository<OraTransaksjonMapping, Long> {

    @Query("from OraTransaksjonMapping otm where otm.bestillingId is null order by otm.id")
    Iterable<OraTransaksjonMapping> findAllByBestIdIsNullOrderByIdAsc();
}