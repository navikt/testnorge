package no.nav.registre.testnorge.organiasjonbestillingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.Status;
import no.nav.registre.testnorge.organiasjonbestillingservice.consumer.EregConsumer;
import no.nav.registre.testnorge.organiasjonbestillingservice.domain.Order;
import no.nav.registre.testnorge.organiasjonbestillingservice.repository.OrderRepository;
import no.nav.registre.testnorge.organiasjonbestillingservice.repository.model.OrderModel;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final EregConsumer consumer;

    public Long register(Order order) {
        return repository
                .findBy(order.getMiljo(), order.getBatchId(), order.getUuid())
                .orElseGet(() -> repository.save(order.toModel()))
                .getId();
    }

    public Set<String> getOrderUuids() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(OrderModel::getUuid)
                .collect(Collectors.toSet());
    }

    public ItemDTO getStatusBy(Long id) {
        var model = repository.findById(id);
        if (model.isEmpty()) {
            return null;
        }
        var order = new Order(model.get());
        var kode = consumer.getStatusKode(order);
        return new ItemDTO(id, toStatus(kode));
    }

    public List<ItemDTO> getStatusBy(String uuid) {
        var list = repository.findBy(uuid);
        return list.stream()
                .map(value -> getStatusBy(value.getId()))
                .collect(Collectors.toList());
    }

    public void delete(String uuid) {
        var orders = repository.findBy(uuid);
        orders.forEach(order -> {
            repository.deleteById(order.getId());
        });
    }

    private Status toStatus(Long kode) {
        if (kode < 0) {
            return Status.RUNNING;
        }
        if (kode == 0) {
            return Status.COMPLETED;
        }
        if (kode < 16) {
            return Status.ERROR;
        }
        return Status.FAILED;
    }
}
