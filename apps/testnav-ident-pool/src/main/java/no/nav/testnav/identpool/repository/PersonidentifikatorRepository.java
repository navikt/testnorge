package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Personidentifikator;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonidentifikatorRepository extends ReactiveCrudRepository<Personidentifikator, Long> {

    @Query("""
            select * from personidentifikator2032 p
            where p.dato_identifikator = :datoIdentifikator
            and p.allokert = :allokert
            and p.individnummer = (select min(individnummer) from personidentifikator2032 pi
                                  where pi.dato_identifikator = :datoIdentifikator
                                  and pi.allokert = :allokert)
            order by p.individnummer desc, p.personidentifikator
            """)
    Flux<Personidentifikator> findAvail(@Param("datoIdentifikator") String datoIdentifikator,
                                        @Param("allokert") boolean allokert);

    Mono<Boolean> existsByDatoIdentifikator(String datoIdentifikator);

    @Modifying
    Mono<Void> deleteByPersonidentifikator(String identifikator);
}
