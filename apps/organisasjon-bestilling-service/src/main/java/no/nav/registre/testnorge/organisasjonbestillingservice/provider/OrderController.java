package no.nav.registre.testnorge.organisasjonbestillingservice.provider;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public ResponseEntity<Set<String>> getOrders(){
        var uuids = service.getOrderUuids();
        return ResponseEntity.ok(uuids);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<HttpStatus> registerBestilling(@PathVariable("uuid") String uuid, @RequestBody OrderDTO dto) {
        var id = service.register(new Order(dto, uuid));
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
}
