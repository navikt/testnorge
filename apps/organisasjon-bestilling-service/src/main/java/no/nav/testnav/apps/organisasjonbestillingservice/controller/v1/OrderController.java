package no.nav.testnav.apps.organisasjonbestillingservice.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.service.v1.OrderService;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public Mono<Set<String>> getOrders() {
        return service.getOrderUuids();
    }

    @PutMapping("/{uuid}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Long> registerBestilling(@PathVariable("uuid") String uuid) {
        log.info("Oppretter bestilling med uuid: {}", uuid);
        return service.create(uuid);
    }

    @PutMapping("/{uuid}/items/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Long> updateBestilling(@PathVariable("uuid") String uuid, @PathVariable("id") Long id, @RequestBody OrderDTO dto) {
        log.info("Oppdaterer bestilling med uuid: {} og id: {}", uuid, id);
        return service.update(new Order(dto, uuid), id);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("uuid") String uuid) {
        return service.delete(uuid);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAll() {
        return service.deleteAll();
    }
}
