package no.nav.testnav.apps.tenorsearchservice.repository;

import no.nav.testnav.apps.tenorsearchservice.domain.TenorMal;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalUpsertResult;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface TenorMalRepository extends ReactiveSortingRepository<TenorMal, Long> {

    Mono<Void> deleteAll();

    @Query("""
            insert into tenor_mal (
                mal_navn,
                mal_navn_normalisert,
                mal_type,
                soek_kriterier,
                bruker_id,
                opprettet,
                sist_oppdatert
            )
            values (
                :malNavn,
                :malNavnNormalisert,
                :malType,
                :soekKriterier,
                :brukerId,
                current_timestamp,
                current_timestamp
            )
            on conflict (bruker_id, mal_navn_normalisert) do update
            set mal_navn = excluded.mal_navn,
                mal_type = excluded.mal_type,
                soek_kriterier = excluded.soek_kriterier,
                sist_oppdatert = current_timestamp
            returning *, (xmax = 0) as ny
            """)
    Mono<TenorMalUpsertResult> upsert(
            @Param("malNavn") String malNavn,
            @Param("malNavnNormalisert") String malNavnNormalisert,
            @Param("malType") String malType,
            @Param("soekKriterier") String soekKriterier,
            @Param("brukerId") Long brukerId);

    @Query("""
            select *
            from tenor_mal
            where bruker_id in (:brukerIds)
            order by lower(mal_navn), id
            """)
    Flux<TenorMal> findByBrukerIdInOrderByMalNavnAscIdAsc(
            @Param("brukerIds") Collection<Long> brukerIds);

    @Query("""
            select *
            from tenor_mal
            where bruker_id in (:brukerIds)
              and mal_type = :malType
            order by lower(mal_navn), id
            """)
    Flux<TenorMal> findByBrukerIdInAndMalTypeOrderByMalNavnAscIdAsc(
            @Param("brukerIds") Collection<Long> brukerIds,
            @Param("malType") TenorMalType malType);

    Mono<Boolean> existsByBrukerIdAndMalNavnNormalisertAndIdNot(
            Long brukerId,
            String malNavnNormalisert,
            Long id);

    @Modifying
    @Query("""
            update tenor_mal
            set mal_navn = :malNavn,
                mal_navn_normalisert = :malNavnNormalisert,
                sist_oppdatert = current_timestamp
            where id = :id
              and bruker_id = :brukerId
            """)
    Mono<Integer> updateMalNavn(
            @Param("id") Long id,
            @Param("brukerId") Long brukerId,
            @Param("malNavn") String malNavn,
            @Param("malNavnNormalisert") String malNavnNormalisert);

    @Modifying
    @Query("""
            delete from tenor_mal
            where id = :id
              and bruker_id = :brukerId
            """)
    Mono<Integer> deleteByIdAndBrukerId(
            @Param("id") Long id,
            @Param("brukerId") Long brukerId);

    Mono<TenorMal> findByIdAndBrukerId(Long id, Long brukerId);
}
