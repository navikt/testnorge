package no.nav.testnav.apps.organisasjonbestillingservice.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.service.v1.OrderService;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public ResponseEntity<Set<String>> getOrders() {
        var uuids = service.getOrderUuids();
        return ResponseEntity.ok(uuids);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Long> registerBestilling(@PathVariable("uuid") String uuid) {
        log.info("Oppretter bestilling med uuid: {}", uuid);
        var id = service.create(uuid);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/items/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).body(id);
    }

    @PutMapping("/{uuid}/items/{id}")
    public ResponseEntity<HttpStatus> updateBestilling(@PathVariable("uuid") String uuid, @PathVariable("id") Long id, @RequestBody OrderDTO dto) {
        log.info("Oppdaterer bestilling med uuid: {} og id: {}", uuid, id);
        service.update(new Order(dto, uuid), id);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/items/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("uuid") String uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
