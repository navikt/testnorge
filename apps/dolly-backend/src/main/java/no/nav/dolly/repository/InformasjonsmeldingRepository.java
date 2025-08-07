package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.InfoStripe;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface InformasjonsmeldingRepository extends ReactiveCrudRepository<InfoStripe, Long> {

    @Query("""
            select * from info_stripe i where
            (i.start is null or i.start <= current_timestamp) and
            (i.expires is null or i.expires >= current_timestamp)
            order by i.id desc
            """)
    Flux<InfoStripe> findGyldigMeldinger();
}
