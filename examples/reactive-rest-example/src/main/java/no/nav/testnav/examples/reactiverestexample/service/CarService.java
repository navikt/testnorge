package no.nav.testnav.examples.reactiverestexample.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import no.nav.testnav.examples.reactiverestexample.domain.Car;
import no.nav.testnav.examples.reactiverestexample.repository.CarRepository;

@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public Mono<Car> createCar(Car car) {
        return repository.save(car.toEntity(true)).map(Car::new);
    }

    public Mono<Car> getCar(String registrationPlate) {
        return repository.findById(registrationPlate).map(Car::new);
    }

    public Flux<Car> getCarsFrom(String color, String brand) {
        if (color != null && brand != null) {
            return repository.findByColorAndBrand(color, brand).map(Car::new);
        } else if (color != null) {
            return repository.findByColor(color).map(Car::new);
        } else if (brand != null) {
            return repository.findByBrand(brand).map(Car::new);
        }
        return repository.findAll().map(Car::new);
    }
}
