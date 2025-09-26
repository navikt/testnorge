package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.TransaksjonMapping;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TransaksjonMappingRepository extends ReactiveCrudRepository<TransaksjonMapping, Long> {

    Flux<TransaksjonMapping> findAllBySystemAndIdent(String system, String ident);

    Flux<TransaksjonMapping> findAllByIdentAndMiljoe(String ident, String miljoe);

    Flux<TransaksjonMapping> findAllBySystemAndIdentAndMiljoe(String system, String ident, String miljoe);

    @Query("""
            select * from transaksjon_mapping t
            where t.ident=:ident
            and (:bestillingId is null
            or (t.bestilling_id is not null and t.bestilling_id=:bestillingId))
            """)
    Flux<TransaksjonMapping> findAllByBestillingIdAndIdent(@Param("bestillingId") Long bestillingId, @Param("ident") String ident);

    Flux<TransaksjonMapping> findAllByBestillingId(Long bestillingId);

    @Modifying
    Mono<Void> deleteAllByIdent(String ident);

    @Modifying
    Mono<Void> deleteByIdentAndMiljoeAndSystem(String ident, String miljoe, String system);

    Mono<Void> deleteByIdentAndMiljoeAndSystemAndBestillingId(String ident, String miljoe, String system, Long bestillingId);

    @Modifying
    @Query("""
            delete from Transaksjon_Mapping tm
            where tm.bestilling_id in
            (select b.id from Bestilling b where b.gruppe_id = :gruppeId)
            """)
    Mono<Void> deleteByGruppeId(@Param("gruppeId") Long gruppeId);
}
