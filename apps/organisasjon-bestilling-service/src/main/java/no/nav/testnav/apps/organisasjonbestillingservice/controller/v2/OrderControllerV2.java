package no.nav.testnav.apps.organisasjonbestillingservice.controller.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.service.v2.OrderServiceV2;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("/api/v2/order")
@RequiredArgsConstructor
public class OrderControllerV2 {
    private final OrderServiceV2 service;


    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAll() {
        var list = service.findAll().stream().map(Order::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> opprett(
            @RequestBody OrderDTO dto
    ) {
        var order = service.save(new Order(dto));

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/items/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity.created(uri).body(order.toDTO());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<HttpStatus> delete(@PathVariable String uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/ids")
    public ResponseEntity<Set<Long>> getIds(@PathVariable String uuid) {
        Set<Long> ids = service.find(uuid).stream().map(Order::getId).collect(Collectors.toSet());
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/{uuid}/ids/{id}/status")
    public ResponseEntity<StatusDTO> getStatus(@PathVariable String uuid, @PathVariable Long id) {
        var status = service.getStatus(id);
        if (isNull(status)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new StatusDTO(status));
    }

}
