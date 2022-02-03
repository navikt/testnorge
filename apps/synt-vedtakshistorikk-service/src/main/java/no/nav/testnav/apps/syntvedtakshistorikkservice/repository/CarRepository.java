package no.nav.testnav.apps.syntvedtakshistorikkservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CarRepository extends ReactiveCrudRepository<CarEntity, String> {

    Flux<CarEntity> findByColor(String color);

    Flux<CarEntity> findByBrand(String brand);

    Flux<CarEntity> findByColorAndBrand(String color, String brand);
}
