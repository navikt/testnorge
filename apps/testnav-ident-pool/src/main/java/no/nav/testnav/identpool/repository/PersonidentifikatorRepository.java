package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Ident2032;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonidentifikatorRepository extends ReactiveCrudRepository<Ident2032, Long> {

    @Query("""
            select * from personidentifikator2032 p
            where p.dato_identifikator = :datoIdentifikator
            and p.allokert = false
            and p.individnummer = (select min(individnummer) from personidentifikator2032 pi
                                  where pi.dato_identifikator = :datoIdentifikator
                                  and pi.allokert = false)
            order by p.individnummer desc, p.personidentifikator
            """)
    Flux<Ident2032> findLedige(@Param("datoIdentifikator") String datoIdentifikator);

    @Query("""
            select * from personidentifikator2032 p
            where p.dato_identifikator = :datoIdentifikator
            and p.individnummer = (select min(individnummer) from personidentifikator2032 pi
                                  where pi.dato_identifikator = :datoIdentifikator)
            order by p.individnummer desc, p.personidentifikator
            """)
    Flux<Ident2032> findAvail(@Param("datoIdentifikator") String datoIdentifikator);

    Mono<Boolean> existsByDatoIdentifikatorAndAllokert(String datoIdentifikator, boolean allokert);

    Mono<Ident2032> findByPersonidentifikator(String personidentifikator);

    @Modifying
    @Query("""
            update personidentifikator2032
            set allokert = false
            where personidentifikator = :personidentifikator;
            """)
    Mono<Void> releaseByPersonidentifikator(String identifikator);
}
