package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrganisasjonBestillingMalRepository extends ReactiveCrudRepository<OrganisasjonBestillingMal, Long> {

    Flux<OrganisasjonBestillingMal> findByBrukerIdAndMalNavn(Long brukerId, String navn);

    Flux<OrganisasjonBestillingMal> findByBrukerId(Long brukerId);

    @Modifying
    @Query("""
            update organisasjon_bestilling_mal b
            set mal_navn = :malNavn where b.id = :id
            """)
    Mono<OrganisasjonBestillingMal> updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

}
