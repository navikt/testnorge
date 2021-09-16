package no.nav.testnav.apps.organisasjonbestillingservice.service.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.nav.testnav.apps.organisasjonbestillingservice.consumer.EregBatchStatusConsumer;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.OrderRepository;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.model.OrderModel;
import no.nav.testnav.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.dto.organiasjonbestilling.v1.Status;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final EregBatchStatusConsumer consumer;

    public Long create(String uuid) {
        return repository.save(OrderModel.builder().uuid(uuid).build()).getId();
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
        log.info("Henter status for uuid: {}", uuid);
        var list = repository.findBy(uuid);
        return list.stream()
                .map(value -> getStatusBy(value.getId()))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(String uuid) {
        repository.findBy(uuid).forEach(value -> repository.deleteById(value.getId()));
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
