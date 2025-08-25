package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrganisasjonBestillingMalRepository extends ReactiveSortingRepository<OrganisasjonBestillingMal, Long> {

    Flux<OrganisasjonBestillingMal> findByBrukerIdAndMalNavnOrderByMalNavn(Long brukerId, String navn);

    Flux<OrganisasjonBestillingMal> findByBrukerIdOrderByMalNavn(Long brukerId);

    @Modifying
    @Query("""
            update organisasjon_bestilling_mal b
            set mal_navn = :malNavn where b.id = :id
            """)
    Mono<OrganisasjonBestillingMal> updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

    @Modifying
    Mono<OrganisasjonBestillingMal> save(OrganisasjonBestillingMal bestillingMal);

    @Modifying
    Mono<Void> deleteById(Long id);

    Mono<OrganisasjonBestillingMal> findById(Long id);

    Flux<OrganisasjonBestillingMal> findByOrderByMalNavn();
}
