package no.nav.testnav.apps.organisasjonbestillingservice.service.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.domain.v1.Order;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.OrderRepository;
import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.model.OrderModel;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.Status;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

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
