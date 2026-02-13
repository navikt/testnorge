package no.nav.testnav.apps.organisasjonbestillingservice.controller.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v2.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.service.v2.OrderServiceV2;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v2/order")
@RequiredArgsConstructor
public class OrderControllerV2 {
    private final OrderServiceV2 service;

    @GetMapping
    public Mono<List<OrderDTO>> getAll() {
        return service.findAll()
                .map(list -> list.stream().map(Order::toDTO).collect(Collectors.toList()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDTO> opprett(@RequestBody OrderDTO dto) {
        return service.save(new Order(dto))
                .map(Order::toDTO);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String uuid) {
        return service.delete(uuid);
    }

    @GetMapping("/{uuid}/ids")
    public Mono<Set<Long>> getIds(@PathVariable String uuid) {
        return service.find(uuid)
                .map(list -> list.stream().map(Order::getId).collect(Collectors.toSet()));
    }

    @GetMapping("/{uuid}/ids/{id}/status")
    public Mono<StatusDTO> getStatus(@PathVariable String uuid, @PathVariable Long id) {
        return service.getStatus(id)
                .map(StatusDTO::new);
    }

}
