package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface RelasjonRepository extends ReactiveCrudRepository<DbRelasjon, Long> {

    Mono<DbRelasjon> findByPersonIdAndRelatertPersonIdAndRelasjonType(Long id1, Long id2, RelasjonType relasjonType);

    @Modifying
    @Query("delete from DbRelasjon r " +
           "where r.person.id in (select p.id from DbPerson p where p.ident in (:identer))")
    Mono<Void> deleteByPersonIdentIn(List<String> identer);
}
