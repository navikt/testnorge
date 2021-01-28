package no.nav.registre.testnorge.organisasjonbestillingservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Set;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;
import no.nav.registre.testnorge.organisasjonbestillingservice.domain.Order;
import no.nav.registre.testnorge.organisasjonbestillingservice.service.OrderService;

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

    @GetMapping("/{uuid}/items")
    public ResponseEntity<List<ItemDTO>> getItems(@PathVariable("uuid") String uuid) {
        var items = service.getStatusBy(uuid);
        if (items == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{uuid}/items/{id}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable("id") Long id) {
        var item = service.getStatusBy(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
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
