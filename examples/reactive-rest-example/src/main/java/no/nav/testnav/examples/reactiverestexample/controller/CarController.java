package no.nav.testnav.examples.reactiverestexample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import no.nav.testnav.examples.reactiverestexample.controller.dto.CarDTO;
import no.nav.testnav.examples.reactiverestexample.domain.Car;
import no.nav.testnav.examples.reactiverestexample.service.CarService;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public Flux<CarDTO> getCars(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String brand
    ) {
        return carService.getCarsFrom(color, brand).map(Car::toDTO);
    }

    @PostMapping
    public Mono<ResponseEntity<CarDTO>> createCar(@RequestBody CarDTO dto, ServerHttpRequest serverHttpRequest) {
        return carService
                .createCar(new Car(dto))
                .map(Car::toDTO)
                .map(value -> ResponseEntity
                        .created(URI.create(serverHttpRequest.getURI() + "/" + value.regnumber()))
                        .body(value)
                );
    }

    @GetMapping("/{regnumber}")
    public Mono<ResponseEntity<CarDTO>> getCar(@PathVariable String regnumber) {
        return carService.getCar(regnumber)
                .map(Car::toDTO)
                .map(ResponseEntity::ok);
    }

}
