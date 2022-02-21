package no.nav.registre.testnav.ameldingservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.ameldingservice.domain.AMelding;
import no.nav.registre.testnav.ameldingservice.service.AMeldingService;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amelding")
public class AMeldingController {
    private final AMeldingService service;

    @PutMapping
    public Mono<ResponseEntity<HttpStatus>> save(
            @RequestHeader String miljo,
            @RequestBody AMeldingDTO dto,
            ServerHttpRequest serverHttpRequest
    ) {
        return service
                .save(new AMelding(dto), miljo)
                .map(id -> ResponseEntity
                        .created(URI.create(serverHttpRequest.getURI() + "/" + id))
                        .header("ID", id)
                        .build()
                );
    }

    @PutMapping
    public Mono<ResponseEntity<HttpStatus>> saveAll(
            @RequestHeader String miljo,
            @RequestBody AMeldingDTO dto,
            ServerHttpRequest serverHttpRequest
    ) {
        return service
                .save(new AMelding(dto), miljo)
                .map(id -> ResponseEntity
                        .created(URI.create(serverHttpRequest.getURI() + "/" + id))
                        .header("ID", id)
                        .build()
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AMeldingDTO>> get(@PathVariable("id") String id) {
        return service.get(id)
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
