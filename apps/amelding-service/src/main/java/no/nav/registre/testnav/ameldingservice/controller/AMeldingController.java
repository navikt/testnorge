package no.nav.registre.testnav.ameldingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import no.nav.registre.testnav.ameldingservice.domain.AMelding;
import no.nav.registre.testnav.ameldingservice.service.AMeldingService;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amelding")
public class AMeldingController {
    private final AMeldingService service;

    @PutMapping
    public ResponseEntity<?> save(@RequestHeader String miljo, @RequestBody AMeldingDTO dto) {
        var id = service.save(new AMelding(dto), miljo);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).header("ID", id).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id)
                .map(value -> ResponseEntity.ok(value.toDTO()))
                .orElse(ResponseEntity.notFound().build());
    }

}
