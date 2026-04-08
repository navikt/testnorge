package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Dokument;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface DokumentRepository extends ReactiveCrudRepository<Dokument, Long> {

    Flux<Dokument> getDokumentsByBestillingId(Long bestillingId);

    Flux<Dokument> getDokumentsByIdIsIn(Collection<Long> dokumentIdListe);

    @Modifying
    @Query("UPDATE dokument SET bestilling_id = :bestillingId, sist_oppdatert = NOW() WHERE id = :id AND bestilling_id IS NULL")
    Mono<Integer> updateBestillingIdIfNull(Long id, Long bestillingId);

    @Modifying
    @Query("UPDATE dokument SET contents = COALESCE(contents, '') || :chunk, sist_oppdatert = NOW() WHERE id = :id")
    Mono<Integer> appendContent(Long id, String chunk);
}