package no.nav.registre.testnorge.organisasjonbestillingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.Status;
import no.nav.registre.testnorge.organisasjonbestillingservice.consumer.EregBatchStatusConsumer;
import no.nav.registre.testnorge.organisasjonbestillingservice.domain.Order;
import no.nav.registre.testnorge.organisasjonbestillingservice.repository.OrderRepository;
import no.nav.registre.testnorge.organisasjonbestillingservice.repository.model.OrderModel;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final EregBatchStatusConsumer consumer;

    public Long create(String uuid) {
        return repository
                .findBy(uuid)
                .map(OrderModel::getId)
                .orElseGet(() -> repository.save(OrderModel.builder().uuid(uuid).build()).getId());
    }


    public Long update(Order order, Long id) {
        return repository.save(order.toModel(id)).getId();
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
        var value = model.get();
        if (value.getBatchId() == null) {
            return new ItemDTO(id, Status.NOT_STARTED);
        }
        var order = new Order(value);
        var kode = consumer.getStatusKode(order);
        return new ItemDTO(id, toStatus(kode));
    }

    public List<ItemDTO> getStatusBy(String uuid) {
        var list = repository.findBy(uuid);
        return list.stream()
                .map(value -> getStatusBy(value.getId()))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(String uuid) {
        repository.findBy(uuid).ifPresent(value -> repository.deleteById(value.getId()));
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
