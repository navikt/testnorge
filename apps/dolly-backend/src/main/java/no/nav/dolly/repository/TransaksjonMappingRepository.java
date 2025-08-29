package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.TransaksjonMapping;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransaksjonMappingRepository extends CrudRepository<TransaksjonMapping, Long> {

    List<TransaksjonMapping> findAllBySystemAndIdent(String system, String ident);

    @Query(value = "from TransaksjonMapping t " +
            " where t.ident=:ident" +
            " and (:bestillingId is null " +
            " or (t.bestillingId is not null and t.bestillingId=:bestillingId))")
    List<TransaksjonMapping> findAllByBestillingIdAndIdent(@Param("bestillingId") Long bestillingId, @Param("ident") String ident);

    @Modifying
    int deleteAllByIdent(String ident);

    @Modifying
    int deleteByIdentAndMiljoeAndSystem(String ident, String miljoe, String system);

    int deleteByIdentAndMiljoeAndSystemAndBestillingId(String ident, String miljoe, String system, Long bestillingId);

    @Modifying
    @Query(value = "delete from TransaksjonMapping tm where tm.bestillingId in " +
            "(select b.id from Bestilling b where b.gruppe.id = :gruppeId)")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Query(value = """
            from TransaksjonMapping tm
            where tm.system = 'DOKARKIV'
            AND tm.transaksjonId like '{%'
            order by tm.id
            """)
    Iterable<TransaksjonMapping> findAllByDokarkiv();

    @Query(value = """
            from TransaksjonMapping tm
            where tm.system = 'HISTARK'
            AND tm.transaksjonId like '{%'
            order by tm.id
            """)
    Iterable<TransaksjonMapping> findAllByHistark();
}
