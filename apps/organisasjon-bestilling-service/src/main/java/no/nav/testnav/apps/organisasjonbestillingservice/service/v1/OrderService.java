package no.nav.testnav.apps.organisasjonbestillingservice.service.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.OrderRepository;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.model.OrderModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public Mono<Long> create(String uuid) {
        return Mono.fromCallable(() -> repository.save(OrderModel.builder().uuid(uuid).build()).getId())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Long> update(Order order, Long id) {
        return Mono.fromCallable(() -> repository.save(order.toModel(id)).getId())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Set<String>> getOrderUuids() {
        return Mono.fromCallable(() -> StreamSupport
                        .stream(repository.findAll().spliterator(), false)
                        .map(OrderModel::getUuid)
                        .collect(Collectors.toSet()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteAll() {
        return Mono.fromRunnable(() -> repository.deleteAll())
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> delete(String uuid) {
        return Mono.fromRunnable(() -> repository.findBy(uuid).forEach(value -> repository.deleteById(value.getId())))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
