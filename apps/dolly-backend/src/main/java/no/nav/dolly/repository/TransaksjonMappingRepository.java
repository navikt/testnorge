package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.TransaksjonMapping;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransaksjonMappingRepository extends CrudRepository<TransaksjonMapping, Long> {

    TransaksjonMapping save(TransaksjonMapping transaksjonMapping);

    Optional<List<TransaksjonMapping>> findAllByIdent(String ident);

    Optional<List<TransaksjonMapping>> findAllBySystemAndIdent(String system, String ident);

    @Query(value = "from TransaksjonMapping t where (:bestillingId is null or " +
            "t.bestillingId is null or " +
            "(t.bestillingId is not null and t.bestillingId=:bestillingId)) and " +
            "t.ident=:ident")
    Optional<List<TransaksjonMapping>> findAllByBestillingIdAndIdent(Long bestillingId, String ident);

    @Modifying
    int deleteAllByIdent(String ident);

    @Modifying
    @Query(value = "delete from TransaksjonMapping tm where tm.bestillingId in " +
                    "(select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);
}