package no.nav.testnav.apps.organisasjonbestillingservice.repository.v1;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.testnav.apps.organisasjonbestillingservice.repository.v1.model.OrderModel;

public interface OrderRepository extends CrudRepository<OrderModel, Long> {
    @Query(value = "from OrderModel o1 where o1.uuid = ?1")
    List<OrderModel> findBy(String uuid);
}
