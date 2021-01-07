package no.nav.registre.testnorge.organisasjonbestillingservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.organisasjonbestillingservice.repository.model.OrderModel;

public interface OrderRepository extends CrudRepository<OrderModel, Long> {

    @Query(value = "from OrderModel o1 where o1.miljo = ?1 AND o1.batchId = ?2 AND o1.uuid = ?3")
    Optional<OrderModel> findBy(String miljo, Long id, String uuid);

    @Query(value = "from OrderModel o1 where o1.uuid = ?1")
    Optional<OrderModel> findBy(String uuid);
}
